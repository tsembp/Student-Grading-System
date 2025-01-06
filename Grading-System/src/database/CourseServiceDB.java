package database;

import models.Course;
import models.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import models.Student;

public class CourseServiceDB {

    private TeacherServiceDB teacherServiceDB = new TeacherServiceDB();

    // Create a course and add it to the database
    public void createCourse(Course course) {
        String query = "INSERT INTO Course (courseName, courseId, creditHours, teacher_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseName());
            stmt.setString(2, course.getCourseId());
            stmt.setDouble(3, course.getCreditHours());
            stmt.setString(4, course.getTeacher().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get a course by ID   
    public Course getCourseById(String courseId) {
        String query = "SELECT * FROM Course WHERE courseId = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Assuming a method exists to fetch a Teacher by ID
                Teacher teacher = teacherServiceDB.findTeacherById(rs.getString("teacher_id"));
                return new Course(
                        rs.getString("courseName"),
                        rs.getString("courseId"),
                        rs.getDouble("creditHours"),
                        teacher
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getStudentsForCourse(String courseId) {
        String query = "SELECT u.first_name, u.last_name, u.user_id, u.username " +
                       "FROM users u " +
                       "JOIN studentcourse sc ON u.user_id = sc.studentId " +
                       "WHERE sc.courseId = ?";
    
        List<Student> students = new ArrayList<>();
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            // Set the courseId parameter
            stmt.setString(1, courseId);
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String id = rs.getString("user_id");
                String username = rs.getString("username");
                String password = ""; // You may choose whether to include the password, typically not used for display
    
                // Create a new Student object
                students.add(new Student(firstName, lastName, id, username, password));
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return students;
    }

    // Delete a course from the database
    public void deleteCourse(String courseId) {
        String query = "DELETE FROM Course WHERE courseId = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
