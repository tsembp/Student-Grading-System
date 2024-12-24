package services;

import models.Student;
import java.util.HashMap;
import java.util.Map;

public class StudentService {
    private Map<String, Student> students = new HashMap<>();

    public void addStudent(Student student) {
        students.put(student.getId(), student);
    }

    public Student findStudentById(String studentId) {
        return students.get(studentId);
    }

    public Map<String, Student> getAllStudents() {
        return students;
    }
}
