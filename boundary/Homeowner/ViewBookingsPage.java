package boundary.Homeowner;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entity.BookingDAO;
import entity.Homeowner.BookingService;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class ViewBookingsPage extends JFrame {
    private BookingDAO bookingDAO;
    private UserAccount userAccount;
    
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    public ViewBookingsPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.bookingDAO = new BookingDAO();
        initializeUI();
        loadBookings();
    }
    
    private void initializeUI() {
        setTitle("My Bookings");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("My Bookings");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        
        String[] columnNames = {"Booking ID", "Cleaner", "Service", "Date", "Time", "Status", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setRowHeight(25);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem viewDetailsItem = new JMenuItem("View Booking Details");
        viewDetailsItem.addActionListener(e -> viewBookingDetails());
        
        JMenuItem cancelBookingItem = new JMenuItem("Cancel Booking");
        cancelBookingItem.addActionListener(e -> cancelBooking());
        
        popupMenu.add(viewDetailsItem);
        popupMenu.add(cancelBookingItem);
        
        bookingsTable.setComponentPopupMenu(popupMenu);
        
        bookingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = bookingsTable.rowAtPoint(point);
                if (row >= 0 && row < bookingsTable.getRowCount()) {
                    bookingsTable.setRowSelectionInterval(row, row);
                }
            }
        });
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadBookings());
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadBookings() {
        tableModel.setRowCount(0);
        
        List<BookingService> allBookings = bookingDAO.getBookingsByHomeowner(userAccount.getUsername());
        
        for (BookingService booking : allBookings) {
            if (booking.getStatus().equals("Completed") || booking.getStatus().equals("Cancelled")) {
                continue;
            }
            
            Object[] rowData = {
                booking.getBookingId(),
                booking.getCleanerUsername(),
                "Service ID: " + booking.getServiceId(),
                booking.getBookingDate().format(DATE_FORMATTER),
                booking.getStartTime().format(TIME_FORMATTER) + " - " + 
                    booking.getEndTime().format(TIME_FORMATTER),
                booking.getStatus(),
                "$" + String.format("%.2f", booking.getTotalPrice())
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void viewBookingDetails() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        BookingService booking = bookingDAO.getBookingById(bookingId);
        
        if (booking == null) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving booking details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog detailsDialog = new JDialog(this, "Booking Details", true);
        detailsDialog.setSize(400, 400);
        detailsDialog.setLayout(new BorderLayout());
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        addDetailRow(detailsPanel, gbc, "Booking ID:", String.valueOf(booking.getBookingId()));
        addDetailRow(detailsPanel, gbc, "Cleaner:", booking.getCleanerUsername());
        addDetailRow(detailsPanel, gbc, "Service ID:", String.valueOf(booking.getServiceId()));
        addDetailRow(detailsPanel, gbc, "Date:", booking.getBookingDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        addDetailRow(detailsPanel, gbc, "Time:", booking.getStartTime().format(TIME_FORMATTER) + " - " + 
                                          booking.getEndTime().format(TIME_FORMATTER));
        addDetailRow(detailsPanel, gbc, "Status:", booking.getStatus());
        addDetailRow(detailsPanel, gbc, "Address:", booking.getAddress());
        
        if (booking.getNotes() != null && !booking.getNotes().isEmpty()) {
            addDetailRow(detailsPanel, gbc, "Notes:", booking.getNotes());
        }
        
        addDetailRow(detailsPanel, gbc, "Total Price:", "$" + String.format("%.2f", booking.getTotalPrice()));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(closeButton);
        
        detailsDialog.add(detailsPanel, BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        panel.add(labelComponent, gbc);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        panel.add(valueComponent, gbc);
        
        gbc.gridy++;
    }
    
    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        
        // Check if the booking is already cancelled or completed
        if (status.equals("Cancelled") || status.equals("Completed")) {
            JOptionPane.showMessageDialog(this,
                "This booking cannot be cancelled because its status is: " + status,
                "Cannot Cancel",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?\nThis action cannot be undone.",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = bookingDAO.cancelBooking(bookingId);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Booking cancelled successfully.",
                "Cancellation Confirmed",
                JOptionPane.INFORMATION_MESSAGE);
            loadBookings(); 
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to cancel the booking. Please try again.",
                "Cancellation Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Accessor methods for subclasses
    protected DefaultTableModel getTableModel() {
        return tableModel;
    }
    
    protected List<BookingService> getBookings() {
        return bookingDAO.getBookingsByHomeowner(userAccount.getUsername());
    }
    
    public void displayBookingsPage() {
        setVisible(true);
    }
}