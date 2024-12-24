package models;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private String name;
    private String id;
    private String className;
    private List<Course> courses;

    public Student(String name, String id, String className) {
        this.name = name;
        this.id = id;
        this.className = className;
        this.courses = new ArrayList<>();
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public List<Course> getCourses() {
        return courses;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Class: " + className;
    }
}
