package raisetech.StudentManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domein.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StudentController {
    private StudentService service;
    private StudentConverter converter;

    @Autowired
    public StudentController(StudentService service, StudentConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @GetMapping("/studentList")
    public List<StudentDetail> getStudentList() {
        List<Student> students = service.searchStudentList();
        List<StudentsCourses> studentsCourses = service.searchStudentsCourseList();
        return converter.convertStudentDetails(students, studentsCourses);
        }

    /*不要
    @GetMapping("/student/{id}")
    public String getStudent(@PathVariable String id, Model model){
        StudentDetail studentDetail = service.searchStudent(id);
        model.addAttribute("studentDetail", studentDetail);
        return "updateStudent";
    }
    */
    /*不要
    @GetMapping("/studentsCourseList")
    public List<StudentsCourses> getStudentsCourseList(){
        return service.searchStudentsCourseList();
    }
    */
    @GetMapping("/newStudent")
    public String newStudent(Model model){
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudentsCourses(Arrays.asList(new StudentsCourses()));
        model.addAttribute("studentDetail", studentDetail);
        return "registerStudent";
    }

    @PostMapping("/registerStudent")
    public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result){
        if(result.hasErrors()){
            return "registerStudent";
        }
        service.registerStudent(studentDetail);
        return "redirect:/studentList";
    }


    @PostMapping("/updateStudent")
    public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail){
        service.updateStudent(studentDetail);
        return ResponseEntity.ok("更新処理が成功しました。");
    }

}
