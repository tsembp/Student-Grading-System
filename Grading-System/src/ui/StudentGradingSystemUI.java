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

        // Center: Tabs for Students, Courses, and Grading
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createStudentTab(), createCourseTab(), createGradeTab());
        mainLayout.setCenter(tabPane);

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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

        Button addStudentButton = new Button("Add Student");
        addStudentButton.setOnAction(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            if (!name.isEmpty() && !id.isEmpty()) {
                studentService.addStudent(new Student(name, id));
                nameField.clear();
                idField.clear();
                showAlert("Success", "Student added successfully!");
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        });

        studentLayout.getChildren().addAll(
                new Label("Add Student:"),
                nameField,
                idField,
                addStudentButton
        );

        // List Students Section
        Button listStudentsButton = new Button("List Students");
        TextArea studentListArea = new TextArea();
        studentListArea.setEditable(false);

        listStudentsButton.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Student student : studentService.getAllStudents().values()) {
                sb.append(student).append("\n");
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
    
        // List Courses Section
        Button listCoursesButton = new Button("List Courses");
        TextArea courseListArea = new TextArea();
        courseListArea.setEditable(false);
    
        listCoursesButton.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Course course : courseService.getAllCourses().values()) {
                sb.append(course).append("\n");
            }
            courseListArea.setText(sb.toString());
        });
    
        courseLayout.getChildren().addAll(
                new Label("Courses:"),
                listCoursesButton,
                courseListArea
        );
    
        courseTab.setContent(courseLayout);
        return courseTab;
    }    

    private Tab createGradeTab() {
        Tab gradeTab = new Tab("Assign Grades");
        gradeTab.setClosable(false);

        VBox gradeLayout = new VBox(10);
        gradeLayout.setStyle("-fx-padding: 10;");

        // Assign Grade Section
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Course ID");
        TextField gradeField = new TextField();
        gradeField.setPromptText("Grade");

        Button assignGradeButton = new Button("Assign Grade");
        assignGradeButton.setOnAction(e -> {
            String studentId = studentIdField.getText();
            String courseId = courseIdField.getText();
            double grade = Double.parseDouble(gradeField.getText());

            Student student = studentService.findStudentById(studentId);
            if (student != null) {
                for (Course course : student.getCourses()) {
                    if (course.getCourseId().equals(courseId)) {
                        courseService.assignGrade(course, grade);
                        showAlert("Success", "Grade assigned successfully!");
                    }
                }
            } else {
                showAlert("Error", "Student not found.");
            }
        });

        gradeLayout.getChildren().addAll(
                new Label("Assign Grade:"),
                studentIdField,
                courseIdField,
                gradeField,
                assignGradeButton
        );

        gradeTab.setContent(gradeLayout);
        return gradeTab;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
