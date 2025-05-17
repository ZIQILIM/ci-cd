package boundary.Cleaner;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.Homeowner.BookingController;
import entity.BookingDAO;
import entity.Homeowner.BookingService;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class ViewCleanerBookingsPage extends JFrame {
    private BookingDAO bookingDAO;
    private BookingController controller;
    private UserAccount userAccount;
    
    // UI Components
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    public ViewCleanerBookingsPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.bookingDAO = new BookingDAO();
        this.controller = new BookingController();
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
        
        String[] columnNames = {"Booking ID", "Homeowner", "Service", "Date", "Time", "Status", "Price"};
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
        
        JMenuItem acceptBookingItem = new JMenuItem("Accept Booking");
        acceptBookingItem.addActionListener(e -> acceptBooking());
        
        JMenuItem completeBookingItem = new JMenuItem("Mark as Completed");
        completeBookingItem.addActionListener(e -> completeBooking());
        
        popupMenu.add(viewDetailsItem);
        popupMenu.add(acceptBookingItem);
        popupMenu.add(completeBookingItem);
        
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
        
        List<BookingService> bookings = bookingDAO.getBookingsByCleaner(userAccount.getUsername());
        
        for (BookingService booking : bookings) {
            Object[] rowData = {
                booking.getBookingId(),
                booking.getHomeownerUsername(),
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
        addDetailRow(detailsPanel, gbc, "Homeowner:", booking.getHomeownerUsername());
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);
        
        if (booking.getStatus().equals("Pending")) {
            JButton acceptButton = new JButton("Accept Booking");
            acceptButton.setBackground(new Color(52, 152, 219)); 
            acceptButton.setForeground(Color.WHITE); 
            acceptButton.addActionListener(e -> {
                boolean success = controller.acceptBooking(booking.getBookingId());
                if (success) {
                    JOptionPane.showMessageDialog(detailsDialog,
                        "Booking accepted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    detailsDialog.dispose();
                    loadBookings(); 
                } else {
                    JOptionPane.showMessageDialog(detailsDialog,
                        "Failed to accept the booking. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(acceptButton);
        }
        
        if (booking.getStatus().equals("Confirmed")) {
            JButton completeButton = new JButton("Mark as Completed");
            completeButton.setBackground(new Color(46, 204, 113)); 
            completeButton.setForeground(Color.WHITE); 
            completeButton.addActionListener(e -> {
                boolean success = controller.completeBooking(booking.getBookingId());
                if (success) {
                    JOptionPane.showMessageDialog(detailsDialog,
                        "Booking marked as completed successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    detailsDialog.dispose();
                    loadBookings(); 
                } else {
                    JOptionPane.showMessageDialog(detailsDialog,
                        "Failed to complete the booking. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(completeButton);
        }
        
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
    
    private void acceptBooking() {
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
        
        if (!status.equals("Pending")) {
            JOptionPane.showMessageDialog(this,
                "Only pending bookings can be accepted. This booking is: " + status,
                "Invalid Operation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to accept this booking?",
            "Confirm Acceptance",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = controller.acceptBooking(bookingId);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Booking accepted successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            loadBookings(); 
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to accept the booking. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completeBooking() {
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
        
        if (!status.equals("Confirmed")) {
            JOptionPane.showMessageDialog(this,
                "Only confirmed bookings can be marked as completed. This booking is: " + status,
                "Invalid Operation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to mark this booking as completed?",
            "Confirm Completion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = controller.completeBooking(bookingId);
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Booking marked as completed successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            loadBookings(); 
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to complete the booking. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void displayBookingsPage() {
        setVisible(true);
    }
}