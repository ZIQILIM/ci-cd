package controller.Cleaner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import entity.CleaningServiceDAO;
import entity.Cleaner.CleaningService;
import entity.Cleaner.MatchHistory;
import entity.Homeowner.BookingService;

public class CleaningServiceController {
    private CleaningServiceDAO serviceDAO;
    private MatchHistory matchHistory;
    
    public CleaningServiceController() {
        this.serviceDAO = new CleaningServiceDAO();
        this.matchHistory = new MatchHistory();
    }
    
    // From CleaningServiceController
    public boolean createServiceWithCategories(String serviceName, String description, float price, 
                              String cleanerUsername, int duration, String availableDays,
                              LocalTime startTime, LocalTime endTime, List<Integer> categoryIds) {
        if (serviceName == null || serviceName.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            price <= 0 || cleanerUsername == null || cleanerUsername.isEmpty() ||
            duration <= 0 || availableDays == null || availableDays.isEmpty() ||
            startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            return false;
        }
        
        CleaningService service = new CleaningService(serviceName, description, price, cleanerUsername);
        service.setServiceDuration(duration);
        service.setAvailableDays(availableDays);
        service.setAvailableStartTime(startTime);
        service.setAvailableEndTime(endTime);
        service.setCategoryIds(categoryIds);
        
        System.out.println("Creating service with " + (categoryIds != null ? categoryIds.size() : 0) + " categories");
        if (categoryIds != null) {
            for (Integer categoryId : categoryIds) {
                System.out.println("Category ID: " + categoryId);
            }
        }
        
        return serviceDAO.createService(service);
    }
    
    public boolean createService(String serviceName, String description, float price, 
                              String cleanerUsername, int duration, String availableDays,
                              LocalTime startTime, LocalTime endTime) {
        if (serviceName == null || serviceName.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            price <= 0 || cleanerUsername == null || cleanerUsername.isEmpty() ||
            duration <= 0 || availableDays == null || availableDays.isEmpty() ||
            startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            return false;
        }
        
        CleaningService service = new CleaningService(serviceName, description, price, cleanerUsername);
        service.setServiceDuration(duration);
        service.setAvailableDays(availableDays);
        service.setAvailableStartTime(startTime);
        service.setAvailableEndTime(endTime);
        
        return serviceDAO.createService(service);
    }
    
    public boolean createService(String serviceName, String description, float price, String cleanerUsername) {
        if (serviceName == null || serviceName.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            price <= 0 || cleanerUsername == null || cleanerUsername.isEmpty()) {
            return false;
        }
        
        CleaningService service = new CleaningService(serviceName, description, price, cleanerUsername);
        
        return serviceDAO.createService(service);
    }
    
    // From DeleteCleaningServiceController
    public boolean deleteService(int serviceId) {
        if (serviceDAO.findServiceById(serviceId) == null) {
            return false;
        }
        
        return serviceDAO.deleteService(serviceId);
    }
    
    // From SearchCleaningServiceController
    public List<CleaningService> searchCleaningService(int serviceId, String title) {
        // Get all services
        List<CleaningService> allServices = serviceDAO.getAllServices();
        List<CleaningService> matchingServices = new ArrayList<>();
        
        for (CleaningService service : allServices) {
            boolean matches = true;
            
            if (serviceId > 0 && service.getServiceId() != serviceId) {
                matches = false;
            }
            
           
            if (title != null && !title.trim().isEmpty() && 
                !service.getTitle().toLowerCase().contains(title.toLowerCase().trim())) {
                matches = false;
            }
            
            
            if (matches) {
                matchingServices.add(service);
            }
        }
        
        return matchingServices;
    }
    
    // From UpdateCleaningServiceController
    public boolean updateService(int serviceId, CleaningService service) {
        if (service == null || service.getTitle() == null || service.getTitle().trim().isEmpty() ||
            service.getDescription() == null || service.getDescription().trim().isEmpty() ||
            service.getPrice() <= 0 || service.getCleanerUsername() == null || 
            service.getCleanerUsername().isEmpty() ||
            service.getServiceDuration() <= 0 || service.getAvailableDays() == null ||
            service.getAvailableDays().isEmpty() || service.getAvailableStartTime() == null ||
            service.getAvailableEndTime() == null || 
            !service.getAvailableStartTime().isBefore(service.getAvailableEndTime())) {
            return false;
        }
        
        // Verify this is the correct service ID
        if (service.getServiceId() != serviceId) {
            return false;
        }
        
        return serviceDAO.updateService(service);
    }
    
    // From both controllers
    public List<CleaningService> getServicesByCleanerUsername(String username) {
        return serviceDAO.getServicesByCleanerUsername(username);
    }
    
    public List<CleaningService> getAllServices() {
        return serviceDAO.getAllServices();
    }
    
    public CleaningService findServiceById(int serviceId) {
        return serviceDAO.findServiceById(serviceId);
    }
    
    // From ViewCleaningServiceController
    public boolean viewService(int serviceId) {
        CleaningService service = serviceDAO.findServiceById(serviceId);
        return service != null;
    }
    
    // Simplified updateService method
    public boolean updateService(CleaningService service) {
        if (service == null || service.getTitle() == null || service.getTitle().trim().isEmpty() ||
            service.getDescription() == null || service.getDescription().trim().isEmpty() ||
            service.getPrice() <= 0 || service.getCleanerUsername() == null || 
            service.getCleanerUsername().isEmpty() ||
            service.getServiceDuration() <= 0 || service.getAvailableDays() == null ||
            service.getAvailableDays().isEmpty() || service.getAvailableStartTime() == null ||
            service.getAvailableEndTime() == null || 
            !service.getAvailableStartTime().isBefore(service.getAvailableEndTime())) {
            return false;
        }
        
        return serviceDAO.updateService(service);
    }
    
    // Get match history for a cleaner filtered by date range and optionally service IDs
    public List<BookingService> getMatchHistory(String cleanerUsername, LocalDate dateFrom, LocalDate dateTo, List<Integer> serviceIds) {
        return matchHistory.findBy(cleanerUsername, dateFrom, dateTo, serviceIds);
    }
}