package models;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private String firstName;
    private String lastName;
    private String id;
    private String className;
    private List<Course> courses;

    public Student(String firstName, String lastName, String id, String className) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public void setFirstName(String newName) {
        this.firstName = newName;
    }

    public void setLastName(String newName) {
        this.lastName = newName;
    }

    public void setId(String newId) {
        this.id = newId;
    }

    public void setClassName(String newClassName) {
        this.className = newClassName;
    }

    public String getCoursesAndGrades() {
        StringBuilder sb = new StringBuilder();

        int courseIndex = 1;
        for (Course course : courses) {
            Grade grade = course.getGradeForStudent(this); // get total grade for student 'this'
            sb.append(courseIndex + ". ");
            sb.append("[" + course.getCourseId() + "]");
            sb.append(" " + course.getName());
            sb.append(" (" + course.getCreditHours() + " EC)");
            if (grade != null) {
                sb.append("\t (Midterm: ").append(grade.getMidtermGrade())
                        .append(", End-Term: ").append(grade.getEndTermGrade())
                        .append(", Project: ").append(grade.getProjectGrade())
                        .append(", Final Grade: ").append(grade.getFinalGrade()).append(")\n");
            } else {
                sb.append("\t(Grade: Not Assigned)\\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append(" | Full Name: ").append(firstName).append(" ").append(lastName).append(" | Class: ").append(className).append("\n");

        if (courses.isEmpty()) {
            sb.append("> Assigned Courses: No enrolled courses.\n");
        } else {
            sb.append("> Assigned Courses:\n");
            sb.append(getCoursesAndGrades());
        }

        return sb.toString();
    }
    
}
