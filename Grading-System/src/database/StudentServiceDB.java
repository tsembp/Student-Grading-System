package database;

import database.DatabaseConnection;
import models.Student;
import models.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentServiceDB {

    // Add a student to the database
    public void addStudent(Student student) {
        String query = "INSERT INTO Student (firstName, lastName, id, className) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getId());
            stmt.setString(4, student.getClassName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete student from database
    public void deleteStudent(String studentId) {
        // Delete the associated records in the studentcourse table first
        String deleteStudentCourseQuery = "DELETE FROM studentcourse WHERE studentId = ?";
        String deleteStudentQuery = "DELETE FROM Student WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect()) {
            // Delete records from studentcourse table
            try (PreparedStatement stmt = conn.prepareStatement(deleteStudentCourseQuery)) {
                stmt.setString(1, studentId);
                stmt.executeUpdate();
            }
    
            // Now delete the student record from the Student table
            try (PreparedStatement stmt = conn.prepareStatement(deleteStudentQuery)) {
                stmt.setString(1, studentId);
                stmt.executeUpdate();
            }
            
            System.out.println("Student and associated records deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add grade for a student in a course
    public void assignGrade(String studentId, String courseId, double midtermGrade, double endTermGrade, double projectGrade) {
        String query = "INSERT INTO Grade (studentId, courseId, midtermGrade, endTermGrade, projectGrade) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "midtermGrade = VALUES(midtermGrade), " +
                    "endTermGrade = VALUES(endTermGrade), " +
                    "projectGrade = VALUES(projectGrade)";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            stmt.setDouble(3, midtermGrade);
            stmt.setDouble(4, endTermGrade);
            stmt.setDouble(5, projectGrade);
            stmt.executeUpdate();
            System.out.println("Grades assigned successfully for student " + studentId + " in course " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Remove course from student in database
    public void removeCourseFromStudent(String studentId, String courseId) {
        String query = "DELETE FROM studentcourse WHERE studentId = ? AND courseId = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Find student by id in database
    public Student findStudentById(String studentId) {
        String query = "SELECT * FROM Student WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Log the studentId to ensure it is being passed correctly
            System.out.println("Searching for student with ID: " + studentId);
            
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Log the result to see if we are getting data
                System.out.println("Student found: " + rs.getString("id"));
                return new Student(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("id"),
                        rs.getString("className")
                );
            } else {
                // Log if no student is found
                System.out.println("No student found with ID: " + studentId);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no student is found
    }

    // Get student's courses
    public List<Course> getCoursesForStudent(String studentId) {
        String query = "SELECT c.courseName, c.courseId, c.creditHours " +
                       "FROM Course c " +
                       "JOIN StudentCourse sc ON c.courseId = sc.courseId " +
                       "WHERE sc.studentId = ?";
        
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set the studentId parameter
            stmt.setString(1, studentId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                // Retrieve course details from the result set
                String courseId = rs.getString("courseName");
                String courseName = rs.getString("courseId");
                double creditHours = rs.getDouble("creditHours");
                
                // Create a new Course object and add it to the list
                Course course = new Course(courseId, courseName, creditHours);
                courses.add(course);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courses;
    }
    
    // Update student information
    public void updateStudent(Student student) {
        String query = "UPDATE Student SET firstName = ?, lastName = ?, className = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getClassName());
            stmt.setString(4, student.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Enroll student to course
    public void addStudentCourse(Student student, Course course) {
        String query = "INSERT INTO StudentCourse (studentId, courseId) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set the studentId and courseId values
            stmt.setString(1, student.getId());
            stmt.setString(2, course.getCourseId());
            
            // Execute the insert query
            stmt.executeUpdate();
            System.out.println("Student with ID " + student.getId() + " added to course with ID " + course.getCourseId());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all student records from database
    public List<Student> getAllStudents() {
        String query = "SELECT * FROM Student";
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("id"),
                        rs.getString("className")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return students;
    }

}
