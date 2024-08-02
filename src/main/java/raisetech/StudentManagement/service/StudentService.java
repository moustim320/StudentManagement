package raisetech.StudentManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.StudentManagement.data.Course;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private StudentRepository repository;

    @Autowired
    public StudentService(StudentRepository repository){
        this.repository = repository;
    }

    public List<Student> serchStudentList(){
        //検索処理
        repository.search();

        //絞り込みをする。年齢が30代の人のみ抽出する。
        //抽出したリストをコントローラーに渡す。
        return repository.search()
                .stream().filter(student -> student.getAge() >= 41)
                .collect(Collectors.toList());
    }

    public List<Course> serchStudentCourseList(){
        //絞り込み検索で「・・コース」のコース情報のみを抽出する。
        //抽出したリストをコントローラーに渡す。
        return repository.explore()
                .stream().filter(course -> course.getCourseName().equalsIgnoreCase("Aコース"))
                .collect(Collectors.toList());
    }

}
