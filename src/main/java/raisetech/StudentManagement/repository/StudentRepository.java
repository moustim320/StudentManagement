package raisetech.StudentManagement.repository;
import org.apache.ibatis.annotations.*;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.data.Student;
import java.util.List;
/**
 * 受講生情報を扱うリポジトリ
 *
 * 全件検索や単一条件での検索、コース情報の検索が行えるクラス
 */
//データベースを操作するためのもの
@Mapper
public interface StudentRepository {
    /**
     * 全件検索します
     *
     * @return　全件検索した受講生情報の一覧
     */
    @Select("SELECT * FROM students")
    List<Student> search();

    @Select("SELECT * FROM students WHERE id = #{id}")
    Student searchStudent(String id);

    @Select("SELECT * FROM students_courses")
    List<StudentsCourses> searchStudentsCourseList();

    @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourses> searchStudentsCourses(String studentId);

    @Insert("INSERT INTO students(name, kana_name, nickname, mail_address, address, age, gender, remark, isDeleted)" +
            " VALUES(#{name}, #{kanaName}, #{nickname}, #{mailAddress}, #{address}, #{age}, #{gender}, #{remark}, false)")

    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudent(Student student);

    @Insert("INSERT INTO students_courses(student_id, course_name, course_start_at, course_end_at)" +
            "VALUES(#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})")

    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudentsCourses(StudentsCourses studentsCourses);

    @Update("UPDATE students SET name = #{name}, kana_name = #{kanaName}, nickname = #{nickname}, " +
            "mail_address = #{mailAddress}, address = #{address}, age = #{age}, gender = #{gender}, remark = #{remark}, isDeleted = #{isDeleted} WHERE id = #{id}")
    void updateStudent(Student student);

    @Update("UPDATE students_courses SET course_name = #{courseName}  WHERE id = #{id}")
    void updateStudentsCourses(StudentsCourses studentsCourses);
}
