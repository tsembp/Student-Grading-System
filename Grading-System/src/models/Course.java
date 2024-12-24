package models;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String name;
    private String courseId;
    private List<Student> students;
    private List<Double> grades;

    public Course(String name, String courseId) {
        this.name = name;
        this.courseId = courseId;
        this.students = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            grades.add(-1.0);  // Add a default grade for the student
        }
    }

    public void assignGrade(Student student, double grade) {
        int index = students.indexOf(student);
        if (index != -1) {
            grades.set(index, grade);
        } else {
            System.out.println("Student not enrolled in this course.");
        }
    }

    public double getGradeForStudent(Student student) {
        int index = students.indexOf(student);
        return index != -1 ? grades.get(index) : -1;
    }

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
