package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;

// Entity class for service records
public class ServiceRecord {
    private int bookingId;
    private String homeownerUsername;
    private String cleanerUsername;
    private String cleanerName;
    private int serviceId;
    private String serviceName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String address;
    private String notes;
    private float price;
    
    // Constructor
    public ServiceRecord(
        int bookingId,
        String homeownerUsername,
        String cleanerUsername,
        String cleanerName,
        int serviceId,
        String serviceName,
        LocalDate bookingDate,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String address,
        String notes,
        float price
    ) {
        this.bookingId = bookingId;
        this.homeownerUsername = homeownerUsername;
        this.cleanerUsername = cleanerUsername;
        this.cleanerName = cleanerName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.address = address;
        this.notes = notes;
        this.price = price;
    }
    
    // Fetch past services for a homeowner with optional filtering
    public static List<ServiceRecord> fetchPastServices(
        String homeownerUsername,
        List<String> serviceIds,
        LocalDate dateFrom,
        LocalDate dateTo
    ) {
        List<ServiceRecord> services = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT b.booking_id, b.homeowner_username, b.cleaner_username, ");
        sqlBuilder.append("u.name as cleaner_name, ");
        sqlBuilder.append("b.service_id, cs.title as service_name, ");
        sqlBuilder.append("b.booking_date, b.start_time, b.end_time, ");
        sqlBuilder.append("b.status, b.address, b.notes, b.total_price ");
        sqlBuilder.append("FROM bookings b ");
        sqlBuilder.append("JOIN users u ON b.cleaner_username = u.username ");
        sqlBuilder.append("JOIN cleaning_services cs ON b.service_id = cs.service_id ");
        sqlBuilder.append("WHERE b.homeowner_username = ? ");
        sqlBuilder.append("AND b.status = 'Completed' ");
        sqlBuilder.append("AND b.booking_date BETWEEN ? AND ? ");
        
        // Add service ID filter 
        if (serviceIds != null && !serviceIds.isEmpty()) {
            sqlBuilder.append("AND b.service_id IN (");
            for (int i = 0; i < serviceIds.size(); i++) {
                sqlBuilder.append("?");
                if (i < serviceIds.size() - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(") ");
        }
        
        sqlBuilder.append("ORDER BY b.booking_date DESC");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            int paramIndex = 1;
            pstmt.setString(paramIndex++, homeownerUsername);
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(dateFrom));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(dateTo));
            
            // Set service ID parameters 
            if (serviceIds != null && !serviceIds.isEmpty()) {
                for (String serviceId : serviceIds) {
                    pstmt.setInt(paramIndex++, Integer.parseInt(serviceId));
                }
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ServiceRecord service = new ServiceRecord(
                    rs.getInt("booking_id"),
                    rs.getString("homeowner_username"),
                    rs.getString("cleaner_username"),
                    rs.getString("cleaner_name"),
                    rs.getInt("service_id"),
                    rs.getString("service_name"),
                    rs.getDate("booking_date").toLocalDate(),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getString("status"),
                    rs.getString("address"),
                    rs.getString("notes"),
                    rs.getFloat("total_price")
                );
                
                services.add(service);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving past services: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    
    // Getters
    public int getBookingId() {
        return bookingId;
    }
    
    public String getHomeownerUsername() {
        return homeownerUsername;
    }
    
    public String getCleanerUsername() {
        return cleanerUsername;
    }
    
    public String getCleanerName() {
        return cleanerName;
    }
    
    public int getServiceId() {
        return serviceId;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public LocalDate getBookingDate() {
        return bookingDate;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public float getPrice() {
        return price;
    }
}