package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.*;
import models.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SystemUI extends Application {

    private UserServiceDB userService = new UserServiceDB();

    public  static void createDefaultUsers() {
        // SQL queries for inserting the users into the users table and their respective role tables
        String insertUserQuery = "INSERT INTO users (user_id, first_name, last_name, username, password, role_id) VALUES (?, ?, ?, ?, ?, ?)";
        String insertStudentQuery = "INSERT INTO student (id, user_id) VALUES (?, ?)";
        String insertTeacherQuery = "INSERT INTO teacher (id, department, user_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.connect()) {
            // Create Admin user
            try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
                stmt.setString(1, "admin123");
                stmt.setString(2, "Admin");
                stmt.setString(3, "User");
                stmt.setString(4, "admin");
                stmt.setString(5, "admin123");
                stmt.setInt(6, Role.ADMIN.getId());
                stmt.executeUpdate();
            }

            // Create Student user
            try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
                stmt.setString(1, "student123");
                stmt.setString(2, "Panagiotis");
                stmt.setString(3, "Tsembekis");
                stmt.setString(4, "tsembp");
                stmt.setString(5, "studentPassword123");
                stmt.setInt(6, Role.STUDENT.getId());
                stmt.executeUpdate();
            }

            // Insert student into student table
            try (PreparedStatement stmt = conn.prepareStatement(insertStudentQuery)) {
                stmt.setString(1, "student123");
                stmt.setString(2, "student123");
                stmt.executeUpdate();
            }

            // Create Teacher user
            try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
                stmt.setString(1, "teacher123");
                stmt.setString(2, "Lysandros");
                stmt.setString(3, "Georgiou");
                stmt.setString(4, "lysandrosg");
                stmt.setString(5, "teacherPassword123");
                stmt.setInt(6, Role.TEACHER.getId());
                stmt.executeUpdate();
            }

            // Insert teacher into teacher table
            try (PreparedStatement stmt = conn.prepareStatement(insertTeacherQuery)) {
                stmt.setString(1, "teacher123");
                stmt.setString(2, "Computer Science");
                stmt.setString(3, "teacher123");
                stmt.executeUpdate();
            }

            System.out.println("Admin, Student, and Teacher have been created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        vbox.setPadding(new javafx.geometry.Insets(30));  // Add padding around the VBox
        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);

        // Add background image
        BackgroundImage backgroundImage = new BackgroundImage(
            new javafx.scene.image.Image("file:resources/background.jpg", true),
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        vbox.setBackground(background);

        // Handle login button click
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Authenticate the user
            User user = userService.authenticateUser(username, password);

            if (user != null) {
                // If authentication is successful, check the user's role and show the respective window
                Role role = user.getRole();
                switch (role) {
                    case Role.STUDENT:
                        showStudentWindow();
                        break;
                    case Role.TEACHER:
                        showTeacherWindow();
                        break;
                    case Role.ADMIN:
                        showAdminWindow();
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

    // Function to show the student window
    private void showStudentWindow() {
        // Implement the student window UI here
        System.out.println("Showing student window...");
    }

    // Function to show the teacher window
    private void showTeacherWindow() {
        // Implement the teacher window UI here
        System.out.println("Showing teacher window...");
    }

    // Function to show the admin window
    private void showAdminWindow() {
        // Implement the admin window UI here
        System.out.println("Showing admin window...");
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
