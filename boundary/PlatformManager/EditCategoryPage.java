package boundary.PlatformManager;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.PlatformManager.CategoryController;
import entity.PlatformManager.ServiceCategory;

public class EditCategoryPage extends JDialog {
    private CategoryController controller;
    private ServiceCategory category;
    
    private JTextField categoryNameField;
    private JButton submitButton;
    private JButton cancelButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);  
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); 
    private final Color TEXT_COLOR = new Color(44, 62, 80);       
    
    public EditCategoryPage(JFrame parent, ServiceCategory category) {
        super(parent, "Edit Category", true);
        this.category = category;
        this.controller = new CategoryController();
        initializeUI();
    }
    
    private void initializeUI() {
        setSize(400, 200);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Edit Category");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(getParent());
        
        categoryNameField.setText(category.getName());
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameLabel, gbc);
        
        categoryNameField = new JTextField();
        categoryNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryNameField.setPreferredSize(new Dimension(250, 30));
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
        cancelButton.addActionListener(e -> dispose());
        
        submitButton = new JButton("Update Category");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submitUpdatedCategory());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        return buttonPanel;
    }
    
    private void submitUpdatedCategory() {
        String newName = categoryNameField.getText().trim();
        
        if (newName.equals(category.getName())) {
            dispose();
            return;
        }
        
        boolean success = controller.updateCategoryName(category.getCategoryId(), newName);
        
        if (success) {
            showSuccessMessage();
            dispose();  
        } else {
            showError("Invalid or duplicate name");
        }
    }
    
    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(this,
            "Category updated successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public void displayEditForm() {
        categoryNameField.setText(category.getName());
        setVisible(true);
    }
}