package boundary.PlatformManager;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.PlatformManager.CategoryController;
import entity.UserAdmin.UserAccount;

public class CreateCategoryPage extends JFrame {
    private CategoryController controller;
    private UserAccount userAccount;
    
    private JTextField categoryNameField;
    private JButton submitButton;
    private JButton cancelButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);  
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); 
    private final Color TEXT_COLOR = new Color(44, 62, 80);      
    
    public CreateCategoryPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.controller = new CategoryController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Create Service Category");
        setSize(500, 250); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel headerLabel = new JLabel("Create New Service Category");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
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
        gbc.insets = new Insets(10, 5, 10, 5);
        
        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameLabel, gbc);
        
        categoryNameField = new JTextField();
        categoryNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryNameField.setPreferredSize(new Dimension(300, 30));
        categoryNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(categoryNameField, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());
        
        submitButton = new JButton("Create Category");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(150, 35));
        submitButton.addActionListener(e -> submitCategory());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        return buttonPanel;
    }
    
    public void displayForm() {
        setVisible(true);
    }
    
    private void submitCategory() {
        String categoryName = categoryNameField.getText().trim();
        
        boolean success = controller.processNewCategory(categoryName);
        
        if (success) {
            displaySuccessMessage();
            dispose();  
        } else {
            displayError("Invalid category name or category already exists.");
        }
    }
    
    // Method to display success message
    private void displaySuccessMessage() {
        JOptionPane.showMessageDialog(this,
            "Category created successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Method to display error message
    private void displayError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}