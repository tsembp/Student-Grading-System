package models;

public class Course {
    private String name;
    private String courseId;
    private double grade;

    public Course(String name, String courseId) {
        this.name = name;
        this.courseId = courseId;
        this.grade = -1;  // Default to unassigned grade
    }

    public String getName() {
        return name;
    }

    public String getCourseId() {
        return courseId;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Course{name='" + name + "', courseId='" + courseId + "', grade=" + grade + "}";
    }
}
