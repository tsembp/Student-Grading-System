package services;

import models.Course;
import models.Student;
import models.Grade;
import java.util.HashMap;
import java.util.Map;

public class CourseService {

    private Map<String, Course> courses;

    public CourseService() {
        courses = new HashMap<>();
    }

    // Create a course and add it to the system
    public void createCourse(String name, String courseId, double creditHours) {
        courses.put(courseId, new Course(name, courseId, creditHours));
    }

    // Get a course by ID
    public Course getCourseById(String courseId) {
        return courses.get(courseId);
    }

    // Assign grades to a student for a course (midterm, end-term, project)
    public void assignGradeToCourse(Student student, Course course, double midtermGrade, double endTermGrade, double projectGrade) {
        // Check if the student is already enrolled in the course
        if (student.getCourses().contains(course)) {
            Grade grade = new Grade(midtermGrade, endTermGrade, projectGrade);
            course.assignGrade(student, grade);  // Assuming Course has a method to assign the Grade object
        } else {
            System.out.println("Student with ID '" + student.getId() + "' not enrolled in this course.");
        }
    }
}
