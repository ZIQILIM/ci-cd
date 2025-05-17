package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import database.DatabaseConnection;

// Entity class for tracking service view information
public class TrackViewInformation {
    
    // Get view stats for a service
    public Map<Integer, Integer> getViewStats(int serviceId) {
        Map<Integer, Integer> viewStats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT service_id, view_count FROM service_views WHERE service_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, serviceId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    viewStats.put(rs.getInt("service_id"), rs.getInt("view_count"));
                } else {
                    // If no record exists yet, return 0 views
                    viewStats.put(serviceId, 0);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving view stats: " + e.getMessage());
            e.printStackTrace();
            
            // If the table doesn't exist, create it
            if (e.getMessage().contains("doesn't exist")) {
                createViewsTable();
            }
        }
        
        return viewStats;
    }
    
    // Increment view count for a service
    public boolean incrementViewCount(int serviceId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if record exists
            String checkSql = "SELECT COUNT(*) FROM service_views WHERE service_id = ?";
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, serviceId);
                ResultSet rs = checkStmt.executeQuery();
                
                boolean exists = false;
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                
                String sql;
                if (exists) {
                    // Update existing record
                    sql = "UPDATE service_views SET view_count = view_count + 1 WHERE service_id = ?";
                } else {
                    // Insert new record
                    sql = "INSERT INTO service_views (service_id, view_count) VALUES (?, 1)";
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, serviceId);
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating view count: " + e.getMessage());
            e.printStackTrace();
            
            // If the table doesn't exist, create it
            if (e.getMessage().contains("doesn't exist")) {
                createViewsTable();
            }
        }
        
        return false;
    }
    
    // Get view stats for all services of a cleaner
    public Map<Integer, Integer> getViewStatsByUsername(String cleanerUsername) {
        Map<Integer, Integer> viewStats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT cs.service_id, COALESCE(sv.view_count, 0) as view_count " +
                         "FROM cleaning_services cs " +
                         "LEFT JOIN service_views sv ON cs.service_id = sv.service_id " +
                         "WHERE cs.cleaner_username = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cleanerUsername);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    viewStats.put(rs.getInt("service_id"), rs.getInt("view_count"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving view stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return viewStats;
    }
    
    // Create the views table if it doesn't exist
    private void createViewsTable() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS service_views (" +
                         "service_id INT PRIMARY KEY, " +
                         "view_count INT NOT NULL DEFAULT 0, " +
                         "FOREIGN KEY (service_id) REFERENCES cleaning_services(service_id) ON DELETE CASCADE)";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Service views table created successfully");
            }
        } catch (SQLException e) {
            System.out.println("Error creating service views table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}