package ui;

import models.Student;
import models.Course;
import services.StudentService;
import services.CourseService;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StudentGradingSystemUI extends Application {

    private static final String ACCESS_PIN = "1234";  // pin for user login

    private StudentService studentService = new StudentService();
    private CourseService courseService = new CourseService();

    @Override
    public void start(Stage primaryStage) {

        // User authentication page
        if (!showLoginScreen(primaryStage)) {
            return;  // If authentication fails, exit the application
        }

        primaryStage.setTitle("Student Grading System");

        BorderPane mainLayout = new BorderPane();

        // Top: Application Title
        Label titleLabel = new Label("Student Grading System");
        titleLabel.getStyleClass().add("title-label");
        VBox topSection = new VBox(titleLabel);
        topSection.setStyle("-fx-alignment: center; -fx-padding: 10;");
        mainLayout.setTop(topSection);

        // Center: Tabs for Students and Courses
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createStudentTab(), createCourseTab(), manageStudentTab());
        mainLayout.setCenter(tabPane);

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 650);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Login Page
    private boolean showLoginScreen(Stage primaryStage) {
        // Create the login dialog
        TextInputDialog pinDialog = new TextInputDialog();
        pinDialog.setTitle("Login");
        pinDialog.setHeaderText("Please enter the PIN to continue:");
        pinDialog.setContentText("PIN:");

        // Show the dialog and capture the result
        String enteredPin = pinDialog.showAndWait().orElse("");

        if (enteredPin.equals(ACCESS_PIN)) {
            return true;  // Successful authentication
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Authentication Failed");
            alert.setHeaderText("Incorrect PIN");
            alert.setContentText("Please enter the correct PIN to access the system.");
            alert.showAndWait();
            return false;  // Authentication failed
        }
    }

    // Create Student Tab
    private Tab createStudentTab() {
        Tab studentTab = new Tab("Add Students");
        studentTab.setClosable(false);

        VBox studentLayout = new VBox(10);
        studentLayout.setStyle("-fx-padding: 10;");

        // Add Labels
        Label addLabel = new Label("Enter Student's Details to Add:");
        addLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label listLabel = new Label("List of Students:");
        listLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Add Student Section
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("input-field"); 
        TextField idField = new TextField();
        idField.setPromptText("ID");
        idField.getStyleClass().add("input-field"); 
        TextField classField = new TextField();
        classField.setPromptText("Class");
        classField.getStyleClass().add("input-field"); 

        Button addStudentButton = new Button("Add Student");
        addStudentButton.getStyleClass().add("button");
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
                addLabel,
                nameField,
                idField,
                classField,
                addStudentButton
        );

        // List Students Section
        Button listStudentsButton = new Button("View");
        listStudentsButton.getStyleClass().add("button"); 
        TextArea studentListArea = new TextArea();
        studentListArea.setEditable(false);
        studentListArea.getStyleClass().add("textarea"); 

        // List Students Section
        listStudentsButton.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            if(studentService.getAllStudents().isEmpty()) {
                sb.append("No students in the system.");
            } else{
                for (Student student : studentService.getAllStudents().values()) {
                    sb.append(student.toString());
                    sb.append("\n");
                }
            }

            studentListArea.setText(sb.toString());
        });

        studentLayout.getChildren().addAll(
                listLabel,
                listStudentsButton,
                studentListArea
        );

        studentTab.setContent(studentLayout);
        return studentTab;
    }

    private Tab manageStudentTab() {
        Tab studentTab = new Tab("Manage Students");
        studentTab.setClosable(false);
    
        VBox studentLayout = new VBox(10);
        studentLayout.setStyle("-fx-padding: 10;");
    
        // Add Label above the input field
        Label searchLabel = new Label("Enter Student ID to Search:");
        searchLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    
        // Add Student Section
        TextField idField = new TextField();
        idField.setPromptText("ID");
        idField.getStyleClass().add("input-field");
    
        Button addStudentButton = new Button("Search Student");
        addStudentButton.getStyleClass().add("button");
        addStudentButton.setOnAction(e -> {
            String id = idField.getText();
            if (!id.isEmpty()) {
                Student student = studentService.findStudentById(id);
                if (student != null) {
                    displayStudentOptions(studentLayout, student, idField, addStudentButton);
                } else {
                    showAlert("Error", "Student not found.");
                }
    
                idField.clear();
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        });
    
        // Add label and input field to the layout
        studentLayout.getChildren().addAll(searchLabel, idField, addStudentButton);
        studentTab.setContent(studentLayout);
        return studentTab;
    }
    
    private void displayStudentOptions(VBox studentLayout, Student student, TextField idField, Button addStudentButton) {
        // Clear previous content
        studentLayout.getChildren().clear();
    
        // Add return button to go back to search section
        Button returnButton = new Button("Back");
        returnButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-underline: true;");
        returnButton.setOnAction(e -> {
            // Clear the current student options and show the search section again
            studentLayout.getChildren().clear();
            studentLayout.getChildren().addAll(idField, addStudentButton);
        });
    
        studentLayout.getChildren().add(returnButton);
    
        // Show student info
        Text studentInfo = new Text("Student: " + student.getName() + " (ID: " + student.getId() + ")");
        studentLayout.getChildren().add(studentInfo);
    
        // Section for updating student info (Name, ID, Class)
        TextField nameField = new TextField(student.getName());
        nameField.setPromptText("Name");
    
        TextField idField2 = new TextField(student.getId());
        idField2.setPromptText("ID");
    
        TextField classField = new TextField(student.getClassName());
        classField.setPromptText("Class");
    
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            student.setName(nameField.getText());
            student.setId(idField2.getText());
            student.setClassName(classField.getText());
            studentService.updateStudent(student);
            showAlert("Success", "Student information updated.");
        });
    
        VBox updateSection = new VBox(5, nameField, idField2, classField, saveButton);
        studentLayout.getChildren().add(updateSection);
    
        // Section for adding/removing courses
        VBox coursesSection = new VBox(5);
        Text coursesTitle = new Text("Courses:");
        coursesSection.getChildren().add(coursesTitle);

        // For each course, display the course name with a red X for removal
        for (Course course : student.getCourses()) {
            HBox courseRow = new HBox(10); // Align course and button horizontally
            Text courseId = new Text(course.getCourseId());
            Text courseName = new Text(course.getName());

            // Red X button for course removal
            Button removeCourseButton = new Button("❌");
            removeCourseButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 0;");
            removeCourseButton.setPrefSize(20, 20);  // Set size for the red X button
            removeCourseButton.setOnAction(e -> {
                student.getCourses().remove(course); // Remove course from student's courses list
                studentService.updateStudent(student); // Update student record
                displayStudentOptions(studentLayout, student, idField, addStudentButton); // Refresh the course list
            });

            // Add the "X" button to the left of the course name
            HBox.setHgrow(removeCourseButton, Priority.ALWAYS);
            courseRow.getChildren().addAll(removeCourseButton, courseId, courseName);
            coursesSection.getChildren().add(courseRow);
        }

        studentLayout.getChildren().add(coursesSection);
    
        // Create a VBox to hold the delete button
        VBox buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);  // Align the button to the bottom center
    
        // Delete student button (at the very bottom)
        Button deleteButton = new Button("Delete Student");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            studentService.deleteStudent(student.getId());
            studentLayout.getChildren().clear();
            studentLayout.getChildren().clear();
            studentLayout.getChildren().addAll(idField, addStudentButton);
            showAlert("Success", "Student deleted successfully.");
        });
    
        // Add the button to the container
        buttonContainer.getChildren().add(deleteButton);
    
        // Add the container to the layout
        studentLayout.getChildren().add(buttonContainer);
    }
    
    private void manageCourses(Student student) {
        // Implement the logic for adding/removing courses for the student
        System.out.println("Manage courses for " + student.getName());
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
        courseNameField.getStyleClass().add("input-field"); 
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Course ID");
        courseIdField.getStyleClass().add("input-field");

        Button addCourseButton = new Button("Add Course");
        addCourseButton.getStyleClass().add("button");
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
        studentIdField.getStyleClass().add("input-field"); 
        TextField courseIdForStudentField = new TextField();
        courseIdForStudentField.setPromptText("Course ID");
        courseIdForStudentField.getStyleClass().add("input-field"); 

        Button assignCourseButton = new Button("Assign Course to Student");
        assignCourseButton.getStyleClass().add("button");
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

        // Add fields for midterm, endterm, and project grades
        TextField midtermGradeField = new TextField();
        midtermGradeField.setPromptText("Midterm Grade");
        midtermGradeField.getStyleClass().add("input-field");

        TextField endtermGradeField = new TextField();
        endtermGradeField.setPromptText("Endterm Grade");
        endtermGradeField.getStyleClass().add("input-field");

        TextField projectGradeField = new TextField();
        projectGradeField.setPromptText("Project Grade");
        projectGradeField.getStyleClass().add("input-field");

        Button assignGradeButton = new Button("Assign Grades");
        assignGradeButton.getStyleClass().add("button"); 
        assignGradeButton.setOnAction(e -> {
            String studentId = studentIdField.getText();
            String courseId = courseIdForStudentField.getText();
            try {
                double midtermGrade = Double.parseDouble(midtermGradeField.getText());
                double endtermGrade = Double.parseDouble(endtermGradeField.getText());
                double projectGrade = Double.parseDouble(projectGradeField.getText());
                Student student = studentService.findStudentById(studentId);
                Course course = courseService.getCourseById(courseId);
                if (student != null && course != null) {
                    // Assign grades to course
                    courseService.assignGradeToCourse(student, course, midtermGrade, endtermGrade, projectGrade);
                    showAlert("Success", "Grades assigned successfully!");
                } else {
                    showAlert("Error", "Student or Course not found.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid grades.");
            }
        });

        courseLayout.getChildren().addAll(
                new Label("Assign Grades:"),
                midtermGradeField,
                endtermGradeField,
                projectGradeField,
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
