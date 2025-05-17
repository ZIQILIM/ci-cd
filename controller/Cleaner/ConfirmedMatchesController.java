package controller.Cleaner;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import entity.ConfirmedMatch;
import entity.CleaningServiceDAO;
import entity.Homeowner.BookingService;

public class ConfirmedMatchesController {
    private ConfirmedMatch confirmedMatch;
    private CleaningServiceDAO serviceDAO;
    
    public ConfirmedMatchesController() {
        this.confirmedMatch = new ConfirmedMatch();
        this.serviceDAO = new CleaningServiceDAO();
    }

    // Get confirmed matches for a cleaner filtered by services and date range
    public List<BookingService> getConfirmedMatches(
        String cleanerUsername,
        List<Integer> serviceIds,
        LocalDate dateFrom,
        LocalDate dateTo
    ) {
        return confirmedMatch.fetchConfirmedMatches(
            cleanerUsername,
            serviceIds,
            dateFrom,
            dateTo
        );
    }
    
    // Get the list of service IDs for a cleaner's services
    public List<Integer> getCleanerServiceIds(String cleanerUsername) {
        List<Integer> serviceIds = new ArrayList<>();
        serviceDAO.getServicesByCleanerUsername(cleanerUsername).forEach(service -> {
            serviceIds.add(service.getServiceId());
        });
        return serviceIds;
    }

    // Get the name of a service by its ID
    public String getServiceName(int serviceId) {
        try {
            return serviceDAO.findServiceById(serviceId).getTitle();
        } catch (Exception e) {
            return "Service " + serviceId;
        }
    }
}