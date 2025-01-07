package ui;

import database.*;
import models.*;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SystemUI extends Application {

    private UserServiceDB userService = new UserServiceDB();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // userService.addAdminUser(new User("Admin", "User", "admin123", "admin", "adminPass123", Role.ADMIN));
        // DatabaseSeeder.seedDatabase();
        // DatabaseSeeder.clearDatabase();
        // User u = userService.authenticateUser("admin", "adminPass123");

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

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 14px; -fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 10px 20px;");
        exitButton.setOnAction(_ -> {
            System.exit(0);
        });

        // Add refresh and logout buttons to a horizontal box
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(exitButton, loginButton);

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new javafx.geometry.Insets(30));
        vbox.setStyle("-fx-background-color: #2c2f38;");
        
        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, buttonBox);
        
        // Login logic
        loginButton.setOnAction(_ -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Create a loading dialog
            Stage loadingStage = new Stage();
            ProgressIndicator progressIndicator = new ProgressIndicator();
            VBox loadingBox = new VBox(progressIndicator);
            loadingBox.setAlignment(Pos.CENTER);
            loadingBox.setPadding(new javafx.geometry.Insets(20));
            Scene loadingScene = new Scene(loadingBox, 200, 100);
            loadingStage.setScene(loadingScene);
            loadingStage.setTitle("Loading...");
            loadingStage.setResizable(false);

            // Task to authenticate the user and load the corresponding window
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // Authenticate the user
                    User user = userService.authenticateUser(username, password);

                    // Ensure UI updates happen on the JavaFX Application Thread
                    javafx.application.Platform.runLater(() -> {
                        loadingStage.close(); // Close the loading dialog

                        if (user != null) {
                            // Check the user's role and show the appropriate window
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

                    return null;
                }
            };

            // Show loading dialog and run the task
            loadingStage.show();
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        // Create scene and show login window
        Scene scene = new Scene(vbox, 400, 300);
        scene.setFill(Color.web("#2c2f38"));
        primaryStage.setTitle("Student Grading System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
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
