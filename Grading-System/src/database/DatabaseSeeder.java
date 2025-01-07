package database;

import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class DatabaseSeeder {

    private static final TeacherServiceDB teacherService = new TeacherServiceDB();
    private static final StudentServiceDB studentService = new StudentServiceDB();
    private static final CourseServiceDB courseService = new CourseServiceDB();
    
    public static void seedDatabase() {
        System.out.println("Seeding the database with unique records...");

        // Create Teachers with simpler but realistic passwords
        Teacher[] teachers = {
            new Teacher("Emma", "Johnson", "T1001", "ejohnson", "emma123", "Computer Science"),
            new Teacher("Liam", "Williams", "T1002", "lwilliams", "liam123", "Mathematics"),
            new Teacher("Olivia", "Brown", "T1003", "obrown", "olivia123", "Physics"),
            new Teacher("Noah", "Davis", "T1004", "ndavis", "noah123", "Engineering"),
            new Teacher("Sophia", "Miller", "T1005", "smiller", "sophia123", "Statistics")
        };

        for (Teacher teacher : teachers) {
            teacherService.addTeacher(teacher);
        }

        // Create Courses and Assign to Teachers (Ensuring 1-3 Courses per Teacher)
        Course[] courses = {
            new Course("Discrete Mathematics", "DM101", 6.0, teachers[0]),
            new Course("Linear Algebra", "LA102", 5.5, teachers[0]),
            new Course("Calculus II", "CA103", 5.0, teachers[0]),  // 3 Courses for Emma

            new Course("Quantum Physics", "QP201", 7.0, teachers[2]),
            new Course("Classical Mechanics", "CM202", 6.5, teachers[2]), // 2 Courses for Olivia

            new Course("Software Engineering", "SE401", 6.5, teachers[3]), // 1 Course for Noah

            new Course("Advanced Statistics", "AS301", 8.0, teachers[4]),
            new Course("Probability Theory", "PT302", 7.0, teachers[4]) // 2 Courses for Sophia

        };

        for (Course course : courses) {
            courseService.createCourse(course);
            teacherService.assignCourseToTeacher(course.getTeacher().getId(), course.getCourseId());
        }

        // Create Students with simpler passwords
        Student[] students = {
            new Student("James", "Anderson", "S3001", "janderson", "james123"),
            new Student("Ava", "Martinez", "S3002", "amartinez", "ava123"),
            new Student("Benjamin", "Garcia", "S3003", "bgarcia", "ben123"),
            new Student("Mia", "Rodriguez", "S3004", "mrodriguez", "mia123"),
            new Student("Lucas", "Martinez", "S3005", "lmartinez", "lucas123"),
            new Student("Amelia", "Hernandez", "S3006", "ahernandez", "amelia123"),
            new Student("Henry", "Lopez", "S3007", "hlopez", "henry123"),
            new Student("Evelyn", "Gonzalez", "S3008", "egonzalez", "evelyn123"),
            new Student("Alexander", "Perez", "S3009", "aperez", "alex123"),
            new Student("Charlotte", "Rivera", "S3010", "crivera", "char123")
        };

        for (Student student : students) {
            studentService.addStudent(student);
        }

        // Enroll Students in Courses and Assign Grades
        Random random = new Random();
        for (Student student : students) {
            for (Course course : courses) {
                studentService.assignCourseToStudent(student, course);
                Grade grade = new Grade(
                    // grades between 55 and 100 (2 decimals)
                    Math.round((55 + (random.nextDouble() * 45)) * 100.0) / 100.0,
                    Math.round((55 + (random.nextDouble() * 45)) * 100.0) / 100.0,
                    Math.round((55 + (random.nextDouble() * 45)) * 100.0) / 100.0
                );
                studentService.assignGradeToStudent(student.getId(), course.getCourseId(), grade);
            }
        }

        System.out.println("Database seeding with adjusted teacher-course relationships complete!");
    }

    public static void clearDatabase() {
        System.out.println("Clearing all records from the database...");

        String[] deleteQueries = {
            "DELETE FROM grade",
            "DELETE FROM studentcourse",
            "DELETE FROM student",
            "DELETE FROM course",
            "DELETE FROM teacher",
            "DELETE FROM users"
        };

        try (Connection conn = DatabaseConnection.connect()) {
            for (String query : deleteQueries) {
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.executeUpdate();
                }
            }
            System.out.println("All records have been deleted from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while deleting records.");
        }
    }

}