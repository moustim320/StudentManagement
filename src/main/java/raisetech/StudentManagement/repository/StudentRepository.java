package raisetech.StudentManagement.repository;
import org.apache.ibatis.annotations.*;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.Student;
import java.util.List;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

    /**
     * 受講生の全件検索を行います。
     * @return 受講生一覧（全件）
     */
    @Select("SELECT * FROM students")
    List<Student> search();

    /**
     * 受講生の検索を行います。
     * @param id 受講生ID
     * @return 受講生
     */
    @Select("SELECT * FROM students WHERE id = #{id}")
    Student searchStudent(String id);

    /**
     * 受講生のコース情報の全件検索を行います。
     * @return 受講生のコース情報（全件）
     */
    @Select("SELECT * FROM students_courses")
    List<StudentCourse> searchStudentCourseList();

    /**
     * 受講生IDに紐づく受講生コース情報を検索します。
     * @param studentId 受講生ID
     * @return 受講生IDに紐づく受講生コース情報
     */
    @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
    List<StudentCourse> searchStudentCourse(String studentId);

    /**
     * 受講生を新規登録します。
     * IDは自動採番を行う。
     *
     * @param student 受講生
     */
    @Insert("INSERT INTO students(name, kana_name, nickname, mail_address, address, age, gender, remark, isDeleted)" +
            " VALUES(#{name}, #{kanaName}, #{nickname}, #{mailAddress}, #{address}, #{age}, #{gender}, #{remark}, false)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudent(Student student);

    /**
     * 受講生コース情報を新規登録します。
     * IDは自動採番を行う。
     *
     * @param studentCourse 受講生コース情報
     */
    @Insert("INSERT INTO students_courses(student_id, course_name, course_start_at, course_end_at)" +
            "VALUES(#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudentCourse(StudentCourse studentCourse);

    /**
     * 受講生を更新します。
     * @param student 受講生
     */
    @Update("UPDATE students SET name = #{name}, kana_name = #{kanaName}, nickname = #{nickname}, " +
            "mail_address = #{mailAddress}, address = #{address}, age = #{age}, gender = #{gender}, remark = #{remark}, isDeleted = #{isDeleted} WHERE id = #{id}")
    void updateStudent(Student student);

    /**
     * 受講生コース情報のコース名を更新します。
     * @param studentCourse 受講生コース情報
     */
    @Update("UPDATE students_courses SET course_name = #{courseName}  WHERE id = #{id}")
    void updateStudentCourse(StudentCourse studentCourse);
}
