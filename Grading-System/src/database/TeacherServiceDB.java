package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.*;

public class TeacherServiceDB {
    
    // Add a teacher to the database
    public void addTeacher(Teacher teacher) {
        String userQuery = "INSERT INTO users (first_name, last_name, user_id, username, password, role_id) VALUES (?, ?, ?, ?, ?, ?)";
        String teacherQuery = "INSERT INTO teacher (id, department, user_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery)) {
        
            // Insert into the users table
            userStmt.setString(1, teacher.getFirstName());
            userStmt.setString(2, teacher.getLastName());
            userStmt.setString(3, teacher.getId()); 
            userStmt.setString(4, teacher.getUsername());
            userStmt.setString(5, teacher.getPassword());
            userStmt.setInt(6, teacher.getRole().getId());
            userStmt.executeUpdate();
        
            // Insert into the teacher table
            teacherStmt.setString(1, teacher.getId());
            teacherStmt.setString(2, teacher.getDepartment());
            teacherStmt.setString(3, teacher.getId());
            teacherStmt.executeUpdate();
        
            System.out.println("Teacher added successfully");
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   // Delete teacher from database and unassign them from courses
    public void deleteTeacher(String teacherId) {
        // Queries to delete associated records
        String deleteTeacherQuery = "DELETE FROM teacher WHERE id = ?";
        String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
        String unassignTeacherFromCoursesQuery = "UPDATE course SET teacher_id = NULL WHERE teacher_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            // Unassign teacher from courses
            try (PreparedStatement stmt = conn.prepareStatement(unassignTeacherFromCoursesQuery)) {
                stmt.setString(1, teacherId);
                stmt.executeUpdate();
            }

            // Delete the teacher record from the Teacher table
            try (PreparedStatement stmt = conn.prepareStatement(deleteTeacherQuery)) {
                stmt.setString(1, teacherId);
                stmt.executeUpdate();
            }

            // Delete the teacher record from the Users table
            try (PreparedStatement stmt = conn.prepareStatement(deleteUserQuery)) {
                stmt.setString(1, teacherId); // Ensure teacherId corresponds to user_id
                stmt.executeUpdate();
            }

            System.out.println("Teacher and associated records deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Assign teacher to course
    public void assignCourseToTeacher(String teacherId, String courseId) {
        String checkCourseAssignedQuery = "SELECT COUNT(*) FROM course WHERE courseId = ? AND teacher_id = ?";
        String assignCourseQuery = "UPDATE course SET teacher_id = ? WHERE courseId = ?";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement checkStmt = conn.prepareStatement(checkCourseAssignedQuery);
            PreparedStatement assignStmt = conn.prepareStatement(assignCourseQuery)) {

            // Check if the course already has a teacher assigned
            checkStmt.setString(1, courseId);
            checkStmt.setString(2, teacherId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                // Course is not assigned yet, so assign it
                assignStmt.setString(1, teacherId);
                assignStmt.setString(2, courseId);
                assignStmt.executeUpdate();

                System.out.println("Course assigned to teacher successfully.");
            } else {
                // Course is already assigned
                System.out.println("Course is already assigned to this teacher.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Un-assign course from teacher
    public void unassignCourseFromTeacher(String teacherId, String courseId) {
        String checkCourseAssignedQuery = "SELECT teacher_id FROM course WHERE teacher_id = ? AND courseId = ?";
        String unassignCourseQuery = "UPDATE course SET teacher_id = NULL WHERE teacher_id = ? AND courseId = ?";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement checkStmt = conn.prepareStatement(checkCourseAssignedQuery);
            PreparedStatement unassignStmt = conn.prepareStatement(unassignCourseQuery)) {

            // Check if the course is assigned to the teacher
            checkStmt.setString(1, teacherId);
            checkStmt.setString(2, courseId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getString("teacher_id") != null) {
                // Course is assigned, so unassign it
                unassignStmt.setString(1, teacherId);
                unassignStmt.setString(2, courseId);
                unassignStmt.executeUpdate();

                System.out.println("Course un-assigned from teacher successfully.");
            } else {
                // Course is not assigned to this teacher
                System.out.println("Course is not assigned to this teacher.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get courses for teacher
    public List<Course> getCoursesForTeacher(String teacherId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT courseName, courseId, creditHours " +
                    "FROM course " +
                    "WHERE teacher_id = ?";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            // Fetch the teacher once
            Teacher teacher = findTeacherById(teacherId);
            if (teacher == null) {
                System.out.println("No teacher found with ID: " + teacherId);
                return courses;
            }

            // Log the teacherId
            System.out.println("Fetching courses for teacher with ID: " + teacherId);

            stmt.setString(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Create a Course object for each row in the result set
                Course course = new Course(
                        rs.getString("courseName"),
                        rs.getString("courseId"),
                        rs.getDouble("creditHours"),
                        teacher
                );
                courses.add(course);
            }

            // Log the retrieved courses
            System.out.println("Courses retrieved: " + courses.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    // Get students enrolled in teacher's course/s
    public List<Student> getStudentsForTeacher(String teacherId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT DISTINCT s.user_id, u.first_name, u.last_name, u.username, u.password " +
                        "FROM student s " +
                        "JOIN users u ON s.user_id = u.user_id " +
                        "JOIN studentcourse sc ON s.user_id = sc.studentId " + 
                        "JOIN course c ON sc.courseId = c.courseId " + 
                        "WHERE c.teacher_id = ?";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            // Log the teacherId to ensure it is being passed correctly
            System.out.println("Fetching students for teacher with ID: " + teacherId);

            stmt.setString(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Create a Student object for each row in the result set
                Student student = new Student(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("user_id"), // Changed from student_id to user_id
                        rs.getString("username"),
                        rs.getString("password")
                );
                students.add(student);
            }

            // Log the retrieved students
            System.out.println("Students retrieved: " + students.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // Find teacher by ID in database
    public Teacher findTeacherById(String teacherId) {
        String query = "SELECT t.user_id, t.department, u.first_name, u.last_name, u.username, u.password " +
                    "FROM Teacher t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "WHERE t.id = ?";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            // Log the teacherId to ensure it is being passed correctly
            System.out.println("Searching for teacher with ID: " + teacherId);

            stmt.setString(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Log the result to see if we are getting data
                System.out.println("Teacher found: " + rs.getString("user_id"));

                // Create a new Teacher object using data from both Teacher and users tables
                return new Teacher(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("department")
                );
            } else {
                // Log if no teacher is found
                System.out.println("No teacher found with ID: " + teacherId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no teacher is found
    }

    // Method to get all teachers from the database
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        
        // Query to fetch users with role_id = 2 (teachers) and join with the Teacher table
        String query = "SELECT u.first_name, u.last_name, u.username, u.password, u.user_id, t.department " +
                       "FROM users u " +
                       "JOIN teacher t ON u.user_id = t.user_id " +
                       "WHERE u.role_id = 2";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Create a Teacher object for each row in the result set
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String id = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String department = rs.getString("department");

                // Create Teacher instance and add to list
                Teacher teacher = new Teacher(firstName, lastName, id, username, password, department);
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

}
