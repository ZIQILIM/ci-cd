package entity.Homeowner;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import database.DatabaseConnection;
import entity.Cleaner.CleanerAccount;
import entity.UserDAO;

public class Shortlist {
    
    // Get all cleaners in a homeowner's shortlist
    public List<CleanerAccount> getCleanerList(String homeownerUsername) {
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
            
            // If the table doesn't exist, try to create it
            if (e.getMessage().contains("doesn't exist")) {
                createShortlistTable();
            }
        }
        
        return cleaners;
    }
    
    // Get matching cleaners based on search criteria
    public List<CleanerAccount> getMatchingCleaners(String criteria, String homeownerUsername) {
        List<CleanerAccount> allCleaners = getCleanerList(homeownerUsername);
        List<CleanerAccount> matchingCleaners = new ArrayList<>();
        
        if (criteria == null || criteria.trim().isEmpty()) {
            return allCleaners; 
        }
        
        String lowerCriteria = criteria.toLowerCase().trim();
        
        // Filter cleaners based on criteria (name, address, email, phone)
        for (CleanerAccount cleaner : allCleaners) {
            if ((cleaner.getName() != null && cleaner.getName().toLowerCase().contains(lowerCriteria)) ||
                (cleaner.getAddress() != null && cleaner.getAddress().toLowerCase().contains(lowerCriteria)) ||
                (cleaner.getEmail() != null && cleaner.getEmail().toLowerCase().contains(lowerCriteria)) ||
                (cleaner.getPhoneNumber() != null && cleaner.getPhoneNumber().toLowerCase().contains(lowerCriteria))) {
                matchingCleaners.add(cleaner);
            }
        }
        
        return matchingCleaners;
    }

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
}