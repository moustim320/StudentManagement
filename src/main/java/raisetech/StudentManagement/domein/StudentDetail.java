package raisetech.StudentManagement.domein;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

import java.util.List;

@Schema(description = "受講生")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {

    @Valid
    private Student student;

    @Valid
    private List<StudentCourse> studentCourseList;

}
