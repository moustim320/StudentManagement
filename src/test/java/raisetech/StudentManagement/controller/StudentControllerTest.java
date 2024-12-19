package raisetech.StudentManagement.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.service.StudentService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService service;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception{
        // モックサービスでメソッドの戻り値を設定
        when(service.searchStudentList(
                any(String.class),       // 引数1: String
                any(String.class),       // 引数2: String
                any(LocalDateTime.class), // 引数3: LocalDateTime
                any(LocalDateTime.class), // 引数4: LocalDateTime
                any(String.class)        // 引数5: String
        )).thenReturn(Collections.emptyList());

        // MockMvc でリクエストを実行する部分
        mockMvc.perform(get("/studentList")  // エンドポイントを指定
                .param("name", "森本光雄")   // リクエストパラメータを指定
                .param("courseNme", "Aコース")
                .param("startDate", "2024-04-01T00:00:00")  // ISO8601形式の日時
                .param("endDate", "2025-03-31T00:00:00")
                .param("status", "仮申込"))
                .andExpect(status().isOk())       // HTTPステータス200を期待
                .andExpect(content().json("[]")); // 空のリストが返ることを検証

        // サービスが正しく呼ばれたことを検証
        verify(service, times(1)).searchStudentList(
                "森本光雄", "Aコース",
                LocalDateTime.parse("2024-04-01T00:00:00"),
                LocalDateTime.parse("2025-03-31T00:00:00"),
                "仮申込");
    }

    @Test
    void 受講生詳細の検索が実行できて空で返ってくること() throws Exception {
        String id = "123";
        mockMvc.perform(get("/student/{id}", id))
                .andExpect(status().isOk());

        verify(service, times(1)).searchStudent(id);
    }

    @Test
    void 受講生詳細の登録が実行できて空で返ってくること() throws Exception {
        // リクエストデータは適切に構築して入力チェックの検証も兼ねている。
        // 本来であれば返りは登録されたデータが入るが、モック化すると意味がないため、レスポンスは作らない。
        mockMvc.perform(post("/registerStudent").contentType(MediaType.APPLICATION_JSON).content(
                """
                    {
                        "student": {
                            "id": "123",
                            "name": "森本光雄",
                            "kanaName": "モリモトミツオ",
                            "nickname": "モリモト",
                            "mailAddress": "test@example.com",
                            "address": "兵庫県",
                            "age": "40",
                            "gender": "男",
                            "remark": ""
                        },
                        "studentCourseList": [
                            {
                                "courseName": "Aコース"
                            }
                        ]
                    }
                    """
        ))
        .andExpect(status().isOk()) // レスポンスのステータスコードを検証
        .andExpect(content().string(""));  // レスポンスが空であることを検証

        verify(service, times(1)).registerStudent(any());  // サービス層が呼び出されたか検証
    }

    @Test
    void 受講生詳細の更新が実行できて成功メッセージが返ること() throws Exception {
        // リクエストデータは適切に構築して入力チェックの検証も兼ねている。
        mockMvc.perform(put("/updateStudent").contentType(MediaType.APPLICATION_JSON).content(
                        """
                            {
                                "student": {
                                    "id": "123",
                                    "name": "森本光雄",
                                    "kanaName": "モリモトミツオ",
                                    "nickname": "モリモト",
                                    "mailAddress": "test@example.com",
                                    "address": "兵庫県",
                                    "age": "40",
                                    "gender": "男",
                                    "remark": ""
                                },
                                "studentCourseList": [
                                    {
                                        "id": "345",
                                        "studentId": "123",
                                        "courseName": "Aコース",
                                        "courseStartAt": "2024-04-01T00:00:00",
                                        "courseEndAt": "2025-03-31T00:00:00"
                                    }
                                ]
                            }
                            """
                ))
                .andExpect(status().isOk())  // ステータスコード 200 OK を確認
                .andExpect(content().string("更新処理が成功しました。"));  // 更新処理の成功を示すレスポンス

        verify(service, times(1)).updateStudent(any());  // サービス層が呼び出されたか検証
    }

    @Test
    void 受講生詳細の例外APIが実行できてステータスが400で返ってくること() throws Exception {
        mockMvc.perform(get("/studentListException"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("エラーが発生しました。"));
    }

    @Test
    void 受講生詳細の受講生で適切な値を入力したときに入力チェックに異常が発生しないこと() {

        Student student = new Student();
        student.setId("1");
        student.setName("森本光雄");
        student.setKanaName("モリモトミツオ");
        student.setNickname("モリモト");
        student.setMailAddress("test@example.com");
        student.setAddress("兵庫県");
        student.setGender("男");

        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void 受講生詳細の受講生でIDに数字以外を用いたときに入力チェックにかかること() {

        Student student = new Student();
        student.setId("テストです。");
        student.setName("森本光雄");
        student.setKanaName("モリモトミツオ");
        student.setNickname("モリモト");
        student.setMailAddress("test@example.com");
        student.setAddress("兵庫県");
        student.setGender("男");

        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations).extracting("message")
                .containsOnly("数字のみ入力するようにしてください。");
    }

    @Test
    void コースステータス更新が成功すること() throws Exception {
        mockMvc.perform(put("/updateCourseStatus/{courseId}", "123")
                        .param("status", "本申込"))
                .andExpect(status().isOk())
                .andExpect(content().string("コースステータスを更新しました。"));
    }

}