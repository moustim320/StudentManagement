package raisetech.StudentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raisetech.StudentManagement.domein.StudentDetail;
import raisetech.StudentManagement.exceptionHandler.TestException;
import raisetech.StudentManagement.service.StudentService;

import java.util.List;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして受け付けるControllerです。
 */
@Validated
@RestController
public class StudentController {

    private StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    /**
     * 受講生詳細の一覧検索です。
     * 全件検索を行うので、条件指定は行わないものになります。
     * @return 受講生詳細一覧（全件）
     */
    @Operation(summary = "一覧検索", description = "受講生の一覧を検索します。")
    @GetMapping("/studentList")
    public List<StudentDetail> getStudentList() {
        return service.searchStudentList();
    }

    @Operation(summary = "一覧検索（例外処理）", description = "受講生の一覧検索（エラー）")
    @GetMapping("/studentListException")
    public List<StudentDetail> getStudentListException() throws TestException {
        throw new TestException("エラーが発生しました。");
    }

    /**
     * 受講生詳細の検索です。
     * IDに紐づく任意の受講生の情報を取得します。
     * @param id 受講生ID
     * @return 受講生
     */
    @Operation(summary = "受講生詳細検索", description = "IDに紐づく受講生の情報を検索します。")
    @GetMapping("/student/{id}")
    public StudentDetail getStudent(
            @PathVariable
            @NotBlank
            @Pattern(regexp = "^\\d+$")
            @Size(min=1, max=3, message = "IDは1文字以上、3文字以内で指定してください") String id){
        
        return service.searchStudent(id);
    }

    /**
     * 受講生詳細の登録を行います。
     * @param studentDetail 受講生詳細
     * @return 実行結果
     */
    @Operation(summary = "受講生登録", description = "受講生を登録します。")
    @PostMapping("/registerStudent")
    public ResponseEntity<StudentDetail> registerStudent(
            @RequestBody @Valid StudentDetail studentDetail){
        StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
        return ResponseEntity.ok(responseStudentDetail);
    }

    /**
     * 受講生詳細の更新を行います。
     * キャンセルフラグの更新もここで行います（論理削除）。
     * @param studentDetail 受講生詳細
     * @return 実行結果
     */
    @Operation(summary = "受講生詳細更新", description = "受講生詳細を更新します。")
    @PutMapping("/updateStudent")
    public ResponseEntity<String> updateStudent(
            @RequestBody @Valid StudentDetail studentDetail){
        service.updateStudent(studentDetail);
        return ResponseEntity.ok("更新処理が成功しました。");
    }

    //基本的にputは全体的な更新、patchは部分的な更新に使う


}
