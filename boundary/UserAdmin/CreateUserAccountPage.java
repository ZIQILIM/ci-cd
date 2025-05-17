package boundary.UserAdmin;

import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

// Boundary class for user account creation (UserAdmin)
public class CreateUserAccountPage extends JFrame {
    private UserAccountController controller;
    private UserAccount adminAccount;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JButton createButton;
    private JButton cancelButton;

    private final Color PRIMARY_COLOR    = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);

    // Constructor with admin user context
    public CreateUserAccountPage(UserAccount adminAccount) {
        this.adminAccount = adminAccount;
        controller = new UserAccountController();
        initUI();
    }

    // Default constructor
    public CreateUserAccountPage() {
        controller = new UserAccountController();
        initUI();
    }

    private void initUI() {
        setTitle("Create New User Account");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 350); 
        
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Create New User Account", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setForeground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        main.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        form.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        form.add(emailField, gbc);

        main.add(form, BorderLayout.CENTER);
        
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(BACKGROUND_COLOR);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        createButton = new JButton("Create User");
        createButton.addActionListener(e -> createUser());
        
        buttons.add(cancelButton);
        buttons.add(createButton);
        main.add(buttons, BorderLayout.SOUTH);

        setContentPane(main);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(createButton);
    }
  
    private void createUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username, password, name and email are required fields.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String emailRegex = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Create user with empty account type, but with empty address and phone
        boolean success = controller.createUserAccount(username, password, email, name);
        setCursor(Cursor.getDefaultCursor());

        if (success) {
            JOptionPane.showMessageDialog(this,
                "User account created successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new CreateUserProfilePage(username).display();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to create user account. Username or email may already exist.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showInputForm(boolean executed) {
        setVisible(true);
    }

    public void display() {
        setVisible(true);
    }
}