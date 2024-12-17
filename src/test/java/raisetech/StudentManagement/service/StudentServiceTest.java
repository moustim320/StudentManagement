package raisetech.StudentManagement.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domein.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @Mock
    private StudentConverter converter;

    private StudentService sut;


    @BeforeEach
    void before(){
        sut = new StudentService(repository, converter);
    }

    @Test
    void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること(){
        // テストデータの準備
        List<Student> studentList = new ArrayList<>();
        List<StudentCourse> studentCourseList = new ArrayList<>();
        when(repository.search()).thenReturn(studentList);
        when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

        // 必要な引数を渡す
        String name = null;
        String courseName = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        String status = null;

        sut.searchStudentList(name, courseName, startDate, endDate, status);

        // モックの呼び出し確認
        verify(repository, times(1)).search();
        verify(repository, times(1)).searchStudentCourseList();
        verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
    }

    @Test
    void 受講生詳細の検索_リポジトリの処理が適切に呼び出され期待通りの結果を返すこと() {
        // 準備
        String id = "123";
        Student student = new Student();
        student.setId(id);

        when(repository.searchStudent(id)).thenReturn(student);
        when(repository.searchStudentCourse(id)).thenReturn(new ArrayList<>());

        // 実行
        StudentDetail expected = new StudentDetail(student, new ArrayList<>());
        StudentDetail actual = sut.searchStudent(id);

        // 検証
        verify(repository, times(1)).searchStudent(id);
        verify(repository, times(1)).searchStudentCourse(id);
        assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
    }

    @Test
    void 受講生登録処理_リポジトリの処理が適切に呼び出され期待通りの結果を返すこと() {
        // 準備
        Student mockStudent = new Student();
        mockStudent.setId("1"); // IDをStringとして設定

        StudentCourse course1 = new StudentCourse();
        StudentCourse course2 = new StudentCourse();
        List<StudentCourse> courseList = List.of(course1, course2);

        StudentDetail mockStudentDetail = new StudentDetail(mockStudent, courseList);

        doNothing().when(repository).registerStudent(mockStudent);
        doNothing().when(repository).registerStudentCourse(any(StudentCourse.class));

        // initStudentsCourse と同じロジックを適用して期待値を準備
        LocalDateTime now = LocalDateTime.now();
        courseList.forEach(course -> {
            course.setStudentId(mockStudent.getId());
            course.setCourseStartAt(now);
            course.setCourseEndAt(now.plusYears(1));
        });

        // 実行
        StudentDetail result = sut.registerStudent(mockStudentDetail);

        // 検証
        assertNotNull(result);
        assertEquals(mockStudentDetail, result);

        // リポジトリの呼び出しを検証
        verify(repository, times(1)).registerStudent(mockStudent);
        verify(repository, times(2)).registerStudentCourse(any(StudentCourse.class));

        // 各 StudentCourse の初期化処理を検証
        courseList.forEach(course -> {
            assertEquals(mockStudent.getId(), course.getStudentId());
            assertNotNull(course.getCourseStartAt());
            assertNotNull(course.getCourseEndAt());
        });
    }

    @Test
    void 受講生詳細の登録_初期化処理が行われること() {
        String id = "123";
        Student student = new Student();
        student.setId(id);
        StudentCourse studentCourse = new StudentCourse();

        sut.initStudentsCourse(studentCourse, student.getId());

        assertEquals(id, studentCourse.getStudentId());
        assertEquals(LocalDateTime.now().getHour(), studentCourse.getCourseStartAt().getHour());
        assertEquals(LocalDateTime.now().plusYears(1).getYear(), studentCourse.getCourseEndAt().getYear());
    }


    @Test
    void 受講生情報更新処理_リポジトリの処理が適切に呼び出されること() {
        // 準備
        Student mockStudent = new Student();
        StudentCourse course1 = new StudentCourse();
        StudentCourse course2 = new StudentCourse();
        List<StudentCourse> courseList = List.of(course1, course2);

        StudentDetail mockStudentDetail = new StudentDetail(mockStudent, courseList);

        doNothing().when(repository).updateStudent(mockStudent);
        doNothing().when(repository).updateStudentCourse(any(StudentCourse.class));

        // 実行
        sut.updateStudent(mockStudentDetail);

        // 検証
        // `updateStudent` が一度呼び出されたことを確認
        verify(repository, times(1)).updateStudent(mockStudent);

        // `updateStudentCourse` が各コースごとに呼び出されたことを確認
        verify(repository, times(2)).updateStudentCourse(any(StudentCourse.class));
        verify(repository, times(1)).updateStudentCourse(course1);
        verify(repository, times(1)).updateStudentCourse(course2);
    }

    @Test
    void 新規コース登録時の初期ステータスが仮申込であることを確認する() {
        // 準備
        StudentCourse course = new StudentCourse();
        // 実行
        sut.initStudentsCourse(course, "123");
        // 検証
        assertEquals("仮申込", course.getStatus());
        assertEquals("123", course.getStudentId());
        assertNotNull(course.getCourseStartAt());
        assertNotNull(course.getCourseEndAt());
    }

    @Test
    void ステータス更新の確認_正しい順序で更新されていること() {
        // 準備
        StudentCourse course = new StudentCourse();
        course.setStatus("仮申込");
        // 実行、検証
        sut.updateCourseStatus(course, "本申込");
        assertEquals("本申込", course.getStatus());

        sut.updateCourseStatus(course, "受講中");
        assertEquals("受講中", course.getStatus());

        sut.updateCourseStatus(course, "受講終了");
        assertEquals("受講終了", course.getStatus());
    }

    @Test
    void 不正なステータスを指定した場合の例外処理() {
        // 準備
        StudentCourse course = new StudentCourse();
        course.setStatus("仮申込");
        // 実行、検証
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            sut.updateCourseStatus(course, "受講終了");
        });

        assertEquals("無効なステータス遷移：仮申込->受講終了", exception.getMessage());
    }
}