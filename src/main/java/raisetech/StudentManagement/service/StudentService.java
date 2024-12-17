package raisetech.StudentManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domein.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.List;

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
        return converter.convertStudentDetails(studentList, studentCourseList);
    }

    /**
     * 受講生詳細検索です。
     * IDに紐づく受講生情報を取得した後、その受講生に紐づく受講生コース情報を取得して設定します。
     * @param id 受講生ID
     * @return 受講生詳細
     */
    public StudentDetail searchStudent(String id){
        Student student = repository.searchStudent(id);
        List<StudentCourse> studentCourse = repository.searchStudentCourse(student.getId());
        return new StudentDetail(student, studentCourse);
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
        });
        return studentDetail;
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
        //新規登録時の初期ステータスを設定
        studentCourse.setStatus("仮申込");
    }

    /**
     * 受講生詳細の更新を行います。
     * 受講生と受講生コース情報をそれぞれ更新します。
     * @param studentDetail 受講生詳細
     */
    @Transactional
    public void updateStudent(StudentDetail studentDetail) {
        repository.updateStudent(studentDetail.getStudent());
        studentDetail.getStudentCourseList()
                .forEach(studentCourse -> repository.updateStudentCourse(studentCourse));
    }

    public void updateCourseStatus(StudentCourse course, String newStatus) {
        String currentStatus = course.getStatus();
        if (currentStatus.equals("仮申込") && newStatus.equals("本申込")) {
            course.setStatus(newStatus);
        } else if (currentStatus.equals("本申込") && newStatus.equals("受講中")) {
            course.setStatus(newStatus);
        } else if (currentStatus.equals("受講中") && newStatus.equals("受講終了")) {
            course.setStatus(newStatus);
        } else {
            throw new IllegalStateException("無効なステータス遷移：" + currentStatus + "->" + newStatus);
        }
    }

}
