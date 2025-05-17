package controller.PlatformManager;

import entity.PlatformManager.ServiceCategory;
import java.util.List;
import java.util.ArrayList;

public class CategoryController {
    
    // Process a new category submission
    public boolean processNewCategory(String name) {
        if (!validateInput(name)) {
            return false;
        }
        
        // Check if the category already exists
        if (ServiceCategory.categoryExists(name)) {
            return false;
        }
        
        // Create and save the new category
        ServiceCategory category = new ServiceCategory(name);
        return category.save();
    }
    
    // Validate the category name input
    public boolean validateInput(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // Check if the name is too short
        if (name.trim().length() < 3) {
            return false;
        }
        
        // Check if the name is too long
        if (name.trim().length() > 50) {
            return false;
        }
        
        // Check if the name contains valid characters (letters, numbers, spaces, and hyphens)
        return name.trim().matches("^[a-zA-Z0-9\\s-]+$");
    }
    
    // Delete a category
    public boolean deleteCategory(int categoryId) {
        return ServiceCategory.deleteCategory(categoryId);
    }
    
    // Check if a category can be deleted (not linked to services)
    public boolean checkIfDeletable(int categoryId) {
        List<ServiceCategory> categories = getAllCategories();
        for (ServiceCategory category : categories) {
            if (category.getCategoryId() == categoryId) {
                return !category.isLinkedToServices();
            }
        }
        
        return false; 
    }
    
    // Fetches all service categories
    public List<ServiceCategory> fetchCategories() {
        return ServiceCategory.getAllCategories();
    }
    
    // Get all categories (alternative method name)
    public List<ServiceCategory> getAllCategories() {
        return ServiceCategory.getAllCategories();
    }
    
    // Update a category name
    public boolean updateCategoryName(int id, String newName) {
        if (!validateInput(newName)) {
            return false;
        }
        
        // Check if another category with this name already exists
        if (ServiceCategory.categoryExists(newName)) {
            return false;
        }
        
        // Find the category by ID
        List<ServiceCategory> categories = ServiceCategory.getAllCategories();
        for (ServiceCategory category : categories) {
            if (category.getCategoryId() == id) {
                // Update the category name
                return category.updateName(newName);
            }
        }
        
        return false; // Category not found
    }
    
    // Search for categories matching a keyword
    public List<ServiceCategory> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories(); 
        }
        
        List<ServiceCategory> allCategories = getAllCategories();
        List<ServiceCategory> matchingCategories = new ArrayList<>();
        
        String lowercaseKeyword = keyword.toLowerCase();
        
        for (ServiceCategory category : allCategories) {
            if (category.getName().toLowerCase().contains(lowercaseKeyword)) {
                matchingCategories.add(category);
            }
        }
        
        return matchingCategories;
    }
}