package database;

import models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.Statement;

public class UserServiceDB {

    // Method to authenticate the user
    public User authenticateUser(String username, String password) {
        // SQL query to find the user by username
        String query = "SELECT user_id, first_name, last_name, username, password, role_id FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            // Check if the user exists and if the password matches
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                // In a real system, you would compare hashed passwords, not plain text
                if (password.equals(storedPassword)) {
                    // Convert the role id to Role enum
                    int roleId = rs.getInt("role_id");
                    Role role = Role.getById(roleId);  // Convert role id to Role enum
                    
                    // Create and return User object with the details from the database
                    return new User(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("user_id"),
                        rs.getString("username"),
                        storedPassword,
                        role
                    );
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Return null if authentication fails
        return null;
    }

    // Add a student to the database
    public void addStudent(Student student) {
        String userQuery = "INSERT INTO users (first_name, last_name, user_id, username, password, role_id) VALUES (?, ?, ?, ?, ?, ?)";
        String studentQuery = "INSERT INTO student (id, className, user_id) VALUES (?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement studentStmt = conn.prepareStatement(studentQuery)) {
    
            // Insert into the users table
            userStmt.setString(1, student.getFirstName());
            userStmt.setString(2, student.getLastName());
            userStmt.setString(3, student.getId());
            userStmt.setString(4, student.getUsername());
            userStmt.setString(5, student.getPassword()); 
            userStmt.setInt(6, student.getRoleId());
            userStmt.executeUpdate();
    
            // Get the generated user_id from the users table
            try (ResultSet rs = userStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    String userId = rs.getString(1);
    
                    // Insert into the student table
                    studentStmt.setString(1, student.getId());
                    studentStmt.setString(3, userId);
                    studentStmt.executeUpdate();
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Add a teacher to the database
    public void addTeacher(Teacher teacher) {
        String userQuery = "INSERT INTO users (first_name, last_name, username, password, role_id) VALUES (?, ?, ?, ?, ?)";
        String teacherQuery = "INSERT INTO teacher (id, department, user_id) VALUES (?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery)) {
    
            // Insert into the users table
            userStmt.setString(1, teacher.getFirstName());
            userStmt.setString(2, teacher.getLastName());
            userStmt.setString(3, teacher.getUsername()); 
            userStmt.setString(4, teacher.getPassword());
            userStmt.setInt(5, teacher.getRole().getId());
            userStmt.executeUpdate();
    
            // Get the generated user_id from the users table
            try (ResultSet rs = userStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    String userId = rs.getString(1);
    
                    // Insert into the teacher table
                    teacherStmt.setString(1, teacher.getId());
                    teacherStmt.setString(2, teacher.getDepartment());
                    teacherStmt.setString(3, userId);
                    teacherStmt.executeUpdate();
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Delete student from database
    public void deleteStudent(String studentId) {
        // Delete the associated records in the studentcourse table first
        String deleteStudentCourseQuery = "DELETE FROM studentcourse WHERE studentId = ?";
        String deleteGradeQuery = "DELETE FROM Grade WHERE studentId = ?";
        String deleteStudentQuery = "DELETE FROM student WHERE id = ?";
        String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            // Delete records from studentcourse table
            try (PreparedStatement stmt = conn.prepareStatement(deleteStudentCourseQuery)) {
                stmt.setString(1, studentId);
                stmt.executeUpdate();
            }

            // Delete records from grades table
            try (PreparedStatement stmt = conn.prepareStatement(deleteGradeQuery)) {
                stmt.setString(1, studentId);
                stmt.executeUpdate();
            }

            // Delete the student record from the Student table
            try (PreparedStatement stmt = conn.prepareStatement(deleteStudentQuery)) {
                stmt.setString(1, studentId);
                stmt.executeUpdate();
            }

            // Delete the student record from the Users table
            try (PreparedStatement stmt = conn.prepareStatement(deleteUserQuery)) {
                stmt.setString(1, studentId); // Make sure studentId corresponds to user_id
                stmt.executeUpdate();
            }

            System.out.println("Student and associated records deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete teacher from database
    public void deleteTeacher(String teacherId) {
        // Queries to delete associated records
        String deleteTeacherCourseQuery = "DELETE FROM teacher_course WHERE teacher_id = ?";
        String deleteTeacherQuery = "DELETE FROM teacher WHERE id = ?";
        String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            // Delete records from teacher_course table
            try (PreparedStatement stmt = conn.prepareStatement(deleteTeacherCourseQuery)) {
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

    // Add grade for a student in a course
    public void assignGrade(String studentId, String courseId, Grade grade) {
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
            stmt.setDouble(3, grade.getMidtermGrade());
            stmt.setDouble(4, grade.getEndTermGrade());
            stmt.setDouble(5, grade.getProjectGrade());
            stmt.executeUpdate();
            System.out.println("Grades assigned successfully for student " + studentId + " in course " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Remove course from student in database
    public void removeCourseFromStudent(String studentId, String courseId) {
        String gradeQuery = "DELETE FROM Grade WHERE studentId = ? AND courseId = ?";
        String studentCourseQuery = "DELETE FROM studentcourse WHERE studentId = ? AND courseId = ?";
    
        try (Connection conn = DatabaseConnection.connect()) {
            // Delete from Grade table
            try (PreparedStatement stmt = conn.prepareStatement(gradeQuery)) {
                stmt.setString(1, studentId);
                stmt.setString(2, courseId);
                stmt.executeUpdate();
            }
    
            // Delete from studentcourse table
            try (PreparedStatement stmt = conn.prepareStatement(studentCourseQuery)) {
                stmt.setString(1, studentId);
                stmt.setString(2, courseId);
                stmt.executeUpdate();
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Find student by ID in database
    public Student findStudentById(String studentId) {
        String query = "SELECT s.user_id, u.first_name, u.last_name, u.username, u.password " +
               "FROM Student s " +
               "JOIN users u ON s.user_id = u.user_id " +
               "WHERE s.id = ?";


        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            // Log the studentId to ensure it is being passed correctly
            System.out.println("Searching for student with ID: " + studentId);

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Log the result to see if we are getting data
                System.out.println("Student found: " + rs.getString("id"));

                // Create a new Student object using data from both Student and users tables
                return new Student(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password")
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
    
    // Get student's grades for a course
    public Grade getGradesForStudentInCourse(String studentId, String courseId) {
        String query = "SELECT midtermGrade, endTermGrade, projectGrade " +
                       "FROM Grade " +
                       "WHERE studentId = ? AND courseId = ?";
            
        Grade grades = null;
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set the studentId and courseId parameters
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Retrieve grades from the result set
                double midtermGrade = rs.getDouble("midtermGrade");
                double endTermGrade = rs.getDouble("endTermGrade");
                double projectGrade = rs.getDouble("projectGrade");
                
                grades = new Grade(midtermGrade, endTermGrade, projectGrade);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return grades;
    }

    // Update student information
    public void updateStudent(Student student) {
        String updateUserQuery = "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ? WHERE user_id = ?";
        String updateStudentQuery = "UPDATE Student SET className = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement updateUserStmt = conn.prepareStatement(updateUserQuery);
            PreparedStatement updateStudentStmt = conn.prepareStatement(updateStudentQuery)) {
            
            // Update the users table with the new user information
            updateUserStmt.setString(1, student.getFirstName());
            updateUserStmt.setString(2, student.getLastName());
            updateUserStmt.setString(3, student.getId()); 
            updateUserStmt.setString(4, student.getUsername()); 
            updateUserStmt.executeUpdate();
            
            // Update the Student table with the new className information
            updateStudentStmt.setString(2, student.getId());
            updateStudentStmt.executeUpdate();
            
            System.out.println("Student and associated user information updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Enroll student to course and initialize grades
    public void addStudentCourse(Student student, Course course) {
        String studentCourseQuery = "INSERT INTO StudentCourse (studentId, courseId) VALUES (?, ?)";
        String gradeInitializationQuery = "INSERT INTO Grade (studentId, courseId, midtermGrade, endTermGrade, projectGrade) VALUES (?, ?, 0, 0, 0)";
        
        try (Connection conn = DatabaseConnection.connect()) {
            // Add student to the course
            try (PreparedStatement stmt = conn.prepareStatement(studentCourseQuery)) {
                stmt.setString(1, student.getId());
                stmt.setString(2, course.getCourseId());
                stmt.executeUpdate();
                System.out.println("Student with ID " + student.getId() + " added to course with ID " + course.getCourseId());
            }
            
            // Initialize grades for the student in the course
            try (PreparedStatement stmt = conn.prepareStatement(gradeInitializationQuery)) {
                stmt.setString(1, student.getId());
                stmt.setString(2, course.getCourseId());
                stmt.executeUpdate();
                System.out.println("Grades initialized for student with ID " + student.getId() + " in course with ID " + course.getCourseId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all student records from database
    public List<Student> getAllStudents() {
        String query = "SELECT s.id, s.className, u.first_name, u.last_name, u.username, u.password " +
                    "FROM Student s " +
                    "JOIN users u ON s.user_id = u.user_id";
        
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password")
                        
                );
                student.setUsername(rs.getString("username"));
                student.setPassword(rs.getString("password"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }


}
