package database;

import models.Course;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseServiceDB {

    // Create a course and add it to the database
    public void createCourse(Course course) {
        String query = "INSERT INTO Course (courseName, courseId, creditHours) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseName());
            stmt.setString(2, course.getCourseId());
            stmt.setDouble(3, course.getCreditHours());
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
                return new Course(
                        rs.getString("courseName"),
                        rs.getString("courseId"),
                        rs.getDouble("creditHours")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
