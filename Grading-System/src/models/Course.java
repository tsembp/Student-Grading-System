package models;

import java.util.HashMap;
import java.util.Map;

public class Course {

    private String name;
    private String courseId;
    private int creditHours;
    private Map<Student, Grade> studentGrades;

    public Course(String name, String courseId, int creditHours) {
        this.name = name;
        this.courseId = courseId;
        this.creditHours = creditHours;
        this.studentGrades = new HashMap<>();
    }

    public void addStudent(Student student) {
        if (!studentGrades.containsKey(student)) {
            studentGrades.put(student, new Grade(0.0, 0.0, 0.0));  // default grades for exams and project
        }
    }

    public void assignGrade(Student student, Grade grade) {
        if (studentGrades.containsKey(student)) {
            studentGrades.put(student, grade);
        } else {
            System.out.println("Student with ID: '" + student.getId() + "' not enrolled in this course.");
        }
    }

    public Grade getGradeForStudent(Student student) {
        return studentGrades.getOrDefault(student, null);
    }

    public int getCreditHours() {
        return creditHours;
    }

    public String getCourseId() {
        return courseId;
    }

    public Map<Student, Grade> getStudentGrades() {
        return studentGrades;
    }

    public String getName() {
        return name;
    }
}
