package controller.Homeowner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import entity.Homeowner.BookingHistory;
import entity.Homeowner.BookingService;
import entity.BookingDAO;
import entity.CleaningServiceDAO;

// Controller for the homeowner’s “Service History” page.
public class SearchServiceHistoryController {
    private final BookingHistory      historyDao;
    private final BookingDAO          bookingDao;
    private final CleaningServiceDAO  serviceDao;

    public SearchServiceHistoryController() {
        this.historyDao = new BookingHistory();
        this.bookingDao = new BookingDAO();
        this.serviceDao = new CleaningServiceDAO();
    }

    
    // Fetch completed bookings for a homeowner, optionally filtered by
    public List<BookingService> getServiceHistory(
        String homeownerUsername,
        List<Integer> serviceIds,
        LocalDate dateFrom,
        LocalDate dateTo
    ) {
        return historyDao.findBy(homeownerUsername, serviceIds, dateFrom, dateTo);
    }

    // Return the distinct service IDs that this homeowner has ever booked.
    public List<Integer> getHomeownerServiceIds(String homeownerUsername) {
        List<BookingService> all = bookingDao.getBookingsByHomeowner(homeownerUsername);
        List<Integer> ids = new ArrayList<>();
        for (BookingService b : all) {
            if (!ids.contains(b.getServiceId())) {
                ids.add(b.getServiceId());
            }
        }
        return ids;
    }

    // Look up a human‐readable service name by ID.
    public String getServiceName(int serviceId) {
        try {
            return serviceDao.findServiceById(serviceId).getTitle();
        } catch (Exception e) {
            return "Service " + serviceId;
        }
    }
}
