package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import models.*;

import java.util.List;

import database.*;

public class AdminWindowUI {

    private CourseServiceDB courseService = new CourseServiceDB();
    private StudentServiceDB studentService = new StudentServiceDB();
    private TeacherServiceDB teacherService = new TeacherServiceDB();

    @SuppressWarnings("unused")
    private User user;
    
    public AdminWindowUI(User user) {
        this.user = user;
    }

    public void showAdminWindow(User user) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Student Grading System - Admin Dashboard");
    
        BorderPane mainLayout = new BorderPane();
    
        // Top: Application Title
        Label titleLabel = new Label("Student Grading System - Admin Dashboard");
        titleLabel.getStyleClass().add("title-label");
        VBox topSection = new VBox(titleLabel);
        topSection.setStyle("-fx-alignment: center; -fx-padding: 20px 0; -fx-background-color: transparent;");
        mainLayout.setTop(topSection);
    
        // Center: Tabs for Students, Teachers, Courses, and Manage Students
        TabPane tabPane = new TabPane();
        tabPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        tabPane.getTabs().addAll(createStudentTab(), createTeacherTab(), createCourseTab(), manageStudentTab());
        mainLayout.setCenter(tabPane);
    
        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-border-color: #888; -fx-border-width: 1px 0 0 0; -fx-padding: 10px;");
        Label footerLabel = new Label("Student Grading System - Admin Dashboard");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");
        footer.getChildren().add(footerLabel);
    
