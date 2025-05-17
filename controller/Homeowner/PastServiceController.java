package controller.Homeowner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import entity.ServiceRecord;

// Controller for handling past service operations for homeowners
public class PastServiceController {
    
    // Get past services for a homeowner with optional filtering
    public List<ServiceRecord> getPastServices(
        String homeownerUsername,
        List<String> serviceIds,
        LocalDate dateFrom,
        LocalDate dateTo
    ) {
        // Call the entity to fetch the data
        return ServiceRecord.fetchPastServices(
            homeownerUsername,
            serviceIds,
            dateFrom,
            dateTo
        );
    }

    // Get a list of available services that the homeowner has used before
    public List<String> getAvailableServices(String homeownerUsername) {
        List<String> services = new ArrayList<>();
        services.add("Regular Cleaning");
        services.add("Deep Cleaning");
        services.add("Move-in/Move-out Cleaning");
        services.add("Office Cleaning");
        services.add("Carpet Cleaning");
        return services;
    }
    
    // Get service IDs by service name
     
    public List<String> getServiceIdsByName(String serviceName) {
        List<String> serviceIds = new ArrayList<>();
        
        switch(serviceName) {
            case "Regular Cleaning":
                serviceIds.add("1");
                serviceIds.add("2");
                break;
            case "Deep Cleaning":
                serviceIds.add("3");
                break;
            case "Move-in/Move-out Cleaning":
                serviceIds.add("4");
                break;
            case "Office Cleaning":
                serviceIds.add("5");
                break;
            case "Carpet Cleaning":
                serviceIds.add("6");
                break;
        }
        
        return serviceIds;
    }
}