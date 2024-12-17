package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生")
@Getter
@Setter
public class Student {

    @Pattern(regexp = "^\\d+$", message = "数字のみ入力するようにしてください。")
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String kanaName;

    @NotBlank
    private String nickname;

    @NotBlank
    @Email
    private String mailAddress;

    @NotBlank
    private String address;

    @Min(value = 0, message = "年齢は0以上である必要があります。")
    private int age;

    @Pattern(regexp = "^(男|女|その他)$", message = "性別は「男」「女」「その他」から選択してください。")
    private String gender;

    private String remark;
    private boolean isDeleted = false;
}

