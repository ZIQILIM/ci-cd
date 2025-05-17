package entity.Homeowner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import entity.Cleaner.CleanerAccount;
import entity.UserDAO;

public class ShortlistService {
    
    public boolean add(String homeownerUsername, String cleanerId) {
        // Check if already in shortlist to avoid duplicates
        if (isCleanerInShortlist(homeownerUsername, cleanerId)) {
            return false;
        }
        
        String sql = "INSERT INTO homeowner_shortlist (homeowner_username, cleaner_username) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, homeownerUsername);
            pstmt.setString(2, cleanerId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error adding cleaner to shortlist: " + e.getMessage());
            e.printStackTrace();
            
            if (e.getMessage().contains("doesn't exist")) {
                createShortlistTable();
                return add(homeownerUsername, cleanerId); 
            }
            
            return false;
        }
    }
    
    public boolean remove(String homeownerUsername, String cleanerId) {
        String sql = "DELETE FROM homeowner_shortlist WHERE homeowner_username = ? AND cleaner_username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, homeownerUsername);
            pstmt.setString(2, cleanerId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error removing cleaner from shortlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all cleaners in a homeowner's shortlist
    public List<CleanerAccount> getShortlist(String homeownerUsername) {
        List<CleanerAccount> cleaners = new ArrayList<>();
        String sql = "SELECT cleaner_username FROM homeowner_shortlist WHERE homeowner_username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, homeownerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            UserDAO userDAO = new UserDAO();
            while (rs.next()) {
                String cleanerUsername = rs.getString("cleaner_username");
                CleanerAccount cleaner = (CleanerAccount) userDAO.findByUsername(cleanerUsername);
                if (cleaner != null) {
                    cleaners.add(cleaner);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting shortlist: " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("doesn't exist")) {
                createShortlistTable();
            }
        }
        
        return cleaners;
    }
    
    // Check if a cleaner is already in the homeowner's shortlist
    public boolean isCleanerInShortlist(String homeownerUsername, String cleanerId) {
        String sql = "SELECT COUNT(*) FROM homeowner_shortlist WHERE homeowner_username = ? AND cleaner_username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, homeownerUsername);
            pstmt.setString(2, cleanerId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking shortlist: " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("doesn't exist")) {
                createShortlistTable();
                return false;
            }
        }
        
        return false;
    }
    
    // count of how many times a cleaner has been shortlisted
    public int getCountByUsername(String cleanerUsername) {
        String sql = "SELECT COUNT(*) FROM homeowner_shortlist WHERE cleaner_username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cleanerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting shortlist count: " + e.getMessage());
            e.printStackTrace();
            
            // If the table doesn't exist, try to create it
            if (e.getMessage().contains("doesn't exist")) {
                createShortlistTable();
                return 0;
            }
        }
        
        return 0; 
    }
    
    // Create the shortlist table if it doesn't exist
    private void createShortlistTable() {
        String sql = "CREATE TABLE IF NOT EXISTS homeowner_shortlist (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "homeowner_username VARCHAR(50) NOT NULL, " +
                     "cleaner_username VARCHAR(50) NOT NULL, " +
                     "date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (homeowner_username) REFERENCES users(username) ON DELETE CASCADE, " +
                     "FOREIGN KEY (cleaner_username) REFERENCES users(username) ON DELETE CASCADE, " +
                     "UNIQUE (homeowner_username, cleaner_username))";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("Shortlist table created successfully");
            
        } catch (SQLException e) {
            System.out.println("Error creating shortlist table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getHomeownersWhoShortlisted(String cleanerUsername) {
        List<String> homeowners = new ArrayList<>();
        String sql = "SELECT homeowner_username FROM homeowner_shortlist WHERE cleaner_username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cleanerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String homeownerUsername = rs.getString("homeowner_username");
                homeowners.add(homeownerUsername);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting homeowners who shortlisted: " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("doesn't exist")) {
                createShortlistTable();
            }
        }
        
        return homeowners;
    }
}