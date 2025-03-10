package raisetech.StudentManagement.controller.converter;

import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.CourseStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domein.StudentDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 受講生詳細を受講生や受講生コース情報、もしくはその逆の変換を行うコンバーターです。
 */
@Component
public class StudentConverter {

    /**
     * 受講生に紐づく受講生コース情報をマッピングする。
     * 受講生コース情報は受講生に対して複数存在するのでループを回して受講生詳細情報を組み立てる。
     * @param studentList 受講生一覧
     * @param studentCourseList 受講生コース情報のリスト
     * @param courseStatusList コースステータスのリスト
     * @return 受講生詳細情報のリスト
     */
    public List<StudentDetail> convertStudentDetails(
            List<Student> studentList,
            List<StudentCourse> studentCourseList,
            List<CourseStatus> courseStatusList) {

        List<StudentDetail> studentDetails = new ArrayList<>();

        studentList.forEach(student -> {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);

            // 受講生に紐づくコースを抽出
            List<StudentCourse> convertStudentCourseList = extractStudentCourses(student, studentCourseList, courseStatusList);

            studentDetail.setStudentCourseList(convertStudentCourseList);
            studentDetails.add(studentDetail);
        });

        return studentDetails;
    }

    /**
     * 特定の受講生に紐づく受講生コース情報とそのステータスを抽出する。
     * @param student 受講生
     * @param studentCourseList 受講生コース情報のリスト
     * @param courseStatusList コースステータスのリスト
     * @return 受講生に紐づくコース情報のリスト
     */
    private List<StudentCourse> extractStudentCourses(
            Student student,
            List<StudentCourse> studentCourseList,
            List<CourseStatus> courseStatusList) {

        return studentCourseList.stream()
                .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
                .peek(studentCourse -> {
                    // 各コースに紐づくステータスを抽出して設定
                    List<CourseStatus> convertCourseStatusList = courseStatusList.stream()
                            .filter(courseStatus -> studentCourse.getId().equals(String.valueOf(courseStatus.getStudentsCoursesId())))
                            .collect(Collectors.toList());
                    studentCourse.setCourseStatusList(convertCourseStatusList);
                })
                .collect(Collectors.toList());
    }
}
