package entity.Cleaner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import entity.Homeowner.BookingService;

public class MatchHistory {
    
    // Find matches by cleaner username, date range, and optionally service IDs
    public List<BookingService> findBy(String cleanerUsername, LocalDate dateFrom, LocalDate dateTo, List<Integer> serviceIds) {
        List<BookingService> matches = new ArrayList<>();
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM bookings WHERE cleaner_username = ? ");
        sqlBuilder.append("AND status = 'Completed' ");
        sqlBuilder.append("AND booking_date BETWEEN ? AND ? ");
        
        // Add service ID filter if provided
        if (serviceIds != null && !serviceIds.isEmpty()) {
            sqlBuilder.append("AND service_id IN (");
            for (int i = 0; i < serviceIds.size(); i++) {
                sqlBuilder.append("?");
                if (i < serviceIds.size() - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(") ");
        }
        
        sqlBuilder.append("ORDER BY booking_date DESC");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            int paramIndex = 1;
            pstmt.setString(paramIndex++, cleanerUsername);
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(dateFrom));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(dateTo));
            
            // Set service ID parameters if provided
            if (serviceIds != null && !serviceIds.isEmpty()) {
                for (Integer serviceId : serviceIds) {
                    pstmt.setInt(paramIndex++, serviceId);
                }
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BookingService booking = new BookingService(
                    rs.getInt("booking_id"),
                    rs.getString("homeowner_username"),
                    rs.getString("cleaner_username"),
                    rs.getInt("service_id"),
                    rs.getDate("booking_date").toLocalDate(),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getString("status"),
                    rs.getString("address"),
                    rs.getString("notes"),
                    rs.getFloat("total_price")
                );
                
                matches.add(booking);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving match history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return matches;
    }
}