package database;

import models.Teacher;
import models.Course;
import models.Student;

import java.util.List;

public class TeacherServiceDBTest {

    private static CourseServiceDB courseService = new CourseServiceDB();
    private static StudentServiceDB studentService = new StudentServiceDB();

    public static void main(String[] args) {
        TeacherServiceDB teacherServiceDB = new TeacherServiceDB();
        
        // Test 1: Add a Teacher
        Teacher newTeacher = new Teacher("John", "Doe", "T001", "johndoe", "password123", "Mathematics");
        teacherServiceDB.addTeacher(newTeacher);
        System.out.println("Teacher added successfully: " + newTeacher.getFirstName() + " " + newTeacher.getLastName());
        
        // Test 2: Find Teacher by ID
        Teacher foundTeacher = teacherServiceDB.findTeacherById("T001");
        if (foundTeacher != null) {
            System.out.println("Teacher found: " + foundTeacher.getFirstName() + " " + foundTeacher.getLastName());
        } else {
            System.out.println("Teacher not found with ID: T001");
        }
        
        Course dummy = new Course("Basic Physics", "PHY101", 6.5, foundTeacher);
        Student stdnt = new Student("Bob", "Ross", "student456", "bobRoss", "studentPassword456");
        studentService.addStudent(stdnt);
        studentService.assignCourseToStudent(stdnt, dummy);

        // Test 3: Assign a Course to Teacher
        // courseService.createCourse(dummy);
        teacherServiceDB.assignCourseToTeacher("T001", "PHY101");
        System.out.println("Course assigned successfully to Teacher T001.");

        // Test 4: Get Courses for Teacher
        List<Course> courses = teacherServiceDB.getCoursesForTeacher("T001");
        if (!courses.isEmpty()) {
            System.out.println("Courses retrieved for Teacher T001:");
            for (Course course : courses) {
                System.out.println(course.getCourseName() + " (ID: " + course.getCourseId() + ")");
            }
        } else {
            System.out.println("No courses found for Teacher T001.");
        }

        // Test 5: Get Students for Teacher's Course
        List<Student> students = teacherServiceDB.getStudentsForTeacher("T001");
        if (!students.isEmpty()) {
            System.out.println("Students enrolled in Teacher T001's courses:");
            for (Student student : students) {
                System.out.println(student.getFirstName() + " " + student.getLastName());
            }
        } else {
            System.out.println("No students found for Teacher T001.");
        }

        // Test 6: Un-assign a Course from Teacher
        teacherServiceDB.unassignCourseFromTeacher("T001", "PHY101"); // Replace with actual course ID
        System.out.println("Course un-assigned successfully from Teacher T001.");

        // Test 7: Delete Teacher
        teacherServiceDB.deleteTeacher("T001"); // Replace with actual teacher ID
        System.out.println("Teacher T001 and associated records deleted successfully.");
    }
}