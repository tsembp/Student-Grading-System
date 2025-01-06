USE sgs;

-- Create the Roles table
CREATE TABLE `roles` (
   `id` int NOT NULL AUTO_INCREMENT,
   `role_name` varchar(50) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the Users table
CREATE TABLE `users` (
   `indexNum` int NOT NULL AUTO_INCREMENT,
   `first_name` varchar(100) NOT NULL,
   `last_name` varchar(100) NOT NULL,
   `user_id` varchar(100) NOT NULL,
   `username` varchar(100) NOT NULL,
   `password` varchar(255) NOT NULL,
   `role_id` int NOT NULL,
   PRIMARY KEY (`indexNum`),
   UNIQUE KEY `user_id` (`user_id`),
   UNIQUE KEY `username` (`username`),
   KEY `role_id` (`role_id`),
   CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the Course table
CREATE TABLE `course` (
   `courseName` varchar(255) DEFAULT NULL,
   `courseId` varchar(255) NOT NULL,
   `creditHours` double DEFAULT NULL,
   PRIMARY KEY (`courseId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the Student table
CREATE TABLE `student` (
   `id` varchar(50) NOT NULL,
   `className` varchar(255) DEFAULT NULL,
   `user_id` varchar(50) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `user_id` (`user_id`), -- Ensure unique mapping to a user
   CONSTRAINT `student_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the StudentCourse table (many-to-many relationship)
CREATE TABLE `studentcourse` (
   `studentId` varchar(255) NOT NULL,
   `courseId` varchar(255) NOT NULL,
   PRIMARY KEY (`studentId`, `courseId`),
   KEY `courseId` (`courseId`),
   CONSTRAINT `studentcourse_ibfk_1` FOREIGN KEY (`studentId`) REFERENCES `student` (`id`),
   CONSTRAINT `studentcourse_ibfk_2` FOREIGN KEY (`courseId`) REFERENCES `course` (`courseId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the Grade table
CREATE TABLE `grade` (
   `studentId` varchar(255) NOT NULL,
   `courseId` varchar(255) NOT NULL,
   `midtermGrade` double DEFAULT NULL,
   `endTermGrade` double DEFAULT NULL,
   `projectGrade` double DEFAULT NULL,
   PRIMARY KEY (`studentId`, `courseId`),
   KEY `fk_course` (`courseId`),
   CONSTRAINT `fk_course` FOREIGN KEY (`courseId`) REFERENCES `course` (`courseId`),
   CONSTRAINT `fk_student` FOREIGN KEY (`studentId`) REFERENCES `student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

use sgs;
CREATE TABLE `teacher` (
    `id` VARCHAR(50) PRIMARY KEY,
    `department` VARCHAR(255) NOT NULL,
    `user_id` VARCHAR(50) NOT NULL,
    CONSTRAINT `teacher_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

use sgs;
CREATE TABLE `teachercourse` (
    `teacher_id` VARCHAR(50) NOT NULL,
    `course_id` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`teacher_id`, `course_id`),
    CONSTRAINT `teacher_fk` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`),
    CONSTRAINT `course_fk` FOREIGN KEY (`course_id`) REFERENCES `course` (`courseId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

use sgs;
ALTER TABLE `course` 
ADD COLUMN `teacher_id` varchar(50) NOT NULL,
ADD CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`);

show create table course;


