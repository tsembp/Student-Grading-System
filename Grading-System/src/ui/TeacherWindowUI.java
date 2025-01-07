package ui;

import java.util.List;

import database.*;
import models.*;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

public class TeacherWindowUI {

    private CourseServiceDB courseService = new CourseServiceDB();
    private StudentServiceDB studentService = new StudentServiceDB();
    private TeacherServiceDB teacherService = new TeacherServiceDB();

    private User user;

    public TeacherWindowUI(User user) {
        this.user = user;
    }

    public void showTeacherWindow() {
        // Create a new Stage for the teacher's window
        Stage teacherStage = new Stage();
        teacherStage.setTitle("Student Grading System - Teacher Dashboard");
    
        // Label for the teacher's name
        Label welcomeLabel = new Label("Welcome, @" + user.getUsername());
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #d1d1d1; -fx-alignment: center;");
    
        // VBox to hold the header
        VBox headerBox = new VBox(20);
        headerBox.setPadding(new Insets(10));
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(welcomeLabel);
    
        // Accordion to hold course tables in dropdowns
        Accordion accordion = new Accordion();
    
        // Get all the courses taught by the teacher
        List<Course> courses = teacherService.getCoursesForTeacher(user.getId());
    
        for (Course course : courses) {
            // Create a TableView for each course
            TableView<Student> table = new TableView<>();
            table.setEditable(true);
            table.setPadding(new Insets(10));
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
            // Add table columns for the student's ID, name, and grades
            TableColumn<Student, String> studentIdColumn = new TableColumn<>("Student ID");
            studentIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
            studentIdColumn.setMinWidth(120);
    
            TableColumn<Student, String> studentNameColumn = new TableColumn<>("Student Name");
            studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
            studentNameColumn.setMinWidth(200);
    
            TableColumn<Student, Double> midtermColumn = new TableColumn<>("Midterm Grade");
            midtermColumn.setCellValueFactory(cellData -> {
                Grade currentGrades = studentService.getGradesForStudentInCourse(cellData.getValue().getId(), course.getCourseId());
                return currentGrades != null ? new SimpleDoubleProperty(currentGrades.getMidtermGrade()).asObject() : new SimpleDoubleProperty(0).asObject();
            });
            midtermColumn.setOnEditCommit(event -> {
                Student student = event.getRowValue();
                Double newMidtermGrade = event.getNewValue();

                if(isInvalidGrade(newMidtermGrade)){
                    // Show an error dialog
                    showErrorDialog("Invalid Grade", "Grades must be between 0 and 100.");
                    
                    // Reset the grade back to the original value
                    event.getTableView().refresh();
                    return;
                }
            
                // Get the student's current grades for the course
                Grade currentGrades = studentService.getGradesForStudentInCourse(student.getId(), course.getCourseId());
            
                if (currentGrades != null) {
                    // Update only the midterm grade
                    studentService.updateGradesForStudent(student.getId(), course.getCourseId(),
                            newMidtermGrade, currentGrades.getEndTermGrade(), currentGrades.getProjectGrade());
                } else {
                    // Handle case where grades are missing
                    studentService.updateGradesForStudent(student.getId(), course.getCourseId(), newMidtermGrade, 0.0, 0.0);
                }
            });            
            midtermColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            midtermColumn.setMinWidth(150);
    
            TableColumn<Student, Double> endTermColumn = new TableColumn<>("Endterm Grade");
            endTermColumn.setCellValueFactory(cellData -> {
                Grade currentGrades = studentService.getGradesForStudentInCourse(cellData.getValue().getId(), course.getCourseId());
                return currentGrades != null ? new SimpleDoubleProperty(currentGrades.getEndTermGrade()).asObject() : new SimpleDoubleProperty(0).asObject();
            });
            endTermColumn.setOnEditCommit(event -> {
                Student student = event.getRowValue();
                Double newEndTermGrade = event.getNewValue();

                if(isInvalidGrade(newEndTermGrade)){
                    // Show an error dialog
                    showErrorDialog("Invalid Grade", "Grades must be between 0 and 100.");
                    
                    // Reset the grade back to the original value
                    event.getTableView().refresh();
                    return;
                }
            
                // Get the student's current grades for the course
                Grade currentGrades = studentService.getGradesForStudentInCourse(student.getId(), course.getCourseId());
            
                if (currentGrades != null) {
                    // Update only the end term grade
                    studentService.updateGradesForStudent(student.getId(), course.getCourseId(),
                            currentGrades.getMidtermGrade(), newEndTermGrade, currentGrades.getProjectGrade());
                } else {
                    // Handle case where grades are missing
                    studentService.updateGradesForStudent(student.getId(), course.getCourseId(), 0.0, newEndTermGrade, 0.0);
                }
            });
            endTermColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            endTermColumn.setMinWidth(150);
    
            TableColumn<Student, Double> projectColumn = new TableColumn<>("Project Grade");
            projectColumn.setCellValueFactory(cellData -> {
                Grade currentGrades = studentService.getGradesForStudentInCourse(cellData.getValue().getId(), course.getCourseId());
                return currentGrades != null ? new SimpleDoubleProperty(currentGrades.getProjectGrade()).asObject() : new SimpleDoubleProperty(0).asObject();
            });
            projectColumn.setOnEditCommit(event -> {
                Student student = event.getRowValue();
                Double newProjectGrade = event.getNewValue();

                if(isInvalidGrade(newProjectGrade)){
                    // Show an error dialog
                    showErrorDialog("Invalid Grade", "Grades must be between 0 and 100.");
                    
                    // Reset the grade back to the original value
                    event.getTableView().refresh();
                    return;
                }
            
                // Get the student's current grades for the course
                Grade currentGrades = studentService.getGradesForStudentInCourse(student.getId(), course.getCourseId());
            
                if (currentGrades != null) {
                    // Update only the project grade
                    studentService.updateGradesForStudent(student.getId(), course.getCourseId(),
                            currentGrades.getMidtermGrade(), currentGrades.getEndTermGrade(), newProjectGrade);
                } else {
                    // Handle case where grades are missing
                    studentService.updateGradesForStudent(student.getId(), course.getCourseId(), 0.0, 0.0, newProjectGrade);
                }
            });
            projectColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            projectColumn.setMinWidth(150);
    
            // Add "Unassign" column with an X button
            TableColumn<Student, Void> unassignColumn = new TableColumn<>("Unassign");
            unassignColumn.setMinWidth(100);
            unassignColumn.setCellFactory(_ -> new TableCell<Student, Void>() {
                private final Button unassignButton = new Button("X");

                {
                    unassignButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 16px;");
                    unassignButton.setOnAction(_ -> {
                        Student selectedStudent = getTableView().getItems().get(getIndex());
                        if (selectedStudent != null) {
                            studentService.unassignCourseFromStudent(selectedStudent.getId(), course.getCourseId());
                            getTableView().getItems().remove(selectedStudent);
                            System.out.println("Student " + selectedStudent.getId() + " removed from course " + course.getCourseId());
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(unassignButton);
                    }
                }
            });


            // Add all the columns to the table
            table.getColumns().addAll(studentIdColumn, studentNameColumn, midtermColumn, endTermColumn, projectColumn, unassignColumn);
    
            // Get the list of students enrolled in the course
            List<Student> students = courseService.getStudentsForCourse(course.getCourseId());
            table.setItems(FXCollections.observableArrayList(students));
            
            // Create the TitledPane for this course
            TitledPane coursePane = new TitledPane("[" + course.getCourseId() + "] " + course.getCourseName(), new VBox(table));
            coursePane.setPadding(new Insets(10));
    
            // Add the TitledPane to the Accordion
            accordion.getPanes().add(coursePane);
        }

        // Add a logout/exit button to exit
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 10px 20px;");
        logoutButton.setOnAction(_ -> {
            System.exit(0);
        });
    
        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-border-color: #888; -fx-border-width: 1px 0 0 0; -fx-padding: 10px;");
        Label footerLabel = new Label("Student Grading System - Teacher Dashboard");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");
        footer.getChildren().add(footerLabel);
    
        // Main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(headerBox, accordion, logoutButton, footer);
    
        // Create scene and show teacher window
        Scene scene = new Scene(mainLayout, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        teacherStage.setScene(scene);
        teacherStage.show();
    }
    
    private boolean isInvalidGrade(Double grade){
        // valid: in range [0,100]
        return (grade < 0 || grade > 100);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
