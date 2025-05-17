package boundary.Homeowner;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import controller.Homeowner.BookingController;
import entity.Cleaner.CleanerAccount;
import entity.Cleaner.CleaningService;
import entity.UserAdmin.UserAccount;
import utility.DateLabelFormatter;  

import java.util.Properties;
import org.jdatepicker.impl.*;

public class BookServicePage extends JFrame {
    private BookingController controller;
    private UserAccount homeownerAccount;
    private CleanerAccount cleanerAccount;
    private CleaningService service;
    
    private JDatePickerImpl datePicker;
    private JComboBox<String> timeComboBox;
    private JTextField addressField;
    private JTextArea notesArea;
    private JLabel totalPriceLabel;
    private JButton bookButton;
    private JButton cancelButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public BookServicePage(UserAccount homeownerAccount, CleanerAccount cleanerAccount, CleaningService service) {
        this.homeownerAccount = homeownerAccount;
        this.cleanerAccount = cleanerAccount;
        this.service = service;
        this.controller = new BookingController();
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Book Service");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Book Cleaning Service");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Service Information",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        infoPanel.add(new JLabel("<html><b>Service:</b> " + service.getTitle() + "</html>"));
        infoPanel.add(new JLabel("<html><b>Cleaner:</b> " + cleanerAccount.getName() + "</html>"));
        infoPanel.add(new JLabel("<html><b>Duration:</b> " + service.getServiceDuration() + " minutes</html>"));
        infoPanel.add(new JLabel("<html><b>Price:</b> $" + String.format("%.2f", service.getPrice()) + "</html>"));
        infoPanel.add(new JLabel("<html><b>Available Days:</b> " + service.getAvailableDays() + "</html>"));
        
        LocalTime start = service.getAvailableStartTime();
        LocalTime end = service.getAvailableEndTime();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        infoPanel.add(new JLabel("<html><b>Available Hours:</b> " + 
            start.format(timeFormatter) + " - " + end.format(timeFormatter) + "</html>"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoPanel, gbc);
        
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(dateLabel, gbc);
        
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        model.setDate(
            calendar.get(java.util.Calendar.YEAR), 
            calendar.get(java.util.Calendar.MONTH), 
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        );
        model.setSelected(true);
        
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(datePicker, gbc);
        
        JLabel timeLabel = new JLabel("Start Time:");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(timeLabel, gbc);
        
        // Generate time slots between available hours
        timeComboBox = new JComboBox<>();
        
        // Create time slots in 1-hour increments within the service's available hours
        LocalTime time = start;
        while (!time.isAfter(end.minusMinutes(service.getServiceDuration()))) {
            timeComboBox.addItem(time.format(timeFormatter));
            time = time.plusHours(1); 
        }
        
        // If no time slots were added (maybe due to service duration), add at least the start time
        if (timeComboBox.getItemCount() == 0) {
            timeComboBox.addItem(start.format(timeFormatter));
        }
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(timeComboBox, gbc);
        
        JLabel addressLabel = new JLabel("Service Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(addressLabel, gbc);
        
        addressField = new JTextField(20);
        if (homeownerAccount.getAddress() != null && !homeownerAccount.getAddress().isEmpty()) {
            addressField.setText(homeownerAccount.getAddress());
        }
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(addressField, gbc);
        
        JLabel notesLabel = new JLabel("Special Instructions:");
        notesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notesLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(notesLabel, gbc);
        
        notesArea = new JTextArea(4, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(scrollPane, gbc);
        
        JLabel priceLabel = new JLabel("Total Price:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(priceLabel, gbc);
        
        totalPriceLabel = new JLabel("$" + String.format("%.2f", service.getPrice()));
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPriceLabel.setForeground(PRIMARY_COLOR);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(totalPriceLabel, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        
        bookButton = new JButton("Book Service");
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setFocusPainted(false);
        bookButton.addActionListener(e -> bookService());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(bookButton);
        
        return buttonPanel;
    }
    
    private void bookService() {
        if (datePicker.getModel().getValue() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a date for the service.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a service address.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            addressField.requestFocus();
            return;
        }
        
        java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
        LocalDate bookingDate = selectedDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        
        if (bookingDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this,
                "Please select a future date for the service.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedTimeStr = (String) timeComboBox.getSelectedItem();
        LocalTime selectedTime = LocalTime.parse(selectedTimeStr, 
            DateTimeFormatter.ofPattern("h:mm a"));
        
        String notes = notesArea.getText().trim();
        
        // Book the service using the controller
        boolean success = controller.bookService(
            homeownerAccount,
            cleanerAccount,
            service,
            bookingDate,
            selectedTime,
            address,
            notes
        );
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Service booked successfully!\n\n" +
                "Cleaner: " + cleanerAccount.getName() + "\n" +
                "Service: " + service.getTitle() + "\n" +
                "Date: " + bookingDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + "\n" +
                "Time: " + selectedTimeStr + "\n" +
                "Total Price: $" + String.format("%.2f", service.getPrice()),
                "Booking Confirmed",
                JOptionPane.INFORMATION_MESSAGE);
                
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "There was an error booking the service. Please try again.",
                "Booking Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}