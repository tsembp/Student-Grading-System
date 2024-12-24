package models;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String name;
    private String courseId;
    private List<Student> students;
    private List<Double> grades;  // Stores grades for each student

    public Course(String name, String courseId) {
        this.name = name;
        this.courseId = courseId;
        this.students = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    // Add student to course
    public void addStudent(Student student) {
        students.add(student);
    }

    // Assign grade to a student in this course
    public void assignGrade(Student student, double grade) {
        int index = students.indexOf(student);
        if (index != -1) {
            grades.set(index, grade);  // Assign grade at the student's index
        } else {
            System.out.println("Student not enrolled in this course.");
        }
    }

    // Getters and setters for name, courseId, etc.
    public String getCourseId() {
        return courseId;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Double> getGrades() {
        return grades;
    }

    public String getName() {
        return name;
    }
}
