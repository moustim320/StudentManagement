package raisetech.StudentManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.data.Course;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.service.StudentService;

import java.util.List;

@RestController
public class StudentController {

    private StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    //データベースからデータを取ってくる
    //select
    @GetMapping("/studentList")
    public List<Student> getStudentList() {
        return service.serchStudentList();
    }

    @GetMapping("/studentCourseList")
    public List<Course> getStudentCourseList(){
        return service.serchStudentCourseList();
    }

}
