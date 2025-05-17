// entity/PlatformManager/MonthlyBookingReport.java
package entity.PlatformManager;

import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MonthlyBookingReport {
    private int cleanerId;
    private String cleanerUsername;
    private String homeownerUsername;
    private String serviceTitle;
    private LocalDate bookingDate;
    private String category;
    private int bookingCount;
    private double amount;

    public MonthlyBookingReport(int cleanerId,
                                String cleanerUsername,
                                String homeownerUsername,
                                String serviceTitle,
                                LocalDate bookingDate,
                                String category,
                                int bookingCount,
                                double amount) {
        this.cleanerId         = cleanerId;
        this.cleanerUsername   = cleanerUsername;
        this.homeownerUsername = homeownerUsername;
        this.serviceTitle      = serviceTitle;
        this.bookingDate       = bookingDate;
        this.category          = category;
        this.bookingCount      = bookingCount;
        this.amount            = amount;
    }

    // Getters 
    public int       getCleanerId()        { return cleanerId; }
    public String    getCleanerUsername()  { return cleanerUsername; }
    public String    getHomeownerUsername(){ return homeownerUsername; }
    public String    getServiceTitle()     { return serviceTitle; }
    public LocalDate getBookingDate()      { return bookingDate; }
    public String    getCategory()         { return category; }
    public int       getBookingCount()     { return bookingCount; }
    public double    getAmount()           { return amount; }

    // DAO METHOD
    public static List<MonthlyBookingReport> fetchByMonth(int month, int year) {
        List<MonthlyBookingReport> list = new ArrayList<>();
        String sql =
        "SELECT b.booking_id, " +
        "       u.user_id          AS cleaner_id, " +
        "       b.cleaner_username, " +
        "       b.homeowner_username, " +
        "       cs.title           AS service_title, " +
        "       b.booking_date, " +
        "       GROUP_CONCAT(DISTINCT sc.category_name " +
        "             ORDER BY sc.category_name SEPARATOR ', ') AS category, " +
        "       1                  AS booking_count, " +
        "       b.total_price      AS amount " +
        "  FROM bookings b " +
        "  JOIN users u   ON b.cleaner_username = u.username " +
        "  JOIN cleaning_services cs ON b.service_id = cs.service_id " +
        "  LEFT JOIN service_category_mapping scm ON b.service_id = scm.service_id " +
        "  LEFT JOIN service_categories sc ON scm.category_id = sc.category_id " +
        " WHERE MONTH(b.booking_date)=? " +
        "   AND YEAR(b.booking_date)=? " +
        "   AND b.status = 'Completed' " +
        " GROUP BY b.booking_id, u.user_id";

        new DatabaseConnection();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("booking_date").toLocalDate();
                    list.add(new MonthlyBookingReport(
                        rs.getInt   ("cleaner_id"),
                        rs.getString("cleaner_username"),
                        rs.getString("homeowner_username"),
                        rs.getString("service_title"),
                        date,
                        rs.getString("category"),
                        rs.getInt   ("booking_count"),
                        rs.getDouble("amount")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
