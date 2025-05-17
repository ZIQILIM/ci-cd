package entity.Homeowner;

public class SearchCriteria {
    private String location;
    private String serviceType;
    private float minPrice;
    private float maxPrice;
    private String availableDays; 
    
    public SearchCriteria() {
        this.location = "";
        this.serviceType = "";
        this.minPrice = 0.0f;
        this.maxPrice = Float.MAX_VALUE;
        this.availableDays = "";
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getServiceType() {
        return serviceType;
    }
    
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    
    public float getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }
    
    public float getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public String getAvailableDays() {
        return availableDays;
    }
    
    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }
}