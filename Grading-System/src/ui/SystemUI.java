package ui;

import java.util.List;
import java.util.Optional;

import database.*;
import models.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SystemUI extends Application {

    private UserServiceDB userService = new UserServiceDB();
    private CourseServiceDB courseService = new CourseServiceDB();
    private StudentServiceDB studentService = new StudentServiceDB();
    private TeacherServiceDB teacherServiceDB = new TeacherServiceDB();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create login UI elements
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-background-color: #f4f4f9; -fx-border-radius: 5px; -fx-padding: 10px;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-background-color: #f4f4f9; -fx-border-radius: 5px; -fx-padding: 10px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 10px 20px;");

        // Create a VBox to hold UI elements
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);

        // Handle login button click
        loginButton.setOnAction(_ -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Authenticate the user
            User user = userService.authenticateUser(username, password);

            if (user != null) {
                // If authentication is successful, check the user's role and show the respective window
                Role role = user.getRole();
                switch (role) {
                    case Role.STUDENT:
                        showStudentWindow(user);
                        break;
                    case Role.TEACHER:
                        // showTeacherWindow(user);
                        break;
                    case Role.ADMIN:
                        // showAdminWindow(user);
                        break;
                    default:
                        showError("Invalid role assigned to user!");
                        break;
                }
            } else {
                // If authentication fails
                showError("Invalid username or password!");
            }
        });

        // Create scene and show login window
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setTitle("Student Grading System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showStudentWindow(User user) {
        // Create a new Stage for the student window
        Stage studentStage = new Stage();
        studentStage.setTitle("Student Grading System - Student Dashboard");

        // Label for the student's name
        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // TableView to display courses and grades
        TableView<Course> courseTableView = new TableView<>();

        // Define columns for course name and grade
        TableColumn<Course, String> courseIdColumn = new TableColumn<>("Course ID");
        courseIdColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(course.getCourseId());
        });

        TableColumn<Course, String> courseNameColumn = new TableColumn<>("Course Title");
        courseNameColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(course.getCourseName());
        });

        TableColumn<Course, String> courseECColumn = new TableColumn<>("EC");
        courseECColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(course.getCreditHours()));
        });

        TableColumn<Course, String> midTermColumn = new TableColumn<>("Midterm Grade");
        midTermColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(studentService.getGradesForStudentInCourse(user.getId(), course.getCourseId()).getMidtermGrade()));
        });

        TableColumn<Course, String> endTermColumn = new TableColumn<>("Endterm Grade");
        endTermColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(studentService.getGradesForStudentInCourse(user.getId(), course.getCourseId()).getEndTermGrade()));
        });

        TableColumn<Course, String> projectColumn = new TableColumn<>("Project Grade");
        projectColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(studentService.getGradesForStudentInCourse(user.getId(), course.getCourseId()).getProjectGrade()));
        });

        // Add "Drop" column with a button
        TableColumn<Course, Void> dropColumn = new TableColumn<>("Drop");
        dropColumn.setCellFactory(_ -> new TableCell<Course, Void>() {
            private final Button dropButton = new Button("❌"); // Use a cross emoji or an icon here

            {
                dropButton.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-text-fill: red;");
                dropButton.setOnAction(_ -> {
                    Course course = getTableView().getItems().get(getIndex());
                    // Show confirmation dialog
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirm Drop");
                    confirmationAlert.setHeaderText("Are you sure you want to drop the course?");
                    confirmationAlert.setContentText("This action cannot be undone.");

                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // Remove course from the student and update table
                        studentService.unassignCourseFromStudent(user.getId(), course.getCourseId());
                        getTableView().getItems().remove(course);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(dropButton);
                }
            }
        });

        courseTableView.getColumns().addAll(courseIdColumn, courseNameColumn, courseECColumn, midTermColumn, endTermColumn, projectColumn, dropColumn);

        // Fetch courses and grades for the student
        List<Course> courses = studentService.getCoursesForStudent(user.getId());
        courseTableView.getItems().addAll(courses);

        // Add a refresh button to refresh the course list
        Button refreshButton = new Button("Refresh Courses");
        refreshButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(_ -> {
            courseTableView.getItems().clear();
            List<Course> updatedCourses = studentService.getCoursesForStudent(user.getId());
            courseTableView.getItems().addAll(updatedCourses);
        });

        // Create a VBox to hold the UI elements
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(welcomeLabel, courseTableView, refreshButton);

        // Create scene and show student window
        Scene scene = new Scene(vbox, 800, 500);
        studentStage.setScene(scene);
        studentStage.show();
    }


    

    // Function to show error message in a dialog
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
