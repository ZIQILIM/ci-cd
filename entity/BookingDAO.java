package entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import entity.Homeowner.BookingService;

public class BookingDAO {

    public BookingDAO() {
    }

    public boolean createBooking(BookingService booking) {
        String sql =
        "INSERT INTO bookings " +
        "(homeowner_username, cleaner_username, service_id, booking_date, " +
        " start_time, end_time, status, address, notes, total_price) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, booking.getHomeownerUsername());
            ps.setString(2, booking.getCleanerUsername());
            ps.setInt(3, booking.getServiceId());
            ps.setDate(4, Date.valueOf(booking.getBookingDate()));
            ps.setTime(5, Time.valueOf(booking.getStartTime()));
            ps.setTime(6, Time.valueOf(booking.getEndTime()));
            ps.setString(7, booking.getStatus());
            ps.setString(8, booking.getAddress());
            ps.setString(9, booking.getNotes());
            ps.setFloat(10, booking.getTotalPrice());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        booking.setBookingId(keys.getInt(1));  // setter now exists
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Fetch homeowner’s historical bookings
    public List<BookingService> getBookingsByHomeowner(String homeownerUsername) {
        List<BookingService> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE homeowner_username = ? ORDER BY booking_date DESC";
        try (
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, homeownerUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookingService(
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
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Get all bookings for a cleaner
    public List<BookingService> getBookingsByCleaner(String cleanerUsername) {
        List<BookingService> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE cleaner_username = ? ORDER BY booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cleanerUsername);
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
                
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting bookings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bookings;
    }
    
    // Update a booking
    public boolean updateBooking(BookingService booking) {
        String sql = "UPDATE bookings SET homeowner_username = ?, cleaner_username = ?, " +
                    "service_id = ?, booking_date = ?, start_time = ?, end_time = ?, " +
                    "status = ?, address = ?, notes = ?, total_price = ? " +
                    "WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, booking.getHomeownerUsername());
            pstmt.setString(2, booking.getCleanerUsername());
            pstmt.setInt(3, booking.getServiceId());
            pstmt.setDate(4, java.sql.Date.valueOf(booking.getBookingDate()));
            pstmt.setTime(5, java.sql.Time.valueOf(booking.getStartTime()));
            pstmt.setTime(6, java.sql.Time.valueOf(booking.getEndTime()));
            pstmt.setString(7, booking.getStatus());
            pstmt.setString(8, booking.getAddress());
            pstmt.setString(9, booking.getNotes());
            pstmt.setFloat(10, booking.getTotalPrice());
            pstmt.setInt(11, booking.getBookingId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating booking: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Cancel a booking
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get a booking by ID
    public BookingService getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new BookingService(
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
            }


        } catch (SQLException e) {
            System.out.println("Error getting booking: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Create the bookings table if it doesn't exist
    public boolean createBookingsTableIfNeeded() {
        String sql = "CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "homeowner_username VARCHAR(50) NOT NULL, " +
                "cleaner_username VARCHAR(50) NOT NULL, " +
                "service_id INT NOT NULL, " +
                "booking_date DATE NOT NULL, " +
                "start_time TIME NOT NULL, " +
                "end_time TIME NOT NULL, " +
                "status VARCHAR(20) NOT NULL, " +
                "address VARCHAR(255) NOT NULL, " +
                "notes TEXT, " +
                "total_price FLOAT NOT NULL, " +
                "FOREIGN KEY (homeowner_username) REFERENCES users(username), " +
                "FOREIGN KEY (cleaner_username) REFERENCES users(username), " +
                "FOREIGN KEY (service_id) REFERENCES cleaning_services(service_id)" +
                ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("Bookings table created or already exists");
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error creating bookings table: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean acceptBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'Confirmed' WHERE booking_id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, bookingId);
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
        
    } catch (SQLException e) {
        System.out.println("Error accepting booking: " + e.getMessage());
        e.printStackTrace();
    }
    
    return false;
    }

    public boolean completeBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'Completed' WHERE booking_id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, bookingId);
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
        
    } catch (SQLException e) {
        System.out.println("Error completing booking: " + e.getMessage());
        e.printStackTrace();
    }
    
    return false;
    }

    public Object getCleanerUsername() {
        throw new UnsupportedOperationException("Unimplemented method 'getCleanerUsername'");
    }
}