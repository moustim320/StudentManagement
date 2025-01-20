package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "仮申込|本申込|受講中|受講終了", message = "無効なステータス値です")
    private String status;

}
