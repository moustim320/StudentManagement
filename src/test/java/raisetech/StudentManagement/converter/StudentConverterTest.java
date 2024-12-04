package raisetech.StudentManagement.converter;

import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domein.StudentDetail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StudentConverterTest {

    private StudentConverter studentConverter = new StudentConverter();

    @Test
    void 有効なデータを渡した場合に受講生詳細情報を正しく変換できること() {
        //テストデータ作成
        Student student1 = createStudent("1", "森本", "モリモト", "モリ", "mori@example.com", "東京", 20, "女", "Remark1", false);
        Student student2 = createStudent("2", "森田", "モリモト", "モリ", "mori@example.com", "東京", 20, "女", "Remark1", false);

        List<Student> studentList = Arrays.asList(student1, student2);

        StudentCourse course1 = createStudentCourse("123", "1", "A", LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        StudentCourse course2 = createStudentCourse("135", "1", "B", LocalDateTime.now(), LocalDateTime.now().plusMonths(2));
        StudentCourse course3 = createStudentCourse("155", "2", "C", LocalDateTime.now(), LocalDateTime.now().plusMonths(3));

        List<StudentCourse> studentCourseList = Arrays.asList(course1, course2, course3);

        //メソッド呼び出し
        List<StudentDetail> result = studentConverter.convertStudentDetails(studentList, studentCourseList);

        //検証
        assertThat(result).hasSize(2);

        //StudentDetail森本
        StudentDetail detail1 = result.get(0);
        assertThat(detail1.getStudent()).isEqualTo(student1);
        assertThat(detail1.getStudentCourseList()).containsExactlyInAnyOrder(course1, course2);

        //StudentDetail森田
        StudentDetail detail2 = result.get(1);
        assertThat(detail2.getStudent()).isEqualTo(student2);
        assertThat(detail2.getStudentCourseList()).containsExactly(course3);
    }

    @Test
    void 受講生リストが空の場合に空の受講生詳細情報リストが返されること() {
        //空のデータ
        List<Student> studentList = new ArrayList<>();
        List<StudentCourse> studentCourseList = new ArrayList<>();

        //メソッド呼び出し
        List<StudentDetail> result = studentConverter.convertStudentDetails(studentList, studentCourseList);

        //検証
        assertThat(result).isEmpty();
    }

    @Test
    void 受講生に対応するコースが存在しない場合にコース情報が空になること() {
        //テストデータ作成
        Student student1 = createStudent("1", "森本", "モリモト", "モリ", "mori@example.com", "東京", 20, "女", "Remark1", false);

        List<Student> studentList = List.of(student1);

        //不一致のstudentIdを持つコース
        StudentCourse course1 = createStudentCourse("123", "1", "A", LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        List<StudentCourse> studentCourseList = List.of(course1);

        //メソッド呼び出し
        List<StudentDetail> result = studentConverter.convertStudentDetails(studentList, studentCourseList);

        //検証
        assertThat(result).hasSize(1);

        StudentDetail detail1 = result.get(0);
        assertThat(detail1.getStudent()).isEqualTo(student1);
        assertThat(detail1.getStudentCourseList().isEmpty());
    }

    //ヘルパーメソッド
    private Student createStudent(String id, String name, String kanaName, String nickname, String mailAddress, String address, int age, String gender, String remark, boolean isDeleted) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setKanaName(kanaName);
        student.setNickname(nickname);
        student.setMailAddress(mailAddress);
        student.setAddress(address);
        student.setAge(age);
        student.setGender(gender);
        student.setRemark(remark);
        student.setDeleted(isDeleted);
        return student;
    }

    private StudentCourse createStudentCourse(String id, String studentId, String courseName, LocalDateTime startAt, LocalDateTime endAt) {
        StudentCourse course = new StudentCourse();
        course.setId(id);
        course.setStudentId(studentId);
        course.setCourseName(courseName);
        course.setCourseStartAt(startAt);
        course.setCourseEndAt(endAt);
        return course;
    }
}
