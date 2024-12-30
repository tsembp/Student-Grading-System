package database;

import database.DatabaseConnection;
import models.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeServiceDB {

    // Assign grades to a student for a course
    public void assignGrade(String studentId, String courseId, Grade grade) {
        String query = "INSERT INTO Grade (studentId, courseId, midtermGrade, endTermGrade, projectGrade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            stmt.setDouble(3, grade.getMidtermGrade());
            stmt.setDouble(4, grade.getEndTermGrade());
            stmt.setDouble(5, grade.getProjectGrade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update grades for a student and course
    public void updateGrade(String studentId, String courseId, Grade grade) {
        String query = "UPDATE Grade SET midtermGrade = ?, endTermGrade = ?, projectGrade = ? WHERE studentId = ? AND courseId = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, grade.getMidtermGrade());
            stmt.setDouble(2, grade.getEndTermGrade());
            stmt.setDouble(3, grade.getProjectGrade());
            stmt.setString(4, studentId);
            stmt.setString(5, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
