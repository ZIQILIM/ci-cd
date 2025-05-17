package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import entity.Cleaner.CleaningService;

public class CleaningServiceDAO {
    
    // Create a new cleaning service in the database with categories
    public boolean createService(CleaningService service) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            int nextId = getNextServiceId();
            String sql = "INSERT INTO cleaning_services (service_id, title, description, price, cleaner_username, available, " +
                    "service_duration, available_days, available_start_time, available_end_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nextId);
            pstmt.setString(2, service.getTitle());
            pstmt.setString(3, service.getDescription());
            pstmt.setFloat(4, service.getPrice());
            pstmt.setString(5, service.getCleanerUsername());
            pstmt.setBoolean(6, service.isAvailable());
            pstmt.setInt(7, service.getServiceDuration());
            pstmt.setString(8, service.getAvailableDays());
            pstmt.setTime(9, Time.valueOf(service.getAvailableStartTime()));
            pstmt.setTime(10, Time.valueOf(service.getAvailableEndTime()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Set the ID in the service object
                service.setServiceId(nextId);
                
                System.out.println("Created service with ID: " + nextId);
                
                // Add categories
                if (service.getCategoryIds() != null && !service.getCategoryIds().isEmpty()) {
                    try {
                        System.out.println("Adding " + service.getCategoryIds().size() + " categories to service");
                        saveCategoryMappings(conn, service.getServiceId(), service.getCategoryIds());
                    } catch (SQLException e) {
                        System.out.println("Error saving category mappings: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                conn.commit(); 
                return true;
            }

            conn.rollback(); 
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Error creating service: " + e.getMessage());
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
                e.printStackTrace();
            }
        }
    }
    
    // Helper method to get the next sequential service ID
    private int getNextServiceId() {
        String sql = "SELECT MAX(service_id) FROM cleaning_services";
        int nextId = 1; 
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next() && rs.getObject(1) != null) {
                nextId = rs.getInt(1) + 1;
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting next service ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return nextId;
    }
    
    // Helper method to save category mappings with verification
    private void saveCategoryMappings(Connection conn, int serviceId, List<Integer> categoryIds) throws SQLException {
        ensureMappingTableExists(conn);
        
        // verify all categories exist before attempting to insert
        List<Integer> validCategoryIds = new ArrayList<>();
        for (Integer categoryId : categoryIds) {
            String checkSql = "SELECT COUNT(*) FROM service_categories WHERE category_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, categoryId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    validCategoryIds.add(categoryId);
                    System.out.println("Verified category ID: " + categoryId + " exists");
                } else {
                    System.out.println("Warning: Category ID " + categoryId + " does not exist in database, skipping");
                }
            }
        }
        
        // If no valid categories, just return without doing anything
        if (validCategoryIds.isEmpty()) {
            System.out.println("No valid categories to add, skipping mapping creation");
            return;
        }
        
        String sql = "INSERT INTO service_category_mapping (service_id, category_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Integer categoryId : validCategoryIds) {
                System.out.println("Adding mapping: service_id=" + serviceId + ", category_id=" + categoryId);
                