        // Scene setup
        Scene scene = new Scene(mainLayout, 850, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create Student Tab (Database Support)
    private Tab createStudentTab() {
        Tab studentTab = new Tab("Add Students");
        studentTab.setClosable(false);

        VBox studentLayout = new VBox(10);
        studentLayout.setStyle("-fx-padding: 10; -fx-background-color: #2c2f38;");

        // Create labels and apply the CSS class
        Label addLabel = new Label("Enter Student's Details to Add:");
        addLabel.getStyleClass().add("label");

        Label listLabel = new Label("List of Students:");
        listLabel.getStyleClass().add("label");


        // Add Student Section
        TextField firstNameTextField = new TextField();
        firstNameTextField.setPromptText("First Name");
        firstNameTextField.getStyleClass().add("input-field");

        TextField lastNameTextField = new TextField();
        lastNameTextField.setPromptText("Last Name");
        lastNameTextField.getStyleClass().add("input-field"); 

        TextField idField = new TextField();
        idField.setPromptText("ID");
        idField.getStyleClass().add("input-field"); 

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input-field");

        Button addStudentButton = new Button("Add Student");
        addStudentButton.getStyleClass().add("button");
        addStudentButton.setOnAction(_ -> {
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String id = idField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (!firstName.isEmpty() && !lastName.isEmpty() && !id.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                // Avoid duplicate entries
                if(studentService.findStudentById(id) != null) {
                    showAlert("Error", "Student with ID " + id + " already exists.");
                    firstNameTextField.clear();
                    lastNameTextField.clear();
                    idField.clear();
                    usernameField.clear();
                    passwordField.clear();
                    return;
                }

                studentService.addStudent(new Student(firstName, lastName, id, username, password)); // add student to database
                firstNameTextField.clear();
                lastNameTextField.clear();
                idField.clear();
                usernameField.clear();
                showAlert("Success", "Student added successfully!");
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        });

        studentLayout.getChildren().addAll(
                addLabel,
                firstNameTextField,
                lastNameTextField,
                idField,
                usernameField,
                passwordField,
                addStudentButton
        );

        // List Students Section
        Button listStudentsButton = new Button("View");
        listStudentsButton.getStyleClass().add("button"); 
        TextArea studentListArea = new TextArea();
        studentListArea.setEditable(false);
        studentListArea.getStyleClass().add("textarea"); 

        // List Students Section
        listStudentsButton.setOnAction(_ -> {
            StringBuilder sb = new StringBuilder();
            // Retrieve students from database
            if(studentService.getAllStudents().isEmpty()) {
                sb.append("No students in the system.");
            } else{
                int index = 1;
                for (Student student : studentService.getAllStudents()) {
                    // 1. Panagiotis Tsembekis (UC1070326) - @tsembp
                    sb.append(index + ". " + student.getFirstName() + " " + student.getLastName() + " (" + student.getId() + ")" + " - " + student.getUsername() + "\n");
                    sb.append("📚 Enrolled Courses:\n");
                    List<Course> studentCourses = studentService.getCoursesForStudent(student.getId()); // get courses that student is enrolled
                    // • [EPL231] DSA in Java (7.5 EC)
                    if(studentCourses.isEmpty()){
                        sb.append("\tNo courses assigned.\n");
                    } else{
                        for(Course course : studentCourses){
                            sb.append("\t• [" + course.getCourseId() + "] " + course.getCourseName() + " (" + course.getCreditHours() + " EC)\n");
                            Grade courseGrades = studentService.getGradesForStudentInCourse(student.getId(), course.getCourseId()); // get grades for student in course
                            if(courseGrades == null){
                                sb.append("\t\tNo grades assigned.\n");
                            } else{
                                sb.append("\t\tProject Grade: " + courseGrades.getProjectGrade() + " | MidTerm Grade: " + courseGrades.getMidtermGrade() + " | EndTerm Grade: " + courseGrades.getEndTermGrade() + "\n");
                            }
                        }
                    }

                    index++;
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

    // Student Edit/Delete Operations Tab (Database Support)
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
        addStudentButton.setOnAction(_ -> {
            String id = idField.getText();
            if (!id.isEmpty()) {
                // Student student = studentService.findStudentById(id); // find student in list (no database)
                Student student = studentService.findStudentById(id); // find student in database
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
    
    // Helper for displaying student options
    private void displayStudentOptions(VBox studentLayout, Student student, TextField idField, Button addStudentButton) {
        // Clear previous content
        studentLayout.getChildren().clear();
    
        // Add return button to go back to search section
        Text returnText = new Text("Back");
        returnText.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-underline: true;");
        HBox returnButtonContainer = new HBox(returnText);
        returnButtonContainer.setCursor(Cursor.HAND);
        returnButtonContainer.setAlignment(Pos.CENTER_LEFT);
        returnButtonContainer.setOnMouseClicked(_ -> {
            // Clear the current student options and show the search section again
            studentLayout.getChildren().clear();
            studentLayout.getChildren().addAll(idField, addStudentButton);
        });

        studentLayout.getChildren().add(returnButtonContainer);

        // Show student info
        Text studentInfo = new Text("Student: " + student.getFirstName() + " " + student.getLastName() + " (ID: " + student.getId() + ")");
        studentInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        studentLayout.getChildren().add(studentInfo);
    
        // Section for updating student info (Name, ID, Class)
        TextField firstNameField = new TextField(student.getFirstName());
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField(student.getLastName());
        lastNameField.setPromptText("Last Name");
    
        TextField idField2 = new TextField(student.getId());
        idField2.setPromptText("ID");
    
        TextField usernameField = new TextField(student.getUsername());
        usernameField.setPromptText("Username");

        TextField passwordField = new TextField(student.getPassword());
        passwordField.setPromptText("Password");
    
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        saveButton.setOnAction(_ -> {
            student.setFirstName(firstNameField.getText());
            student.setLastName(lastNameField.getText());
            student.setId(idField2.getText());
            student.setUsername(usernameField.getText());
            student.setPassword(passwordField.getText());

            studentService.updateStudent(student); // update student's details in database
            System.out.println("Updated student info.");
            showAlert("Success", "Student information updated.");
        });
    
        VBox updateSection = new VBox(5, firstNameField, lastNameField, idField2, usernameField, saveButton);
        studentLayout.getChildren().add(updateSection);
    
        VBox coursesSection = new VBox(5);
        Text coursesTitle = new Text("Courses:");
        coursesSection.getChildren().add(coursesTitle);

        // Retrieve courses for student from database
        List<Course> studentCourses = studentService.getCoursesForStudent(student.getId());
        if(studentCourses.isEmpty()){
            Text noCourses = new Text("No courses assigned.");
            coursesSection.getChildren().add(noCourses);
        } else{
            for(Course course : studentCourses){
                HBox courseRow = new HBox(10);
                Text courseId = new Text(course.getCourseId());
                Text courseName = new Text(course.getCourseName());

                // X button for course removal
                Image removeImage = new Image("file:C:/Users/panag/Documents/GitHub/Student-Grading-System/assets/removeIcon.png");
                ImageView removeCourseIcon = new ImageView(removeImage);
                removeCourseIcon.setFitWidth(20); 
                removeCourseIcon.setFitHeight(20);
                removeCourseIcon.setOnMouseClicked(_ -> {
                    studentService.unassignCourseFromStudent(student.getId(), course.getCourseId()); // remove student-course relation from database
                    displayStudentOptions(studentLayout, student, idField, addStudentButton);
                });

                // Edit button for course editing
                Image editImage = new Image("file:C:/Users/panag/Documents/GitHub/Student-Grading-System/assets/editIcon.png");
                ImageView editCourseIcon = new ImageView(editImage);
                editCourseIcon.setFitWidth(20);
                editCourseIcon.setFitHeight(20);
                editCourseIcon.setOnMouseClicked(_ -> {
                    displayCourseEditBox(course, student, studentLayout, idField, addStudentButton);
                    studentService.updateStudent(student); // update student record in student list (no database)
                    displayStudentOptions(studentLayout, student, idField, addStudentButton);
                });

                courseRow.getChildren().addAll(removeCourseIcon, editCourseIcon, courseId, courseName);
                coursesSection.getChildren().add(courseRow);
            }
        }

        studentLayout.getChildren().add(coursesSection);
    
        // Create a VBox to hold the delete button
        VBox buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
    
        // Delete student button (at the very bottom)
        Button deleteButton = new Button("Delete Student");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteButton.setOnAction(_ -> {
            studentService.deleteStudent(student.getId()); // delete student record from database
            studentLayout.getChildren().clear();
            studentLayout.getChildren().addAll(idField, addStudentButton);
            showAlert("Success", "Student deleted successfully.");
        });
    
        // Add the button to the container
        buttonContainer.getChildren().add(deleteButton);
    
        // Add the container to the layout
        studentLayout.getChildren().add(buttonContainer);
    }

    // Create Teacher Tab (Database Support)
    private Tab createTeacherTab() {
        Tab teacherTab = new Tab("Add Teachers");
        teacherTab.setClosable(false);

        VBox teacherLayout = new VBox(10);
        teacherLayout.setStyle("-fx-padding: 10;");

        // Add Labels
        Label addLabel = new Label("Enter Teacher's Details to Add:");
        addLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label listLabel = new Label("List of Teachers:");
        listLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Add Teacher Section
        TextField firstNameTextField = new TextField();
        firstNameTextField.setPromptText("First Name");
        firstNameTextField.getStyleClass().add("input-field");
        TextField lastNameTextField = new TextField();
        lastNameTextField.setPromptText("Last Name");
        lastNameTextField.getStyleClass().add("input-field");
        TextField idField = new TextField();
        idField.setPromptText("ID");
        idField.getStyleClass().add("input-field");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input-field");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input-field");
        TextField departmentField = new TextField();
        departmentField.setPromptText("Department");
        departmentField.getStyleClass().add("input-field");

        Button addTeacherButton = new Button("Add Teacher");
        addTeacherButton.getStyleClass().add("button");
        addTeacherButton.setOnAction(_ -> {
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String id = idField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String department = departmentField.getText();
            
            if (!firstName.isEmpty() && !lastName.isEmpty() && !id.isEmpty() && !username.isEmpty() && !password.isEmpty() && !department.isEmpty()) {
                // Avoid duplicate entries
                if(teacherService.findTeacherById(id) != null) {
                    showAlert("Error", "Teacher with ID " + id + " already exists.");
                    firstNameTextField.clear();
                    lastNameTextField.clear();
                    idField.clear();
                    usernameField.clear();
                    passwordField.clear();
                    departmentField.clear();
                    return;
                }

                teacherService.addTeacher(new Teacher(firstName, lastName, id, username, password, department)); // add teacher to database
                firstNameTextField.clear();
                lastNameTextField.clear();
                idField.clear();
                usernameField.clear();
                passwordField.clear();
                departmentField.clear();
                showAlert("Success", "Teacher added successfully!");
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        });

        teacherLayout.getChildren().addAll(
                addLabel,
                firstNameTextField,
                lastNameTextField,
                idField,
                usernameField,
                passwordField,
                departmentField,
                addTeacherButton
        );

        // List Teachers Section
        Button listTeachersButton = new Button("View");
        listTeachersButton.getStyleClass().add("button");
        TextArea teacherListArea = new TextArea();
        teacherListArea.setPrefHeight(200);
        teacherListArea.setEditable(false);
        teacherListArea.getStyleClass().add("textarea");

        // List Teachers Section
        listTeachersButton.setOnAction(_ -> {
            StringBuilder sb = new StringBuilder();
            // Retrieve teachers from database
            if(teacherService.getAllTeachers().isEmpty()) {
                sb.append("No teachers in the system.");
            } else {
                int index = 1;
                for (Teacher teacher : teacherService.getAllTeachers()) {
                    // 1. Panagiotis Tsembekis (UC1070326) - @tsembp
                    sb.append(index + ". " + teacher.getFirstName() + " " + teacher.getLastName() + " (" + teacher.getId() + ")" + " - " + teacher.getUsername() + "\n");
                    sb.append("📚 Assigned Courses:\n");
                    List<Course> teacherCourses = teacherService.getCoursesForTeacher(teacher.getId()); // get courses assigned to teacher
                    // • [EPL231] DSA in Java (7.5 EC)
                    if(teacherCourses.isEmpty()) {
                        sb.append("\tNo courses assigned.\n");
                    } else {
                        for(Course course : teacherCourses) {
                            sb.append("\t• [" + course.getCourseId() + "] " + course.getCourseName() + " (" + course.getCreditHours() + " EC)\n");
                        }
                    }
                    index++;
                }
            }

            teacherListArea.setText(sb.toString());
        });

        teacherLayout.getChildren().addAll(
                listLabel,
                listTeachersButton,
                teacherListArea
        );

        teacherTab.setContent(teacherLayout);
        return teacherTab;
    }

    // Create Course Tab (Database Support)
    private Tab createCourseTab() {
        Tab courseTab = new Tab("Manage Courses");
        courseTab.setClosable(false);

        VBox courseLayout = new VBox(10);
        courseLayout.setStyle("-fx-padding: 10;");

        // Add Labels
        Label addCourseLabel = new Label("Enter New Course's Details:");
        addCourseLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label addCourseStudentLabel = new Label("Enroll Student to Course:");
        addCourseStudentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Add Course Section
        TextField courseNameField = new TextField();
        courseNameField.setPromptText("Course Name");
        courseNameField.getStyleClass().add("input-field"); 
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Course ID");
        courseIdField.getStyleClass().add("input-field");
        TextField courseCreditHoursField = new TextField();
        courseCreditHoursField.setPromptText("Credit Hours");
        courseCreditHoursField.getStyleClass().add("input-field");
        TextField teacherIdField = new TextField();
        teacherIdField.setPromptText("Teacher Id");
        teacherIdField.getStyleClass().add("input-field");

        Button addCourseButton = new Button("Add Course");
        addCourseButton.getStyleClass().add("button");
        addCourseButton.setOnAction(_ -> {
            String name = courseNameField.getText();
            String id = courseIdField.getText();
            String creditHours = courseCreditHoursField.getText();
            String teacherId = teacherIdField.getText();
            if (!name.isEmpty() && !id.isEmpty() && !creditHours.isEmpty() || teacherId.isEmpty()) {
                try {
                    double creditHoursInt = Double.parseDouble(creditHours);

                    Teacher teacher = teacherService.findTeacherById(teacherId);
                    if(teacher == null){
                        showAlert("Error", "No such teacher found.");
                        return;
                    }

                    courseService.createCourse(new Course(name, id, creditHoursInt, teacher)); // create course record in database
                    showAlert("Success", "Course added successfully!");
                    courseNameField.clear();
                    courseIdField.clear();
                    courseCreditHoursField.clear();
                    teacherIdField.clear();
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Credit Hours must be a valid number.");
                }
            } else {
                showAlert("Error", "Please fill in all fields.");
            }
        });

        courseLayout.getChildren().addAll(
                addCourseLabel,
                courseNameField,
                courseIdField,
                courseCreditHoursField,
                teacherIdField,
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
        assignCourseButton.setOnAction(_ -> {
            String studentId = studentIdField.getText();
            String courseId = courseIdForStudentField.getText();
            Student student = studentService.findStudentById(studentId);
            Course course = courseService.getCourseById(courseId);
            if (student != null && course != null) {
                student.addCourse(course);
                studentService.assignCourseToStudent(student, course);; // add student-course relation to database
                showAlert("Success", "Course assigned to student successfully!");
            } else {
                showAlert("Error", "Student or Course not found.");
            }

            studentIdField.clear();
            courseIdForStudentField.clear();
        });

        courseLayout.getChildren().addAll(
                addCourseStudentLabel,
                studentIdField,
                courseIdForStudentField,
                assignCourseButton
        );

        courseTab.setContent(courseLayout);
        return courseTab;
    }

    // Function to open a new window for course editing
    public void displayCourseEditBox(Course course, Student student, VBox studentLayout, TextField idField, Button addStudentButton) {
        // Create a new window (Stage) for editing the course
        Stage editCourseStage = new Stage();
        editCourseStage.setTitle("Edit Course");
        editCourseStage.setWidth(300);
        editCourseStage.setHeight(320);

        // Create a form for editing course details
        VBox editForm = new VBox(10);
        editForm.setPadding(new Insets(20));

        // Fields to edit the grades
        Grade studentGrades = studentService.getGradesForStudentInCourse(student.getId(), course.getCourseId()); // get grades for student in course from database
        
        Label midtermLabel = new Label("Midterm Grade:");
        TextField midtermGradeField = new TextField(String.valueOf(studentGrades.getMidtermGrade()));
        midtermGradeField.setPromptText("Midterm Grade");

        Label endtermLabel = new Label("Endterm Grade:");
        TextField endtermGradeField = new TextField(String.valueOf(studentGrades.getEndTermGrade()));
        endtermGradeField.setPromptText("End-term Grade");


        Label projectLabel = new Label("Project Grade:");
        TextField projectGradeField = new TextField(String.valueOf(studentGrades.getProjectGrade()));
        projectGradeField.setPromptText("Project Grade");

        // Buttons for save and un-assign
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");

        Button unassignButton = new Button("Un-assign Course");
        unassignButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        buttonBox.getChildren().addAll(saveButton, unassignButton);

        // Save button action: Update grades and student record
        saveButton.setOnAction(_ -> {
            try {
                // Update the course details with the new grades (no database)
                studentGrades.setMidtermGrade(Double.parseDouble(midtermGradeField.getText()));
                studentGrades.setEndTermGrade(Double.parseDouble(endtermGradeField.getText()));
                studentGrades.setProjectGrade(Double.parseDouble(projectGradeField.getText()));

                // Update student's grades for specific course in database
                studentService.assignGradeToStudent(student.getId(), course.getCourseId(), studentGrades);

                displayStudentOptions(studentLayout, student, idField, addStudentButton);
            } catch (NumberFormatException ex) {
                // Handle invalid input (e.g., non-numeric grades)
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric grades.");
                alert.show();
            }

            // Close the window after saving
            editCourseStage.close();
        });

        // Un-assign button action: Remove course from student's courses
        unassignButton.setOnAction(_ -> {
            studentService.unassignCourseFromStudent(student.getId(), course.getCourseId());

            displayStudentOptions(studentLayout, student, idField, addStudentButton);

            // Close the window after un-assigning the course
            editCourseStage.close();
        });

        // Add all form elements to the VBox
        editForm.getChildren().addAll(
            midtermLabel,
            midtermGradeField,
            endtermLabel,
            endtermGradeField,
            projectLabel,
            projectGradeField,
            saveButton, 
            unassignButton
        );

        // Create a scene for the stage and set it
        Scene editCourseScene = new Scene(editForm, 300, 250);
        editCourseStage.setScene(editCourseScene);

        // Show the new window
        editCourseStage.show();
    }
    
    // Alert box
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
