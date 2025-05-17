package boundary.UserAdmin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import entity.UserAdmin.UserAccount;

 // Boundary class for viewing user profile
public class UserProfileViewPage extends JFrame {
    private UserAccount userAccount;
    
    private JTextField usernameField;
    private JTextField accountTypeField;
    private JTextField nameField;
    private JTextField emailField;
    private JButton closeButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); 
    private final Color TEXT_COLOR = new Color(44, 62, 80);      
    

    public UserProfileViewPage(UserAccount userAccount) {  // Constructor takes a user account to display
        this.userAccount = userAccount;  
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("User Profile");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("User Profile");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel profilePanel = createProfilePanel();
        mainPanel.add(profilePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        
        displayUserData();
    }
    
    // Create profile panel with user data fields
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        profilePanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setEditable(false);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        profilePanel.add(usernameField, gbc);
        
        JLabel accountTypeLabel = new JLabel("Account Type:");
        accountTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        accountTypeLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        profilePanel.add(accountTypeLabel, gbc);
        
        accountTypeField = new JTextField(20);
        accountTypeField.setEditable(false);
        accountTypeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        profilePanel.add(accountTypeField, gbc);
        
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        profilePanel.add(nameLabel, gbc);
        
        nameField = new JTextField(20);
        nameField.setEditable(false);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        profilePanel.add(nameField, gbc);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        profilePanel.add(emailLabel, gbc);
        
        emailField = new JTextField(20);
        emailField.setEditable(false);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        profilePanel.add(emailField, gbc);
        
        return profilePanel;
    }
    
    // Create button panel with close button
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); 
            }
        });
        
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    private void displayUserData() {
        usernameField.setText(userAccount.getUsername());
        accountTypeField.setText(userAccount.getAccountType());
        nameField.setText(userAccount.getName());
        emailField.setText(userAccount.getEmail());
    }
    
     // Display the profile view page
    public void displayProfilePage() {
        setVisible(true);
    }
}