package raisetech.StudentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

	@Autowired
	private StudentRepository repository;


	public static void main(String[] args) {
		SpringApplication.run(StudentManagementApplication.class, args);
	}
/*
	@GetMapping("/name")
	public String getName() {
		return name;
	}

	@GetMapping("/age")
	public String getAge() {
		return age;
	}

	//GET と POST
	//GETは取得する、リクエストの結果を受け取る　curlは裏ではGET
	//POSTは情報を与える、渡す

	@PostMapping("/name")
	public void setName(String name){
		this.name = name;
	}
*/
	//データベースからデータを取ってくる
	//select
	@GetMapping("/studentList")
	public List<Student> getStudentList() {
		 return repository.search();
	}

	@GetMapping("/studentCourseList")
	public List<Course> getStudentCourseList(){
		return repository.explore();
	}

}
