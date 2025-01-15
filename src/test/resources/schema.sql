CREATE TABLE IF NOT EXISTS students
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    kana_name VARCHAR(50),
    nickname VARCHAR(30),
    mail_address VARCHAR(50),
    address VARCHAR(30),
    age INT,
    gender VARCHAR(10),
    remark VARCHAR(100),
    isDeleted TINYINT
);

CREATE TABLE IF NOT EXISTS students_courses
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_name VARCHAR(50),
    course_start_at TIMESTAMP,
    course_end_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    students_courses_id INT NOT NULL,
    status VARCHAR(20) NOT NULL
);