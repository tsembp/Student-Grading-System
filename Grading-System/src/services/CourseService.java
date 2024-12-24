package services;

import models.Course;
import java.util.HashMap;
import java.util.Map;

public class CourseService {

    // Store courses in a map
    private Map<String, Course> courses = new HashMap<>();

    // Create a new course and add it to the courses map
    public Course createCourse(String name, String courseId) {
        Course course = new Course(name, courseId);
        courses.put(courseId, course); // Store course by courseId
        return course;
    }

    // Get a list of all courses
    public Map<String, Course> getAllCourses() {
        return courses;
    }

    // Assign a grade to a course
    public void assignGrade(Course course, double grade) {
        course.setGrade(grade);
    }
}
