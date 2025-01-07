package ui;

import java.util.List;
import java.util.Optional;

import database.*;
import models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StudentWindowUI {

    private StudentServiceDB studentService = new StudentServiceDB();

    @SuppressWarnings("unused")
    private User user;

    public StudentWindowUI(User user) {
        this.user = user;
    }
    
    @SuppressWarnings("unchecked")
    public void showStudentWindow(User user) {
        // Create a new Stage for the student window
        Stage studentStage = new Stage();
        studentStage.setTitle("Student Grading System - Student Dashboard");

        // Label for the student's name
        Label welcomeLabel = new Label("Welcome, @" + user.getUsername());
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #d1d1d1; -fx-alignment: center;");

        // TableView to display courses and grades
        TableView<Course> courseTableView = new TableView<>();
        courseTableView.setPadding(new Insets(10));
        courseTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Table columns
        TableColumn<Course, String> courseIdColumn = new TableColumn<>("Course ID");
        courseIdColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(course.getCourseId());
        });
        courseIdColumn.setMinWidth(100);


        TableColumn<Course, String> courseNameColumn = new TableColumn<>("Course Title");
        courseNameColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(course.getCourseName());
        });
        courseNameColumn.setMinWidth(200);

        TableColumn<Course, String> courseECColumn = new TableColumn<>("EC");
        courseECColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(course.getCreditHours()));
        });
        courseECColumn.setMinWidth(50);

        // Grouping Course Details
        TableColumn<Course, String> courseDetailsColumn = new TableColumn<>("Course Details");
        courseDetailsColumn.getColumns().addAll(courseNameColumn, courseIdColumn, courseECColumn);

        TableColumn<Course, String> midTermColumn = new TableColumn<>("Midterm");
        midTermColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            
            return new SimpleStringProperty(String.valueOf(studentService.getGradesForStudentInCourse(user.getId(), course.getCourseId()).getMidtermGrade()));
        });
        midTermColumn.setMinWidth(60);

        TableColumn<Course, String> endTermColumn = new TableColumn<>("Endterm");
        endTermColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(studentService.getGradesForStudentInCourse(user.getId(), course.getCourseId()).getEndTermGrade()));
        });
        endTermColumn.setMinWidth(60);


        TableColumn<Course, String> projectColumn = new TableColumn<>("Project");
        projectColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(studentService.getGradesForStudentInCourse(user.getId(), course.getCourseId()).getProjectGrade()));
        });
        projectColumn.setMinWidth(60);

        // Grouping Grades
        TableColumn<Course, String> gradesColumn = new TableColumn<>("Grades");
        gradesColumn.getColumns().addAll(midTermColumn, endTermColumn, projectColumn);

        // Add "Drop" column with a button
        TableColumn<Course, Void> dropColumn = new TableColumn<>("Drop");
        dropColumn.setCellFactory(_ -> new TableCell<Course, Void>() {
            private final Button dropButton = new Button("X"); // Use a cross emoji or an icon here

            {
                dropButton.setStyle("-fx-font-size: 20px; -fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold;");
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

        courseTableView.getColumns().addAll(courseDetailsColumn, gradesColumn, dropColumn);

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

        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-border-color: #888; -fx-border-width: 1px 0 0 0; -fx-padding: 10px;");
        Label footerLabel = new Label("Student Grading System - Student Dashboard");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");
        footer.getChildren().add(footerLabel);

        // Create a VBox to hold the UI elements
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(welcomeLabel, courseTableView, refreshButton, footer);

        // Create scene and show student window
        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        studentStage.setScene(scene);
        studentStage.show();
    }

}
