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
import java.util.ArrayList;

public class ViewHistoryPage extends JFrame {
    private BookingDAO bookingDAO;
    private UserAccount userAccount;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    public ViewHistoryPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.bookingDAO = new BookingDAO();
        initializeUI();
        loadHistory();
    }
    
    private void initializeUI() {
        setTitle("Booking History");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("My Booking History");
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
        
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem viewDetailsItem = new JMenuItem("View Booking Details");
        viewDetailsItem.addActionListener(e -> viewBookingDetails());
        
        popupMenu.add(viewDetailsItem);
        
        historyTable.setComponentPopupMenu(popupMenu);
        
        historyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = historyTable.rowAtPoint(point);
                if (row >= 0 && row < historyTable.getRowCount()) {
                    historyTable.setRowSelectionInterval(row, row);
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
        refreshButton.addActionListener(e -> loadHistory());
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadHistory() {
        tableModel.setRowCount(0);
        
        List<BookingService> allBookings = bookingDAO.getBookingsByHomeowner(userAccount.getUsername());
        
        List<BookingService> historyBookings = new ArrayList<>();
        for (BookingService booking : allBookings) {
            if (booking.getStatus().equals("Completed") || booking.getStatus().equals("Cancelled")) {
                historyBookings.add(booking);
            }
        }
        
        // Display the history bookings
        for (BookingService booking : historyBookings) {
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
        
        if (historyBookings.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "You don't have any completed or cancelled bookings in your history yet.",
                "No History",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void viewBookingDetails() {
        int selectedRow = historyTable.getSelectedRow();
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
    
    public void displayHistoryPage() {
        setVisible(true);
    }
}