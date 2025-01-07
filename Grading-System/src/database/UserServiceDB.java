package database;

import models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceDB {

    // Method to authenticate the user
    public User authenticateUser(String username, String password) {
        // SQL query to find the user by username
        String query = "SELECT user_id, first_name, last_name, username, password, role_id FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            // Check if the user exists and if the password matches
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                // In a real system, you would compare hashed passwords, not plain text
                if (password.equals(storedPassword)) {
                    // Convert the role id to Role enum
                    int roleId = rs.getInt("role_id");
                    Role role = Role.getById(roleId);  // Convert role id to Role enum
                    
                    // Create and return User object with the details from the database
                    return new User(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("user_id"),
                        rs.getString("username"),
                        storedPassword,
                        role
                    );
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Return null if authentication fails
        return null;
    }

    // Method to create admin user
    public void addAdminUser(User admin) {
        String query = "INSERT INTO users (first_name, last_name, user_id, username, password, role_id) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, admin.getFirstName());
            stmt.setString(2, admin.getLastName());
            stmt.setString(3, admin.getId());
            stmt.setString(4, admin.getUsername());
            stmt.setString(5, admin.getPassword());
            stmt.setInt(6, Role.ADMIN.getId());  // Assuming Role.ADMIN has the correct role ID.
    
            stmt.executeUpdate();
            System.out.println("Admin user added successfully.");
    
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding admin user.");
        }
    }
    
}
