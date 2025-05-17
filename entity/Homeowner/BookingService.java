package entity.Homeowner;

import java.time.LocalDate;
import java.time.LocalTime;

// Entity representing a booking record, both for new creations and DB loads.
public class BookingService {
    private int bookingId;
    private String homeownerUsername;
    private String cleanerUsername;
    private int serviceId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String address;
    private String notes;
    private float totalPrice;

    public BookingService(
        String homeownerUsername,
        String cleanerUsername,
        int serviceId,
        LocalDate bookingDate,
        LocalTime startTime,
        LocalTime endTime,
        String address,
        String notes,
        float totalPrice
    ) {
        this.homeownerUsername = homeownerUsername;
        this.cleanerUsername   = cleanerUsername;
        this.serviceId         = serviceId;
        this.bookingDate       = bookingDate;
        this.startTime         = startTime;
        this.endTime           = endTime;
        this.address           = address;
        this.notes             = notes;
        this.totalPrice        = totalPrice;
        this.status            = "Pending";
    }

    public BookingService(
        int bookingId,
        String homeownerUsername,
        String cleanerUsername,
        int serviceId,
        LocalDate bookingDate,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String address,
        String notes,
        float totalPrice
    ) {
        this.bookingId          = bookingId;
        this.homeownerUsername  = homeownerUsername;
        this.cleanerUsername    = cleanerUsername;
        this.serviceId          = serviceId;
        this.bookingDate        = bookingDate;
        this.startTime          = startTime;
        this.endTime            = endTime;
        this.status             = status;
        this.address            = address;
        this.notes              = notes;
        this.totalPrice         = totalPrice;
    }

    // Setter for DAO to assign the generated key 
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    // Getters
    public int       getBookingId()            { return bookingId; }
    public String    getHomeownerUsername()    { return homeownerUsername; }
    public String    getCleanerUsername()      { return cleanerUsername; }
    public int       getServiceId()            { return serviceId; }
    public LocalDate getBookingDate()          { return bookingDate; }
    public LocalTime getStartTime()            { return startTime; }
    public LocalTime getEndTime()              { return endTime; }
    public String    getStatus()               { return status; }
    public String    getAddress()              { return address; }
    public String    getNotes()                { return notes; }
    public float     getTotalPrice()           { return totalPrice; }


public String getTitle() {
        try {
            return new entity.CleaningServiceDAO()
                       .findServiceById(this.serviceId)
                       .getTitle();
        } catch (Exception e) {
            return "Service " + this.serviceId;
        }
    }
}