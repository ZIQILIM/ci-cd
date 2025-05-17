package entity.PlatformManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;

public class ServiceCategory {
    private int categoryId;
    private String name;
    
    public ServiceCategory(String name) {
        this.name = name;
    }
    
    public ServiceCategory(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean save() {
        String sql = "INSERT INTO service_categories (category_name) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, this.name);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the auto-generated ID
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.categoryId = rs.getInt(1);
                    }
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.out.println("Error saving category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update the category name 
    public boolean updateName(String newName) {
        String sql = "UPDATE service_categories SET category_name = ? WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newName);
            pstmt.setInt(2, this.categoryId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                this.name = newName;
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.out.println("Error updating category name: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    
    // Check if this category is linked to any services
     
    public boolean isLinkedToServices() {
        String sql = "SELECT COUNT(*) FROM service_category_mapping WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, this.categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking if category is linked: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false; 
    }
    
    
    // Delete this category from the database
     
    public boolean delete() {
        String sql = "DELETE FROM service_categories WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, this.categoryId);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error deleting category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Static method to get all categories
    public static List<ServiceCategory> getAllCategories() {
        List<ServiceCategory> categories = new ArrayList<>();
        
        String sql = "SELECT category_id, category_name FROM service_categories";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("category_id");
                String name = rs.getString("category_name");
                categories.add(new ServiceCategory(id, name));
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving categories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    // Method to check if category exists
    public static boolean categoryExists(String name) {
        String sql = "SELECT COUNT(*) FROM service_categories WHERE category_name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking category existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Method to delete a category
    public static boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM service_categories WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error deleting category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if this category matches a keyword
     
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true; // Empty keyword matches everything
        }
        
        return this.name.toLowerCase().contains(keyword.toLowerCase());
    }
    
    @Override
    public String toString() {
        return name;
    }
}