package models;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {
    private String department;
    private List<Course> courses;

    public Teacher(String firstName, String lastName, String id, String username, String password, String department) {
        super(firstName, lastName, id, username, password, Role.TEACHER);
        this.department = department;
        this.courses = new ArrayList<>();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
    }

    // Override toString for better representation
    @Override
    public String toString() {
        return "Teacher{" +
                "department='" + department + '\'' +
                ", courses=" + courses +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", id='" + getId() + '\'' +
                ", username='" + getUsername() + '\'' +
                '}';
    }
}
