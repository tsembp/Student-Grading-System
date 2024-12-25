package services;

import models.Student;
import java.util.HashMap;
import java.util.Map;

public class StudentService {

    private Map<String, Student> students;

    public StudentService() {
        students = new HashMap<>();
    }

    // Add a student to the system
    public void addStudent(Student student) {
        students.put(student.getId(), student);
    }

    // Find a student by ID
    public Student findStudentById(String studentId) {
        return students.get(studentId);
    }

    // Get all students
    public Map<String, Student> getAllStudents() {
        return students;
    }

    // Update student information
    public void updateStudent(Student student){
        students.put(student.getId(), student);
    }
}
