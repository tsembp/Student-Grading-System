package database;

import models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        String studentQuery = "INSERT INTO student (id, user_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            PreparedStatement studentStmt = conn.prepareStatement(studentQuery)) {
        
            // Insert into the users table
            userStmt.setString(1, student.getFirstName());
            userStmt.setString(2, student.getLastName());
            userStmt.setString(3, student.getId());
            userStmt.setString(4, student.getUsername());
            userStmt.setString(5, student.getPassword()); 
            userStmt.setInt(6, student.getRoleId());
            userStmt.executeUpdate();
        
            // Insert into the student table
            studentStmt.setString(1, student.getId());
            studentStmt.setString(2, student.getId());
            studentStmt.executeUpdate();
        
            System.out.println("Student added successfully");
        
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

    // Delete teacher from database
    public void deleteTeacher(String teacherId) {
        // Queries to delete associated records
        String deleteTeacherCourseQuery = "DELETE FROM teachercourse WHERE teacher_id = ?";
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

    // Assign teacher to course
    public void assignCourseToTeacher(String teacherId, String courseId) {
        String checkCourseAssignedQuery = "SELECT COUNT(*) FROM teachercourse WHERE teacher_id = ? AND course_id = ?";
        String assignCourseQuery = "INSERT INTO teachercourse (teacher_id, course_id) VALUES (?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkCourseAssignedQuery);
             PreparedStatement assignStmt = conn.prepareStatement(assignCourseQuery)) {
    
            // Check if the course is already assigned to the teacher
            checkStmt.setString(1, teacherId);
            checkStmt.setString(2, courseId);
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
        String checkCourseAssignedQuery = "SELECT COUNT(*) FROM teachercourse WHERE teacher_id = ? AND course_id = ?";
        String unassignCourseQuery = "DELETE FROM teachercourse WHERE teacher_id = ? AND course_id = ?";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement checkStmt = conn.prepareStatement(checkCourseAssignedQuery);
            PreparedStatement unassignStmt = conn.prepareStatement(unassignCourseQuery)) {

            // Check if the course is assigned to the teacher
            checkStmt.setString(1, teacherId);
            checkStmt.setString(2, courseId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
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
        String query = "SELECT c.courseName, c.courseId, c.creditHours " +
                       "FROM course c " +
                       "JOIN teachercourse tc ON c.courseId = tc.course_id " +
                       "WHERE tc.teacher_id = ?";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            // Log the teacherId to ensure it is being passed correctly
            System.out.println("Fetching courses for teacher with ID: " + teacherId);
    
            stmt.setString(1, teacherId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                // Create a Course object for each row in the result set
                Course course = new Course(
                        rs.getString("courseName"),
                        rs.getString("courseId"),
                        rs.getDouble("creditHours")
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
                System.out.println("Student found: " + rs.getString("user_id"));

                // Create a new Student object using data from both Student and users tables
                return new Student(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("user_id"),
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
