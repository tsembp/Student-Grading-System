package ui;

import models.Student;
import models.Course;
import services.StudentService;
import services.CourseService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StudentGradingSystemUI extends Application {

    private StudentService studentService = new StudentService();
    private CourseService courseService = new CourseService();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Grading System");

        // Main layout
        BorderPane mainLayout = new BorderPane();

        // Top: Application Title
        Label titleLabel = new Label("Student Grading System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        VBox topSection = new VBox(titleLabel);
        topSection.setStyle("-fx-alignment: center; -fx-padding: 10;");
        mainLayout.setTop(topSection);

        // Center: Tabs for Students and Courses
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createStudentTab(), createCourseTab());
        mainLayout.setCenter(tabPane);

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create Student Tab
    private Tab createStudentTab() {
        Tab studentTab = new Tab("Manage Students");
        studentTab.setClosable(false);

        VBox studentLayout = new VBox(10);
        studentLayout.setStyle("-fx-padding: 10;");

        // Add Student Section
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField idField = new TextField();
        idField.setPromptText("ID");
        TextField classField = new TextField();
        classField.setPromptText("Class");

        Button addStudentButton = new Button("Add Student");
        addStudentButton.setOnAction(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            String className = classField.getText();
            if (!name.isEmpty() && !id.isEmpty() && !className.isEmpty()) {
                studentService.addStudent(new Student(name, id, className));
                nameField.clear();
                idField.clear();
                classField.clear();
                showAlert("Success", "Student added successfully!");
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        });

        studentLayout.getChildren().addAll(
                new Label("Add Student:"),
                nameField,
                idField,
                classField,
                addStudentButton
        );

        // List Students Section
        Button listStudentsButton = new Button("List Students");
        TextArea studentListArea = new TextArea();
        studentListArea.setEditable(false);

        // List Students Section
        listStudentsButton.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Student student : studentService.getAllStudents().values()) {
                sb.append(student.toString());  // Use the overridden toString method
                sb.append("\n");
            }

            studentListArea.setText(sb.toString());
        });


        studentLayout.getChildren().addAll(
                new Label("Students:"),
                listStudentsButton,
                studentListArea
        );

        studentTab.setContent(studentLayout);
        return studentTab;
    }

    // Create Course Tab
    private Tab createCourseTab() {
        Tab courseTab = new Tab("Manage Courses");
        courseTab.setClosable(false);

        VBox courseLayout = new VBox(10);
        courseLayout.setStyle("-fx-padding: 10;");

        // Add Course Section
        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Course Name");
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Course ID");

        Button addCourseButton = new Button("Add Course");
        addCourseButton.setOnAction(e -> {
            String name = courseNameField.getText();
            String id = courseIdField.getText();
            if (!name.isEmpty() && !id.isEmpty()) {
                courseService.createCourse(name, id);
                showAlert("Success", "Course added successfully!");
                courseNameField.clear();
                courseIdField.clear();
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        });

        courseLayout.getChildren().addAll(
                new Label("Add Course:"),
                courseNameField,
                courseIdField,
                addCourseButton
        );

        // Assign Course to Student Section
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");
        TextField courseIdForStudentField = new TextField();
        courseIdForStudentField.setPromptText("Course ID");

        Button assignCourseButton = new Button("Assign Course to Student");
        assignCourseButton.setOnAction(e -> {
            String studentId = studentIdField.getText();
            String courseId = courseIdForStudentField.getText();
            Student student = studentService.findStudentById(studentId);
            Course course = courseService.getCourseById(courseId);
            if (student != null && course != null) {
                student.addCourse(course);
                showAlert("Success", "Course assigned to student successfully!");
            } else {
                showAlert("Error", "Student or Course not found.");
            }
        });

        courseLayout.getChildren().addAll(
                new Label("Assign Course to Student:"),
                studentIdField,
                courseIdForStudentField,
                assignCourseButton
        );

        // Assign Grade Section
        TextField gradeField = new TextField();
        gradeField.setPromptText("Grade");

        Button assignGradeButton = new Button("Assign Grade");
        assignGradeButton.setOnAction(e -> {
            String studentId = studentIdField.getText();
            String courseId = courseIdForStudentField.getText();
            double grade = Double.parseDouble(gradeField.getText());
            Student student = studentService.findStudentById(studentId);
            Course course = courseService.getCourseById(courseId);
            if (student != null && course != null) {
                courseService.assignGradeToCourse(student, course, grade);
                showAlert("Success", "Grade assigned successfully!");
            } else {
                showAlert("Error", "Student or Course not found.");
            }
        });

        courseLayout.getChildren().addAll(
                new Label("Assign Grade:"),
                gradeField,
                assignGradeButton
        );

        courseTab.setContent(courseLayout);
        return courseTab;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
