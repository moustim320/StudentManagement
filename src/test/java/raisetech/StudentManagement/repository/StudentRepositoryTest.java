package raisetech.StudentManagement.repository;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository sut;

    @Test
    void 受講生の全件検索が行えること() {
        //テストデータを取得
        List<Student> actual = sut.search();

        //初期状態では５件のデータが存在していることを確認
        assertThat(actual.size()).isEqualTo(5);

        // データの中身をチェック
        assertThat(actual.get(0).getName()).isEqualTo("大野 智");
        assertThat(actual.get(0).getKanaName()).isEqualTo("オオノ サトシ");
        assertThat(actual.get(0).getNickname()).isEqualTo("リーダー");
        assertThat(actual.get(0).getMailAddress()).isEqualTo("satoshiohno@ara.shi");
        assertThat(actual.get(0).getAddress()).isEqualTo("東京都");
        assertThat(actual.get(0).getAge()).isEqualTo(43);
        assertThat(actual.get(0).getGender()).isEqualTo("男");
    }

    @Test
    void 受講生の登録が行えること() {
        //新規登録する受講生の情報を作成
        Student student = new Student();
        student.setName("森本光雄");
        student.setKanaName("モリモトミツオ");
        student.setNickname("モリモト");
        student.setMailAddress("test@example.com");
        student.setAddress("兵庫県");
        student.setGender("男");

        //新規登録を実行
        sut.registerStudent(student);

        //登録後の受講生リストを取得
        List<Student> actual = sut.search();

        //登録前から１件増加していることを確認
        assertThat(actual.size()).isEqualTo(6);
    }

    @Test
    void 受講生をIDで検索できること() {
        Student actual = sut.searchStudent("1");
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("大野 智");
        assertThat(actual.getKanaName()).isEqualTo("オオノ サトシ");
        assertThat(actual.getNickname()).isEqualTo("リーダー");
        assertThat(actual.getMailAddress()).isEqualTo("satoshiohno@ara.shi");
        assertThat(actual.getAddress()).isEqualTo("東京都");
        assertThat(actual.getAge()).isEqualTo(43);
        assertThat(actual.getGender()).isEqualTo("男");
    }

    @Test
    void 存在しないIDで受講生を検索するとnullが返されること() {
        Student actual = sut.searchStudent("999"); // 存在しないID
        assertThat(actual).isNull();
    }

    @Test
    void 指定した条件で受講生を検索できること() {
        // 検索条件
        String name = "大野 智";

        // メソッド呼び出し
        List<Student> result = sut.searchWithConditions(name);

        // 結果の確認
        assertThat(result).hasSize(1); // 1件だけ一致することを確認
        assertThat(result.get(0).getName()).isEqualTo(name); // 名前が一致することを確認
    }

    @Test
    void 検索条件にnullを渡しても全件が取得できること() {
        List<Student> actual = sut.searchWithConditions(null);
        assertThat(actual).hasSize(5); // 初期データに基づく全件数
    }

    @Test
    void 受講生のコース情報を全件取得できること() {
        List<StudentCourse> actual = sut.searchStudentCourseList();
        assertThat(actual.size()).isEqualTo(11); //初期データに基づく件数

        // データの中身をチェック
        assertThat(actual.get(0).getStudentId()).isEqualTo("1");
        assertThat(actual.get(0).getCourseName()).isEqualTo("Aコース");
        assertThat(actual.get(0).getCourseStartAt()).isEqualTo(LocalDateTime.of(2024, 4, 1, 0, 0));
        assertThat(actual.get(0).getCourseEndAt()).isEqualTo(LocalDateTime.of(2025, 3, 31, 0, 0));
    }

    @Test
    void 受講生IDで紐づくコース情報を取得できること() {
        List<StudentCourse> actual = sut.searchStudentCourse("1");
        assertThat(actual.size()).isEqualTo(1); //ID=1の受講生のコース件数
        assertThat(actual.get(0).getCourseName()).isEqualTo("Aコース");
        assertThat(actual.get(0).getCourseStartAt()).isEqualTo(LocalDateTime.of(2024, 4, 1, 0, 0));
        assertThat(actual.get(0).getCourseEndAt()).isEqualTo(LocalDateTime.of(2025, 3, 31, 0, 0));
    }

    @Test
    void 指定した条件で受講生のコース情報を検索できること() {
        // 検索条件
        String courseName = "Aコース";
        LocalDateTime startDate = LocalDateTime.of(2024, 4, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 0, 0);
        String status = "仮申込"; // 仮のステータス

        // メソッド呼び出し
        List<StudentCourse> result = sut.searchStudentCourseListWithConditions(courseName, startDate, endDate, status);

        // 結果の確認
        assertThat(result).hasSize(1); // 条件に一致するコースが1件だけあることを確認
        assertThat(result.get(0).getCourseName()).isEqualTo(courseName); // コース名が一致することを確認
        assertThat(result.get(0).getCourseStartAt()).isEqualTo(startDate); // 開始日が一致することを確認
        assertThat(result.get(0).getCourseEndAt()).isEqualTo(endDate); // 終了日が一致することを確認
    }

    @Test
    void 新しい受講生コース情報を登録できること() {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId("2");
        studentCourse.setCourseName("Cコース");
        studentCourse.setCourseStartAt(LocalDateTime.of(2024, 12, 1, 9, 0));
        studentCourse.setCourseEndAt(LocalDateTime.of(2025, 5, 31, 18, 0));

        sut.registerStudentCourse(studentCourse);

        List<StudentCourse> actual = sut.searchStudentCourse("2");
        assertThat(actual.size()).isEqualTo(2); //登録後の件数に応じて調整
    }

    @Test
    void 受講生情報を更新できること() {
        Student student = sut.searchStudent("1");
        assertThat(student).isNotNull();

        student.setAddress("新しい住所");
        sut.updateStudent(student);

        Student updatedStudent = sut.searchStudent("1");
        assertThat(updatedStudent.getAddress()).isEqualTo("新しい住所");
    }

    @Test
    void 受講生コース情報のコース名を更新できること() {
        List<StudentCourse> courses = sut.searchStudentCourse("1");
        assertThat(courses).isNotEmpty();

        StudentCourse course = courses.get(0);
        course.setCourseName("新しいコース名");
        sut.updateStudentCourse(course);

        List<StudentCourse> updateCourses = sut.searchStudentCourse("1");
        assertThat(updateCourses.get(0).getCourseName()).isEqualTo("新しいコース名");
    }

}