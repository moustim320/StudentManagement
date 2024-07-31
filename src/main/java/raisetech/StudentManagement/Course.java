package raisetech.StudentManagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Course {
    private String id;
    private String studentId;
    private String courseName;
    private String courseStartAt;
    private String courseEndAt;
}
