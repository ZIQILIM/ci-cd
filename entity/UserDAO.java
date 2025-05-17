package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import entity.Cleaner.CleanerAccount;
import entity.Homeowner.HomeownerAccount;
import entity.PlatformManager.PlatformManagerAccount;
import entity.UserAdmin.UserAccount;
import entity.UserAdmin.UserAdminAccount;

public class UserDAO {
    
    public boolean createUser(UserAccount user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 
            
            String sql = "INSERT INTO users (username, password, account_type, name, email, status, address, phone_number, profile_suspended) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getAccountType());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getStatus() != null ? user.getStatus() : "Active");
            pstmt.setString(7, user.getAddress() != null ? user.getAddress() : "");
            pstmt.setString(8, user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            pstmt.setBoolean(9, user.isProfileSuspended());
            
            int rowsAffected = pstmt.executeUpdate();
            conn.commit(); 
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back: " + ex.getMessage());
            }
            System.out.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); 
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    // Find a user by username
    public UserAccount findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Validate user login credentials
    public UserAccount validateLogin(String username, String password, String profileType) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND account_type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, profileType);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Update an existing user
    public boolean updateUser(UserAccount user) {
        String sql = "UPDATE users SET password = ?, account_type = ?, name = ?, email = ?, status = ?, address = ?, phone_number = ?, profile_suspended = ? WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getAccountType());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getStatus() != null ? user.getStatus() : "Active");
            pstmt.setString(6, user.getAddress() != null ? user.getAddress() : "");
            pstmt.setString(7, user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            pstmt.setBoolean(8, user.isProfileSuspended()); 
            pstmt.setString(9, user.getUsername());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Find a user by email
    public UserAccount findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, email);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
  
    // Delete a user
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all users
    public List<UserAccount> getAllUsers() {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    // Search for users matching a search term in username, name, or email
    public List<UserAccount> searchUsers(String searchTerm) {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR name LIKE ? OR email LIKE ?";
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    // Helper method to create appropriate UserAccount object from ResultSet
    private UserAccount createUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String accountType = rs.getString("account_type");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String status = rs.getString("status"); 
        String address = rs.getString("address"); 
        String phoneNumber = rs.getString("phone_number"); 
        
        // Create appropriate account type based on the account_type
        UserAccount user;
        switch (accountType) {
            case "User Admin":
                user = new UserAdminAccount(username, password, name, email);
                break;
            case "Cleaner":
                user = new CleanerAccount(username, password, name, email);
                break;
            case "Homeowner":
                user = new HomeownerAccount(username, password, name, email);
                break;
            case "Platform Manager":
                user = new PlatformManagerAccount(username, password, name, email);
                break;
            default:
                user = new UserAccount(username, password, accountType, name, email);
                break;
        }
        
        // Set the user ID, status, address and phone number
        user.setUserId(userId);
        user.setStatus(status != null ? status : "Active"); 
        user.setAddress(address != null ? address : "");
        user.setPhoneNumber(phoneNumber != null ? phoneNumber : "");
        
        // Check if profile_suspended column exists, default to false if it doesn't
        try {
            user.setProfileSuspended(rs.getBoolean("profile_suspended"));
        } catch (SQLException e) {
            user.setProfileSuspended(false);
        }
        return user;
    }
    
    public void initializeDemoAccounts() {
        // Check if admin account exists
        if (findByUsername("admin123") == null) {
            UserAdminAccount admin = new UserAdminAccount("admin123", "admin123", "Admin User", "admin@example.com");
            admin.setStatus("Active");
            admin.setAddress("123 Admin St, Admin City");
            admin.setPhoneNumber("123-456-7890");
            createUser(admin);
        }
        
        // Check if cleaner account exists
        if (findByUsername("cleaner123") == null) {
            CleanerAccount cleaner = new CleanerAccount("cleaner123", "cleaner123", "Cleaner User", "cleaner@example.com");
            cleaner.setStatus("Active");
            cleaner.setAddress("456 Clean St, Clean City");
            cleaner.setPhoneNumber("234-567-8901");
            createUser(cleaner);
        }
        
        // Check if homeowner account exists
        if (findByUsername("homeowner123") == null) {
            HomeownerAccount homeowner = new HomeownerAccount("homeowner123", "homeowner123", "Homeowner User", "homeowner@example.com");
            homeowner.setStatus("Active");
            homeowner.setAddress("789 Home St, Home City");
            homeowner.setPhoneNumber("345-678-9012");
            createUser(homeowner);
        }
        
        // Check if platform manager account exists
        if (findByUsername("manager123") == null) {
            PlatformManagerAccount manager = new PlatformManagerAccount("manager123", "manager123", "Manager User", "manager@example.com");
            manager.setStatus("Active");
            manager.setAddress("101 Manager St, Manager City");
            manager.setPhoneNumber("456-789-0123");
            createUser(manager);
        }
    }
}