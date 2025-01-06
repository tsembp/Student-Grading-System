package database;

import models.Course;
import models.Teacher;

public class CourseServiceDBTest {

    public static void main(String[] args) {
        CourseServiceDB courseServiceDB = new CourseServiceDB();
        TeacherServiceDB teacherServiceDB = new TeacherServiceDB();

        // Teacher teacher = new Teacher("Alice", "Smith", "teacher123", "asmith", "teacherPassword123", "Physics");
        // teacherServiceDB.addTeacher(teacher);

        // Assume a teacher has been created in the database or mock the teacher object
        Teacher testTeacher = teacherServiceDB.findTeacherById("teacher123");

        // // Test 1: Create a Course
        // Course newCourse = new Course("Physics", "PHY101", 6.0, testTeacher);
        // courseServiceDB.createCourse(newCourse);
        // System.out.println("Course created successfully: " + newCourse.getCourseName());

        // // Test 2: Retrieve a Course by ID
        // String courseId = "PHY101";
        // Course retrievedCourse = courseServiceDB.getCourseById(courseId);
        // if (retrievedCourse != null) {
        //     System.out.println("Course retrieved: " + retrievedCourse.getCourseName());
        //     System.out.println("Course ID: " + retrievedCourse.getCourseId());
        //     System.out.println("Credit Hours: " + retrievedCourse.getCreditHours());
        //     System.out.println("Teacher: " + retrievedCourse.getTeacher().getFirstName());
        // } else {
        //     System.out.println("Course not found for ID: " + courseId);
        // }

        // Test 3: Delete a Course
        String deleteCourseId = "PHY101";
        courseServiceDB.deleteCourse(deleteCourseId);
        System.out.println("Course with ID " + deleteCourseId + " deleted successfully.");

        // Check if the course is deleted by retrieving it again
        Course deletedCourse = courseServiceDB.getCourseById(deleteCourseId);
        if (deletedCourse == null) {
            System.out.println("Course with ID " + deleteCourseId + " is successfully deleted.");
        } else {
            System.out.println("Course still exists: " + deletedCourse.getCourseName());
        }


    }
}
