package entity.Homeowner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;

/**
 * DAO for a homeowner’s past completed bookings.
 */
public class BookingHistory {

    // Find a homeowner’s past services by username, optional service IDs, and date range.
    public List<BookingService> findBy(
            String homeownerUsername,
            List<Integer> serviceIds,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        List<BookingService> history = new ArrayList<>();

        StringBuilder sql = new StringBuilder()
            .append("SELECT * FROM bookings ")
            .append("WHERE homeowner_username = ? ")
            .append("AND status = 'Completed' ")
            .append("AND booking_date BETWEEN ? AND ? ");

        // Add service ID filter if provided
        if (serviceIds != null && !serviceIds.isEmpty()) {
            sql.append("AND service_id IN (")
            .append(String.join(",", serviceIds.stream().map(i -> "?").toArray(String[]::new)))
            .append(") ");
        }

        sql.append("ORDER BY booking_date DESC");

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            int idx = 1;
            ps.setString(idx++, homeownerUsername);
            ps.setDate(idx++, java.sql.Date.valueOf(dateFrom));
            ps.setDate(idx++, java.sql.Date.valueOf(dateTo));

            if (serviceIds != null && !serviceIds.isEmpty()) {
                for (int sid : serviceIds) {
                    ps.setInt(idx++, sid);
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(new BookingService(
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
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving booking history: " + e.getMessage());
            e.printStackTrace();
        }

        return history;
    }
}
