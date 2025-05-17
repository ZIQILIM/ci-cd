package entity.Cleaner;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CleaningService {
    private int serviceId;
    private String title;
    private String description;
    private float price;
    private String cleanerUsername;
    private boolean available;
    
    private int serviceDuration;
    private String availableDays;
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
    private List<Integer> categoryIds;
    
    // Updated constructor for new services
    public CleaningService(String title, String description, float price, String cleanerUsername) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.cleanerUsername = cleanerUsername;
        this.available = true;
        this.serviceDuration = 60; 
        this.availableDays = "Mon,Tue,Wed,Thu,Fri"; 
        this.availableStartTime = LocalTime.of(9, 0); 
        this.availableEndTime = LocalTime.of(17, 0); 
        this.categoryIds = new ArrayList<>(); 
    }
    
    public CleaningService(int serviceId, String title, String description, float price, 
                        String cleanerUsername, boolean available,
                        int serviceDuration, String availableDays,
                        LocalTime availableStartTime, LocalTime availableEndTime) {
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.cleanerUsername = cleanerUsername;
        this.available = available;
        this.serviceDuration = serviceDuration;
        this.availableDays = availableDays;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.categoryIds = new ArrayList<>(); // 
    }
    
    public List<Integer> getCategoryIds() {
        return categoryIds;
    }
    
    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
    }
    
    public void addCategory(int categoryId) {
        if (categoryIds == null) {
            categoryIds = new ArrayList<>();
        }
        if (!categoryIds.contains(categoryId)) {
            categoryIds.add(categoryId);
        }
    }
    
    public void removeCategory(int categoryId) {
        if (categoryIds != null) {
            categoryIds.remove(Integer.valueOf(categoryId));
        }
    }
    
    public int getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public float getPrice() {
        return price;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    public String getCleanerUsername() {
        return cleanerUsername;
    }
    
    public void setCleanerUsername(String cleanerUsername) {
        this.cleanerUsername = cleanerUsername;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public int getServiceDuration() {
        return serviceDuration;
    }
    
    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }
    
    public String getAvailableDays() {
        return availableDays;
    }
    
    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }
    
    public LocalTime getAvailableStartTime() {
        return availableStartTime;
    }
    
    public void setAvailableStartTime(LocalTime availableStartTime) {
        this.availableStartTime = availableStartTime;
    }
    
    public LocalTime getAvailableEndTime() {
        return availableEndTime;
    }
    
    public void setAvailableEndTime(LocalTime availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    
}