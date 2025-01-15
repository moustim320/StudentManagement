package raisetech.StudentManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.CourseStatus;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domein.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 受講生情報を取り扱うサービスです。
 * 受講生の検索や登録・更新処理を行います。
 */
@Service
public class StudentService {

    private StudentRepository repository;
    private StudentConverter converter;

    @Autowired
    public StudentService(StudentRepository repository, StudentConverter converter){
        this.repository = repository;
        this.converter = converter;
    }

    /**
     * 受講生詳細の検索です。
     * 条件が指定されない場合は全件検索を行います。
     *
     * @param name 受講生の名前
     * @param courseName コース名
     * @param startDate 受講開始日
     * @param endDate 受講終了日
     * @return 条件に合致した、または全件の受講生一覧
     */
    public List<StudentDetail> searchStudentList(String name, String courseName, LocalDateTime startDate, LocalDateTime endDate, String status) {
        List<Student> studentList = repository.searchWithConditions(name);
        List<StudentCourse> studentCourseList = repository.searchStudentCourseListWithConditions(courseName, startDate, endDate, status);
        List<CourseStatus> courseStatusList = repository.searchCourseStatusListWithConditions(status);

        return converter.convertStudentDetails(studentList, studentCourseList, courseStatusList);
    }

    /**
     * 受講生詳細検索です。
     * IDに紐づく受講生情報を取得した後、その受講生に紐づく受講生コース情報を取得して設定します。
     * @param id 受講生ID
     * @return 受講生詳細
     */
    public StudentDetail searchStudent(String id){
        Student student = repository.searchStudent(id);
        List<StudentCourse> studentCourseList = repository.searchStudentCourse(student.getId());
        List<CourseStatus> courseStatusList = repository.searchCourseStatusByStudentId(student.getId());

        // StudentCourseにCourseStatusを紐付ける
        studentCourseList.forEach(studentCourse -> {
            List<CourseStatus> matchedStatusList = courseStatusList.stream()
                    .filter(status -> studentCourse.getId().equals(String.valueOf(status.getStudentsCoursesId())))
                    .collect(Collectors.toList());
            studentCourse.setCourseStatusList(matchedStatusList);
        });

        return new StudentDetail(student, studentCourseList);
    }

    /**
     * 受講生詳細の登録を行います。
     * 受講生と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を紐づける値とコース開始日、コース終了日を設定します。
     * @param studentDetail 受講生詳細
     * @return 登録情報を付与した受講生詳細
     */
    @Transactional
    public StudentDetail registerStudent(StudentDetail studentDetail) {
        Student student = studentDetail.getStudent();
        repository.registerStudent(student);

        studentDetail.getStudentCourseList().forEach(studentCourse -> {
            initStudentsCourse(studentCourse, student.getId());
            repository.registerStudentCourse(studentCourse);

            // 初期ステータスをCourseStatusに登録
            CourseStatus initialStatus = new CourseStatus();
            initialStatus.setStudentsCoursesId(String.valueOf(studentCourse.getId()));
            initialStatus.setStatus("仮申込");
            repository.registerCourseStatus(initialStatus);
        });

        return studentDetail;
    }

    /**
     * コースステータスの登録を行います。
     * @param courseStatus 登録するコースステータス
     * @throws IllegalArgumentException 引数が無効な場合
     */
    public void registerCourseStatus(CourseStatus courseStatus) {
        // Validation: studentsCoursesId と status の必須チェック
        if (courseStatus.getStudentsCoursesId() == null || courseStatus.getStudentsCoursesId().isEmpty()) {
            throw new IllegalArgumentException("コースステータスの登録に必要な 'studentsCoursesId' が指定されていません。");
        }
        if (courseStatus.getStatus() == null || courseStatus.getStatus().isEmpty()) {
            throw new IllegalArgumentException("コースステータスの登録に必要な 'status' が指定されていません。");
        }

        // コースステータスをリポジトリに登録
        repository.registerCourseStatus(courseStatus);
    }


    /**
     * 受講生コース情報を登録する際の初期情報を設定する。
     *
     * @param studentCourse 受講生コース情報
     * @param id 受講生
     */
    void initStudentsCourse(StudentCourse studentCourse, String id) {
        LocalDateTime now = LocalDateTime.now();

        studentCourse.setStudentId(String.valueOf(id));
        studentCourse.setCourseStartAt(now);
        studentCourse.setCourseEndAt(now.plusYears(1));
    }

    /**
     * 受講生詳細の更新を行います。
     * 受講生と受講生コース情報をそれぞれ更新します。
     * @param studentDetail 受講生詳細
     */
    @Transactional
    public void updateStudent(StudentDetail studentDetail) {
        repository.updateStudent(studentDetail.getStudent());
        studentDetail.getStudentCourseList().forEach(studentCourse -> {
                repository.updateStudentCourse(studentCourse);
                studentCourse.getCourseStatusList().forEach(repository::updateCourseStatus);
        });
    }

    /**
     * 新しいステータスに更新します。
     * ただし、有効なステータス遷移のみ許可されます。
     * @param courseId 更新対象のコースID
     * @param newStatus 更新後のステータス
     * @throws IllegalStateException 無効なステータス遷移が試みられた場合
     */
    @Transactional
    public void updateCourseStatus(String courseId, String newStatus) {
        // コース情報をリポジトリから取得
        CourseStatus courseStatus = repository.findLatestCourseStatusByCourseId(courseId);
        if (courseStatus == null) {
            throw new NoSuchElementException("指定されたコースステータスが見つかりません: " + courseId);
        }
        // 現在のステータスをチェックして更新
        String currentStatus = courseStatus.getStatus();
        if ((currentStatus.equals("仮申込") && newStatus.equals("本申込"))||
            (currentStatus.equals("本申込") && newStatus.equals("受講中"))||
            (currentStatus.equals("受講中") && newStatus.equals("受講終了"))){
            courseStatus.setStatus(newStatus);
            repository.updateCourseStatus(courseStatus);
        } else {
            throw new IllegalStateException("無効なステータス遷移：" + currentStatus + "->" + newStatus);
        }
    }

}
