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
}
