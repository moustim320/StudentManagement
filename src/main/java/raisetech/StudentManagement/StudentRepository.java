package raisetech.StudentManagement;

import org.apache.ibatis.annotations.*;

import java.util.List;

//データベースを操作するためのもの
@Mapper
public interface StudentRepository {

    @Select("SELECT * FROM students")
    List<Student> search();

    @Select("SELECT * FROM students_courses")
    List<Course> explore();
}
