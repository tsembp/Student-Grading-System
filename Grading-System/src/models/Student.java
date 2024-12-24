package models;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private String id;
    private List<Course> courses = new ArrayList<>();

    public Student(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', id='" + id + "'}";
    }
}
