package boundary.Cleaner;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultFormatter;

import controller.Cleaner.CleaningServiceController;
import entity.UserAdmin.UserAccount;
import entity.PlatformManager.ServiceCategory;

public class CreateCleaningServicePage extends JFrame {
    private CleaningServiceController controller;
    private UserAccount cleanerAccount;
    
    private JTextField serviceNameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JSpinner durationSpinner;
    private JPanel dayCheckboxPanel;
    private JCheckBox[] dayCheckboxes;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JButton submitButton;
    private JButton cancelButton;
    
    private JPanel categoriesPanel;
    private List<JCheckBox> categoryCheckboxes;
    private List<ServiceCategory> availableCategories;
    
    private final String[] DAYS_OF_WEEK = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); 
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); 
    private final Color TEXT_COLOR = new Color(44, 62, 80);       
    
    public CreateCleaningServicePage(UserAccount cleanerAccount) {
        this.cleanerAccount = cleanerAccount;
        this.controller = new CleaningServiceController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Create New Cleaning Service");
        setSize(700, 750); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Create a New Cleaning Service");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
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
        gbc.insets = new Insets(10, 5, 10, 5);
        
        JLabel nameLabel = new JLabel("Service Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);
        
        serviceNameField = new JTextField(20);
        serviceNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(serviceNameField, gbc);
        
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descriptionLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(descriptionLabel, gbc);
        
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(scrollPane, gbc);
        
        JLabel priceLabel = new JLabel("Price ($):");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(priceLabel, gbc);
        
        priceField = new JTextField(10);
        priceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(priceField, gbc);
        
        JLabel durationLabel = new JLabel("Duration (minutes):");
        durationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        durationLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(durationLabel, gbc);
        
        SpinnerNumberModel durationModel = new SpinnerNumberModel(60, 30, 480, 30);
        durationSpinner = new JSpinner(durationModel);
        JSpinner.NumberEditor durationEditor = new JSpinner.NumberEditor(durationSpinner, "#");
        durationSpinner.setEditor(durationEditor);
        ((DefaultFormatter) durationEditor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(durationSpinner, gbc);
        
        JLabel daysLabel = new JLabel("Available Days:");
        daysLabel.setFont(new Font("Arial", Font.BOLD, 14));
        daysLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(daysLabel, gbc);
        
        dayCheckboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dayCheckboxPanel.setBackground(BACKGROUND_COLOR);
        dayCheckboxes = new JCheckBox[DAYS_OF_WEEK.length];
        
        for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
            dayCheckboxes[i] = new JCheckBox(DAYS_OF_WEEK[i]);
            dayCheckboxes[i].setSelected(i < 5); 
            dayCheckboxes[i].setBackground(BACKGROUND_COLOR);
            dayCheckboxPanel.add(dayCheckboxes[i]);
        }
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(dayCheckboxPanel, gbc);
        
        JLabel timeLabel = new JLabel("Available Hours:");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(timeLabel, gbc);
        
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.setBackground(BACKGROUND_COLOR);
        
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startTimeModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        startTimeSpinner.setValue(java.util.Date.from(java.time.LocalTime.of(9, 0).atDate(java.time.LocalDate.now()).atZone(java.time.ZoneId.systemDefault()).toInstant()));
        
        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endTimeModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        endTimeSpinner.setValue(java.util.Date.from(java.time.LocalTime.of(17, 0).atDate(java.time.LocalDate.now()).atZone(java.time.ZoneId.systemDefault()).toInstant()));
        
        timePanel.add(new JLabel("From: "));
        timePanel.add(startTimeSpinner);
        timePanel.add(new JLabel("  To: "));
        timePanel.add(endTimeSpinner);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(timePanel, gbc);
        
        JLabel categoriesLabel = new JLabel("Categories:");
        categoriesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        categoriesLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        formPanel.add(categoriesLabel, gbc);
        
        categoriesPanel = new JPanel(new GridLayout(0, 2)); 
        categoriesPanel.setBackground(BACKGROUND_COLOR);
        
        // Load categories and create checkboxes
        loadCategories();
        
        JScrollPane categoriesScrollPane = new JScrollPane(categoriesPanel);
        categoriesScrollPane.setPreferredSize(new Dimension(400, 150));
        categoriesScrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(categoriesScrollPane, gbc);
        
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
        
        submitButton = new JButton("Post Service");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submitService());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        return buttonPanel;
    }
    
    // Load categories from database
    private void loadCategories() {
        categoryCheckboxes = new ArrayList<>();
        categoriesPanel.removeAll();
        
        // Get all categories from the database
        availableCategories = ServiceCategory.getAllCategories();
        
        if (availableCategories.isEmpty()) {
            JLabel noCategories = new JLabel("No categories available. Contact a Platform Manager.");
            noCategories.setForeground(Color.RED);
            categoriesPanel.add(noCategories);
        } else {
            for (ServiceCategory category : availableCategories) {
                JCheckBox checkBox = new JCheckBox(category.getName());
                checkBox.setBackground(BACKGROUND_COLOR);
                categoryCheckboxes.add(checkBox);
                categoriesPanel.add(checkBox);
            }
        }
        
        categoriesPanel.revalidate();
        categoriesPanel.repaint();
    }
    
    private void submitService() {
        String serviceName = serviceNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceText = priceField.getText().trim();
        
        if (serviceName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Service name is required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            serviceNameField.requestFocus();
            return;
        }
        
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Description is required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            descriptionArea.requestFocus();
            return;
        }
        
        float price;
        try {
            price = Float.parseFloat(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Price must be greater than zero.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid price.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return;
        }
        
        int duration = (Integer) durationSpinner.getValue();
        
        StringBuilder availableDays = new StringBuilder();
        for (int i = 0; i < dayCheckboxes.length; i++) {
            if (dayCheckboxes[i].isSelected()) {
                if (availableDays.length() > 0) {
                    availableDays.append(",");
                }
                availableDays.append(DAYS_OF_WEEK[i]);
            }
        }
        
        if (availableDays.length() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one available day.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.util.Date startDate = (java.util.Date) startTimeSpinner.getValue();
        java.util.Date endDate = (java.util.Date) endTimeSpinner.getValue();
        
        LocalTime startTime = startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
        
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            JOptionPane.showMessageDialog(this,
                "End time must be after start time.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Collect selected categories
        List<Integer> selectedCategoryIds = new ArrayList<>();
        for (int i = 0; i < categoryCheckboxes.size(); i++) {
            if (categoryCheckboxes.get(i).isSelected()) {
                selectedCategoryIds.add(availableCategories.get(i).getCategoryId());
            }
        }
        
        // Create the service with categories
        boolean success = controller.createServiceWithCategories(
            serviceName, 
            description, 
            price, 
            cleanerAccount.getUsername(),
            duration,
            availableDays.toString(),
            startTime,
            endTime,
            selectedCategoryIds
        );
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Your cleaning service has been created successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error creating service. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}