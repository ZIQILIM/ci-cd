package controller.Homeowner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import entity.CleaningServiceDAO;
import entity.Cleaner.CleanerAccount;
import entity.Cleaner.CleaningService;
import entity.BookingDAO;
import entity.Homeowner.BookingService;
import entity.Homeowner.SearchCriteria;
import entity.UserAdmin.UserAccount;
import entity.UserDAO;

// Controller for homeowner booking operations
public class BookingController {
    private final BookingDAO bookingDAO;
    private final UserDAO    userDAO;
    private final CleaningServiceDAO serviceDAO;
    
    public BookingController() {
        this.bookingDAO = new BookingDAO();
        this.userDAO    = new UserDAO();
        this.serviceDAO = new CleaningServiceDAO();

        bookingDAO.createBookingsTableIfNeeded();
    }
    
    // Book a service for a homeowner.
    public boolean bookService(
        UserAccount     homeowner,
        CleanerAccount  cleaner,
        CleaningService service,
        LocalDate       bookingDate,
        LocalTime       startTime,
        String          address,
        String          notes
    ) {
        // Calculate end time based on service duration
        LocalTime endTime = startTime.plusMinutes(service.getServiceDuration());

        // Create a new BookingService 
        BookingService booking = new BookingService(
            homeowner.getUsername(),   
            cleaner.getUsername(),     
            service.getServiceId(),     
            bookingDate,                
            startTime,                 
            endTime,                   
            address,                 
            notes,                      
            (float) service.getPrice()  
        );

        return bookingDAO.createBooking(booking);
    }
    
    // Cancel an existing booking by its ID.
    public boolean cancelBooking(int bookingId) {
        return bookingDAO.cancelBooking(bookingId);
    }
    
    // Search for cleaners and their services matching given criteria.
    public List<Map<String,Object>> handleSearch(SearchCriteria criteria) {
        // Load and filter users to cleaners
        List<UserAccount> allUsers = userDAO.getAllUsers();
        List<CleanerAccount> cleaners = new ArrayList<>();
        for (UserAccount u : allUsers) {
            if ("Cleaner".equals(u.getAccountType()) &&
                (u.getStatus() == null || "Active".equals(u.getStatus()))) {
                CleanerAccount c = new CleanerAccount(
                    u.getUsername(), u.getPassword(), u.getName(), u.getEmail()
                );
                c.setAddress(u.getAddress());
                c.setPhoneNumber(u.getPhoneNumber());
                cleaners.add(c);
            }
        }
        
        // Group services by cleaner
        List<CleaningService> allServices = serviceDAO.getAllServices();
        Map<String, List<CleaningService>> cleanerServices = new HashMap<>();
        for (CleaningService s : allServices) {
            cleanerServices
                .computeIfAbsent(s.getCleanerUsername(), k -> new ArrayList<>())
                .add(s);
        }
        
        // Apply criteria filters
        List<Map<String,Object>> results = new ArrayList<>();
        for (CleanerAccount cleanerAcc : cleaners) {
            List<CleaningService> services =
                cleanerServices.getOrDefault(cleanerAcc.getUsername(), new ArrayList<>());
            if (services.isEmpty()) continue;
            List<CleaningService> matched = filterServices(cleanerAcc, services, criteria);
            if (!matched.isEmpty()) {
                Map<String,Object> entry = new HashMap<>();
                entry.put("cleaner", cleanerAcc);
                entry.put("services", matched);
                results.add(entry);
            }
        }
        return results;
    }
    
    // filter a cleaner's services by criteria
    private List<CleaningService> filterServices(
        CleanerAccount cleaner,
        List<CleaningService> services,
        SearchCriteria criteria
    ) {
        List<CleaningService> out = new ArrayList<>();
        // Location filter
        if (!criteria.getLocation().isEmpty()) {
            String loc = cleaner.getAddress();
            if (loc == null || !loc.toLowerCase().contains(criteria.getLocation().toLowerCase())) {
                return out;
            }
        }
        // Type, price, days
        for (CleaningService s : services) {
            if (!criteria.getServiceType().isEmpty()) {
                String key = criteria.getServiceType().toLowerCase();
                if (!s.getTitle().toLowerCase().contains(key) &&
                    !s.getDescription().toLowerCase().contains(key)) continue;
            }
            if (s.getPrice() < criteria.getMinPrice() || s.getPrice() > criteria.getMaxPrice()) continue;
            if (!criteria.getAvailableDays().isEmpty()) {
                boolean ok = false;
                for (String d : criteria.getAvailableDays().split(",")) {
                    if (s.getAvailableDays().contains(d.trim())) { ok = true; break; }
                }
                if (!ok) continue;
            }
            out.add(s);
        }
        return out;
    }
    
    // Retrieve the homeowner's past booking history.
    public List<BookingService> getBookingsByHomeowner(String homeownerUsername) {
        return bookingDAO.getBookingsByHomeowner(homeownerUsername);
    }
    
    // Lookup a single booking by its ID.
    public BookingService getBookingById(int bookingId) {
        return bookingDAO.getBookingById(bookingId);
    }
    
    // Accept a pending booking 
    public boolean acceptBooking(int bookingId) {
        return bookingDAO.acceptBooking(bookingId);
    }
    
    // Mark a booking as completed.
    public boolean completeBooking(int bookingId) {
        return bookingDAO.completeBooking(bookingId);
    }
}