                pstmt.setInt(1, serviceId);
                pstmt.setInt(2, categoryId);
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            System.out.println("Successfully added " + results.length + " category mappings");
        }
    }
    
    // Helper method to ensure mapping table exists
    private void ensureMappingTableExists(Connection conn) throws SQLException {
        // Check if the table exists
        boolean tableExists = false;
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "service_category_mapping", null)) {
            tableExists = rs.next();
        }
        
        if (!tableExists) {
            // Create the mapping table
            String createTableSQL = "CREATE TABLE service_category_mapping (" +
                                 "service_id INT, " +
                                 "category_id INT, " +
                                 "PRIMARY KEY (service_id, category_id), " +
                                 "FOREIGN KEY (service_id) REFERENCES cleaning_services(service_id) ON DELETE CASCADE, " +
                                 "FOREIGN KEY (category_id) REFERENCES service_categories(category_id) ON DELETE CASCADE)";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Created service_category_mapping table");
            }
        }
    }
    
    // Helper method to delete category mappings
    private void deleteCategoryMappings(Connection conn, int serviceId) throws SQLException {
        String sql = "DELETE FROM service_category_mapping WHERE service_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        }
    }
    
    // Helper method to get category IDs for a service
    public List<Integer> getCategoryIdsForService(int serviceId) {
        List<Integer> categoryIds = new ArrayList<>();
        String sql = "SELECT category_id FROM service_category_mapping WHERE service_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categoryIds.add(rs.getInt("category_id"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving category IDs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categoryIds;
    }
    
    // Get service categories by name
    public List<String> getServiceCategoryNames(int serviceId) {
        List<String> categoryNames = new ArrayList<>();
        String sql = "SELECT sc.category_name FROM service_categories sc " +
                    "JOIN service_category_mapping scm ON sc.category_id = scm.category_id " +
                    "WHERE scm.service_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categoryNames.add(rs.getString("category_name"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving category names: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categoryNames;
    }
    
    // Get all services for a specific cleaner
    public List<CleaningService> getServicesByCleanerUsername(String username) {
        List<CleaningService> services = new ArrayList<>();
        String sql = "SELECT * FROM cleaning_services WHERE cleaner_username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CleaningService service = createServiceFromResultSet(rs);
                List<Integer> categoryIds = getCategoryIdsForService(service.getServiceId());
                service.setCategoryIds(categoryIds);
                
                services.add(service);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving services: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }

    // Get all cleaning services in the database
    public List<CleaningService> getAllServices() {
        List<CleaningService> services = new ArrayList<>();
        String sql = "SELECT * FROM cleaning_services";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                CleaningService service = createServiceFromResultSet(rs);
                
                // Get categories for this service
                List<Integer> categoryIds = getCategoryIdsForService(service.getServiceId());
                service.setCategoryIds(categoryIds);
                
                services.add(service);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving services: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    
    // Update an existing cleaning service
    public boolean updateService(CleaningService service) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 
            
            String sql = "UPDATE cleaning_services SET title = ?, description = ?, price = ?, " +
                        "available = ?, service_duration = ?, available_days = ?, " +
                        "available_start_time = ?, available_end_time = ? WHERE service_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, service.getTitle());
            pstmt.setString(2, service.getDescription());
            pstmt.setFloat(3, service.getPrice());
            pstmt.setBoolean(4, service.isAvailable());
            pstmt.setInt(5, service.getServiceDuration());
            pstmt.setString(6, service.getAvailableDays());
            pstmt.setTime(7, Time.valueOf(service.getAvailableStartTime()));
            pstmt.setTime(8, Time.valueOf(service.getAvailableEndTime()));
            pstmt.setInt(9, service.getServiceId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                deleteCategoryMappings(conn, service.getServiceId());
                
                // Add the new category 
                if (service.getCategoryIds() != null && !service.getCategoryIds().isEmpty()) {
                    saveCategoryMappings(conn, service.getServiceId(), service.getCategoryIds());
                }
                
                conn.commit(); 
                return true;
            }
            
            conn.rollback(); 
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Error updating service: " + e.getMessage());
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
                e.printStackTrace();
            }
        }
    }
    
    // Delete a cleaning service
    public boolean deleteService(int serviceId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 
            
            deleteCategoryMappings(conn, serviceId);
            String sql = "DELETE FROM cleaning_services WHERE service_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serviceId);
            
            int rowsAffected = pstmt.executeUpdate();
            conn.commit(); 
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Error deleting service: " + e.getMessage());
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
                e.printStackTrace();
            }
        }
    }
    
    // Find a cleaning service by ID
    public CleaningService findServiceById(int serviceId) {
        String sql = "SELECT * FROM cleaning_services WHERE service_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                CleaningService service = createServiceFromResultSet(rs);
                
                // Get categories for this service
                List<Integer> categoryIds = getCategoryIdsForService(serviceId);
                service.setCategoryIds(categoryIds);
                
                return service;
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding service: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Helper method to create a CleaningService from ResultSet
    private CleaningService createServiceFromResultSet(ResultSet rs) throws SQLException {
        int serviceDuration = 60;
        String availableDays = "Mon,Tue,Wed,Thu,Fri";
        LocalTime availableStartTime = LocalTime.of(9, 0);
        LocalTime availableEndTime = LocalTime.of(17, 0);
        
        try {
            serviceDuration = rs.getInt("service_duration");
        } catch (SQLException e) {
        }
        
        try {
            availableDays = rs.getString("available_days");
            if (availableDays == null) {
                availableDays = "Mon,Tue,Wed,Thu,Fri";
            }
        } catch (SQLException e) {
        }
        
        try {
            Time startTime = rs.getTime("available_start_time");
            if (startTime != null) {
                availableStartTime = startTime.toLocalTime();
            }
        } catch (SQLException e) {
        }
        
        try {
            Time endTime = rs.getTime("available_end_time");
            if (endTime != null) {
                availableEndTime = endTime.toLocalTime();
            }
        } catch (SQLException e) {
        }
        
        return new CleaningService(
            rs.getInt("service_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getFloat("price"),
            rs.getString("cleaner_username"),
            rs.getBoolean("available"),
            serviceDuration,
            availableDays,
            availableStartTime,
            availableEndTime
        );
    }
}