USE sgs;

-- Create the Student table
CREATE TABLE Student (
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    id VARCHAR(50) PRIMARY KEY,
    className VARCHAR(255)
);

-- Create the Course table
CREATE TABLE Course (
    courseName VARCHAR(255),
    courseId VARCHAR(255) PRIMARY KEY,
    creditHours DOUBLE
);

-- Create the Grade table
CREATE TABLE Grade (
    studentId VARCHAR(255),
    courseId VARCHAR(255),
    midtermGrade DOUBLE,
    endTermGrade DOUBLE,
    projectGrade DOUBLE,
    PRIMARY KEY (studentId, courseId)
);

-- Create a join table to link Student and Course (many-to-many relationship)
CREATE TABLE StudentCourse (
    studentId VARCHAR(255),
    courseId VARCHAR(255),
    PRIMARY KEY (studentId, courseId),
    FOREIGN KEY (studentId) REFERENCES Student(id),
    FOREIGN KEY (courseId) REFERENCES Course(courseId)
);

-- Create the User table
CREATE TABLE User (
    userId VARCHAR(50) PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role ENUM('Student', 'Teacher', 'Admin') -- Define roles
);