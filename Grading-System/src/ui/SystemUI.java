package ui;

import database.*;
import models.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SystemUI extends Application {

    private UserServiceDB userService = new UserServiceDB();
    private CourseServiceDB courseService = new CourseServiceDB();
    private StudentServiceDB studentService = new StudentServiceDB();
    private TeacherServiceDB teacherService = new TeacherServiceDB();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // User u = userService.authenticateUser("admin", "adminPass123");
        User u = userService.authenticateUser("bRoss", "teacherPass123");
        // showAdminWindow(user);

        // Create login UI elements
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #d1d1d1;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-background-color:rgb(237, 237, 237); -fx-border-radius: 5px; -fx-padding: 10px;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-background-color:rgb(237, 237, 237); -fx-border-radius: 5px; -fx-padding: 10px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 14px; -fx-background-color: #00695c; -fx-text-fill: #d1d1d1; -fx-border-radius: 10px; -fx-padding: 10px 20px;");

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: #2c2f38;");
        
        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);
        

        // Handle login button click
        loginButton.setOnAction(_ -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Authenticate the user
            // User user = userService.authenticateUser(username, password);
            User user = u;

            if (user != null) {
                // If authentication is successful, check the user's role and show the respective window
                Role role = user.getRole();
                switch (role) {
                    case Role.STUDENT:
                        StudentWindowUI studentWindowUI = new StudentWindowUI(user);
                        studentWindowUI.showStudentWindow(user);
                        break;
                    case Role.TEACHER:
                        TeacherWindowUI teacherWindowUI = new TeacherWindowUI(user);
                        teacherWindowUI.showTeacherWindow();
                        break;
                    case Role.ADMIN:
                        AdminWindowUI adminWindowUI = new AdminWindowUI(user);
                        adminWindowUI.showAdminWindow(user);
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
        scene.setFill(Color.web("#2c2f38"));
        primaryStage.setTitle("Student Grading System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Alert box
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
