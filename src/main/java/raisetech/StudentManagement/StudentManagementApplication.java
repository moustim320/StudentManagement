package raisetech.StudentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import raisetech.StudentManagement.data.Course;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.repository.StudentRepository;

import java.util.List;

@SpringBootApplication
public class StudentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentManagementApplication.class, args);
	}
/*
	//GET と POST
	//GETは取得する、リクエストの結果を受け取る　curlは裏ではGET
	//POSTは情報を与える、渡す
*/

	//コード変更を行った体でPRテスト
}
