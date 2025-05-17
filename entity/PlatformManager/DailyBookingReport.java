package entity.PlatformManager;

import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyBookingReport {
    private final int      cleanerId;
    private final String   cleanerUsername;
    private final String   homeownerUsername;
    private final String   serviceTitle;
    private final LocalDate bookingDate;
    private final String   serviceCategory;
    private final int      bookingCount;
    private final double   totalEarned;

    public DailyBookingReport(int cleanerId, String cleanerUsername, String homeownerUsername, String serviceTitle, LocalDate bookingDate, String serviceCategory, int bookingCount, double totalEarned) {
        this.cleanerId         = cleanerId;
        this.cleanerUsername   = cleanerUsername;
        this.homeownerUsername = homeownerUsername;
        this.serviceTitle      = serviceTitle;
        this.bookingDate       = bookingDate;
        this.serviceCategory   = serviceCategory;
        this.bookingCount      = bookingCount;
        this.totalEarned       = totalEarned;
    }

    public int getCleanerId()             { return cleanerId; }
    public String getCleanerUsername()    { return cleanerUsername; }
    public String getHomeownerUsername()  { return homeownerUsername; }
    public String getServiceTitle()       { return serviceTitle; }
    public LocalDate getBookingDate()     { return bookingDate; }
    public String getServiceCategory()    { return serviceCategory; }
    public int getBookingCount()          { return bookingCount; }
    public double getTotalEarned()        { return totalEarned; }


    public static List<DailyBookingReport> fetchByDate(LocalDate date) {
        String sql =
            "SELECT "
        + "  b.booking_id           AS bookingId, "
        + "  u.user_id              AS cleanerId, "
        + "  b.cleaner_username     AS cleanerUsername, "
        + "  b.homeowner_username   AS homeownerUsername, "
        + "  cs.title               AS serviceTitle, "
        + "  b.booking_date         AS bookingDate, "
        + "  GROUP_CONCAT(DISTINCT sc.category_name "
        + "               ORDER BY sc.category_name "
        + "               SEPARATOR ', ') AS serviceCategory, "
        + "  1                      AS bookingCount, "
        + "  b.total_price          AS totalEarned "
        + "FROM bookings b "
        + "  JOIN users u ON b.cleaner_username = u.username "
        + "  JOIN cleaning_services cs ON b.service_id = cs.service_id "
        + "  LEFT JOIN service_category_mapping scm ON b.service_id = scm.service_id "
        + "  LEFT JOIN service_categories sc ON scm.category_id = sc.category_id "
        + "WHERE b.status = 'Completed' "
        + "  AND b.booking_date = ? "
        + "GROUP BY b.booking_id";

        List<DailyBookingReport> report = new ArrayList<>();
        try ( Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql) ) {
            ps.setDate(1, Date.valueOf(date));
            try ( ResultSet rs = ps.executeQuery() ) {
                while (rs.next()) {
                    report.add(new DailyBookingReport(
                        rs.getInt("cleanerId"),
                        rs.getString("cleanerUsername"),
                        rs.getString("homeownerUsername"),
                        rs.getString("serviceTitle"),
                        rs.getDate("bookingDate").toLocalDate(),
                        rs.getString("serviceCategory"),
                        rs.getInt("bookingCount"),
                        rs.getDouble("totalEarned")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return report;
    }
}
