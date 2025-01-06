package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コースのステータス情報")
@Getter
@Setter
public class CourseStatus {

    private String id;

    @NotNull
    private String studentsCoursesId;

    @NotNull
    private String status;

}
