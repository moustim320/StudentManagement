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
    @Select("SELECT * FROM students_courses")
    List<StudentsCourses> explore();
    @Insert("INSERT INTO students(name, kana_name, nickname, mail_address, address, age, gender, remark, isDeleted)" +
            " VALUES(#{name}, #{kanaName}, #{nickname}, #{mailAddress}, #{address}, #{age}, #{gender}, #{remark}, false)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudent(Student student);

    @Insert("INSERT INTO students_courses(student_id, course_name, course_start_at, course_end_at)" +
            "VALUES(#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudentsCourses(StudentsCourses studentsCourses);
}
