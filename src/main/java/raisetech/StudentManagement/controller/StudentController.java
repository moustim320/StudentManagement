package raisetech.StudentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raisetech.StudentManagement.domein.StudentDetail;
import raisetech.StudentManagement.exceptionHandler.TestException;
import raisetech.StudentManagement.service.StudentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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
     * 条件指定が可能です。条件が指定されない場合は全件検索を行います。
     * @param name 受講生名（部分一致検索）
     * @param courseName コース名（部分一致検索）
     * @param  startDate コース開始日（指定された日付以降）
     * @param endDate コース終了日（指定された日付以前）
     * @param status コースのステータス（指定されたステータス）
     * @return 受講生詳細一覧（条件に一致するもの、または全件）
     */
    @Operation(summary = "一覧検索", description = "受講生の一覧を条件付きで検索します。")
    @GetMapping("/studentList")
    public List<StudentDetail> getStudentListWithConditions(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "courseName", required = false) String courseName,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime
startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "status", required = false) String status
    ) {
        return service.searchStudentList(name, courseName, startDate, endDate, status);
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

    @Operation(summary = "コースステータス更新", description = "受講生のコースステータスを更新します。")
    @PutMapping("/updateCourseStatus/{courseId}")
    public ResponseEntity<String> updateCourseStatus(
            @PathVariable String courseId,
            @RequestParam @NotBlank String status) {
        try {
            service.updateCourseStatus(courseId, status);
            return ResponseEntity.ok("コースステータスを更新しました。");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("エラー: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("エラー: 指定されたコースが見つかりません。");
        }
    }

    //基本的にputは全体的な更新、patchは部分的な更新に使う


}
