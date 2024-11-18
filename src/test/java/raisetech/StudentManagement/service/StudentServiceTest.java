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

import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        List<Student> studentList = new ArrayList<>();
        List<StudentCourse> studentCourseList = new ArrayList<>();
        when(repository.search()).thenReturn(studentList);
        when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

        sut.searchStudentList();

        verify(repository, times(1)).search();
        verify(repository, times(1)).searchStudentCourseList();
        verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
    }

    @Test
    void 受講生詳細の検索_リポジトリの処理が適切に呼び出され期待通りの結果を返すこと() {
        // Arrange
        String id = "123";
        Student student = new Student();
        student.setId(id);

        when(repository.searchStudent(id)).thenReturn(student);
        when(repository.searchStudentCourse(id)).thenReturn(new ArrayList<>());

        // Act
        StudentDetail expected = new StudentDetail(student, new ArrayList<>());
        StudentDetail actual = sut.searchStudent(id);

        verify(repository, times(1)).searchStudent(id);
        verify(repository, times(1)).searchStudentCourse(id);
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
    }



    @Test
    void 受講生登録処理_リポジトリの処理が適切に呼び出され期待通りの結果を返すこと() {
        // Arrange
        Student mockStudent = new Student();
        StudentCourse course1 = new StudentCourse();
        StudentCourse course2 = new StudentCourse();
        List<StudentCourse> courseList = List.of(course1, course2);

        StudentDetail mockStudentDetail = new StudentDetail(mockStudent, courseList);

        doNothing().when(repository).registerStudent(mockStudent);
        doNothing().when(repository).registerStudentCourse(any(StudentCourse.class));

        // Act
        StudentDetail result = sut.registerStudent(mockStudentDetail);

        // Assert
        assertNotNull(result);
        assertEquals(mockStudentDetail, result);

        // リポジトリの呼び出しを検証
        verify(repository, times(1)).registerStudent(mockStudent);
        verify(repository, times(2)).registerStudentCourse(any(StudentCourse.class));

        // 各 StudentCourse の初期化処理を検証
        courseList.forEach(course -> {
            assertEquals(mockStudent.getId(), course.getStudentId());
        });
    }

    @Test
    void 受講生情報更新処理_リポジトリの処理が適切に呼び出されること() {
        // Arrange
        Student mockStudent = new Student();
        StudentCourse course1 = new StudentCourse();
        StudentCourse course2 = new StudentCourse();
        List<StudentCourse> courseList = List.of(course1, course2);

        StudentDetail mockStudentDetail = new StudentDetail(mockStudent, courseList);

        doNothing().when(repository).updateStudent(mockStudent);
        doNothing().when(repository).updateStudentCourse(any(StudentCourse.class));

        // Act
        sut.updateStudent(mockStudentDetail);

        // Assert
        // `updateStudent` が一度呼び出されたことを確認
        verify(repository, times(1)).updateStudent(mockStudent);

        // `updateStudentCourse` が各コースごとに呼び出されたことを確認
        verify(repository, times(2)).updateStudentCourse(any(StudentCourse.class));
        verify(repository, times(1)).updateStudentCourse(course1);
        verify(repository, times(1)).updateStudentCourse(course2);
    }

}