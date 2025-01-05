package models;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private List<Course> courses;

    public Student(String firstName, String lastName, String id, String username, String password) {
        super(firstName, lastName, id, username, password, Role.STUDENT);
        this.courses = new ArrayList<>();
    }

    public void addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            course.addStudent(this);
        }
    }

    public List<Course> getCourses() {
        return courses;
    }

    public String getCoursesAndGrades() {
        StringBuilder sb = new StringBuilder();

        int courseIndex = 1;
        for (Course course : courses) {
            Grade grade = course.getGradeForStudent(this); // get total grade for student 'this'
            sb.append(courseIndex + ". ");
            sb.append("[" + course.getCourseId() + "]");
            sb.append(" " + course.getCourseName());
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
        sb.append("ID: ").append(super.getId()).append(" | Full Name: ").append(super.getFirstName()).append(" ").append(super.getLastName()).append("\n");

        if (courses.isEmpty()) {
            sb.append("> Assigned Courses: No enrolled courses.\n");
        } else {
            sb.append("> Assigned Courses:\n");
            sb.append(getCoursesAndGrades());
        }

        return sb.toString();
    }

}
