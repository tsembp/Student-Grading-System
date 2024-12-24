package services;

import models.Course;
import models.Student;
import java.util.HashMap;
import java.util.Map;

public class CourseService {

    private Map<String, Course> courses;

    public CourseService() {
        courses = new HashMap<>();
    }

    // Create a course and add it to the system
    public void createCourse(String name, String courseId) {
        courses.put(courseId, new Course(name, courseId));
    }

    // Get a course by ID
    public Course getCourseById(String courseId) {
        return courses.get(courseId);
    }

    public void assignGradeToCourse(Student student, Course course, double grade) {
        // Check if the student is already enrolled in the course
        if (student.getCourses().contains(course)) {
            course.assignGrade(student, grade);  // Assuming Course has a method to assign grades
        } else {
            System.out.println("Student not enrolled in this course.");
        }
    }
    
}
