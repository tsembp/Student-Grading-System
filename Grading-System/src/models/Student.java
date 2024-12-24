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
        if (!courses.contains(course)) {
            courses.add(course);
            course.addStudent(this);
        }
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

    public String getCoursesAndGrades() {
        StringBuilder sb = new StringBuilder();
        for (Course course : courses) {
            double grade = course.getGradeForStudent(this);
            sb.append(course.getName())
              .append(" (Grade: ").append(grade == -1 ? "Not Assigned" : grade).append(")\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append(", Name: ").append(name).append(", Class: ").append(className).append("\n");
        sb.append("> Assigned Courses:\n");
        
        if (courses.isEmpty()) {
            sb.append("No enrolled courses.\n");
        } else {
            sb.append(getCoursesAndGrades());
        }
        
        return sb.toString();
    }
}
