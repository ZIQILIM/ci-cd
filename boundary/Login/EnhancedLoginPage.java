package boundary.Login;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import boundary.Cleaner.CleanerPage;
import boundary.Homeowner.HomeownerPage;
import boundary.PlatformManager.PlatformManagerPage;
import boundary.UserAdmin.UserAdminPage;
import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

public class EnhancedLoginPage extends JFrame {
    private UserAccountController controller;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> profileComboBox;
    private JButton loginButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public EnhancedLoginPage() {
        controller = new UserAccountController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Cleaning Service Platform");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        setLocationRelativeTo(null);
        
        getRootPane().setDefaultButton(loginButton);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Cleaning Service Platform");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_COLOR);
        
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = createSimpleIcon();
        iconLabel.setIcon(icon);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(BACKGROUND_COLOR);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(iconLabel, BorderLayout.NORTH);
        headerPanel.add(textPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private ImageIcon createSimpleIcon() {
        int width = 80;
        int height = 80;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(0, 0, width, height);
        
        g2d.setColor(Color.WHITE);
        int[] xPoints = {width/2, width/4, width/4, width*3/4, width*3/4};
        int[] yPoints = {height/4, height/2, height*3/4, height*3/4, height/2};
        g2d.fillPolygon(xPoints, yPoints, 5);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(width*5/8, height/3, width/16, height/2);
        g2d.fillOval(width*5/8 - width/16, height/3 - height/16, width/8, height/8);
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);
        
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        formPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        
        JLabel profileLabel = new JLabel("Profile");
        profileLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.2;
        formPanel.add(profileLabel, gbc);
        
        String[] profiles = {"User Admin", "Cleaner", "Homeowner", "Platform Manager"};
        profileComboBox = new JComboBox<>(profiles);
        profileComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        profileComboBox.setBackground(Color.WHITE);
        profileComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        formPanel.add(profileComboBox, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(TEXT_COLOR);
        loginButton.setBackground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(BACKGROUND_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(Color.WHITE);
            }
        });
        
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        buttonPanel.add(loginButton);
        
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_COLOR);
        container.add(buttonPanel, BorderLayout.CENTER);
        
        return container;
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String profile = (String) profileComboBox.getSelectedItem();
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);
        
        SwingWorker<UserAccount, Void> worker = new SwingWorker<UserAccount, Void>() {
            @Override
            protected UserAccount doInBackground() throws Exception {
                Thread.sleep(500);
                return controller.validateLogin(username, password, profile);
            }
            
            @Override
            protected void done() {
                try {
                    UserAccount account = get();
                    if (account != null) {
                        JOptionPane.showMessageDialog(EnhancedLoginPage.this,
                            "Login successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        String accountType = account.getAccountType();
                        if (accountType.equals("User Admin")) {
                            switchToUserAdminPage(account);
                        } else if (accountType.equals("Cleaner")) {
                            switchToCleanerPage(account);
                        } else if (accountType.equals("Homeowner")) {
                            switchToHomeownerPage(account);
                        } else if (accountType.equals("Platform Manager")) {
                            switchToPlatformManagerPage(account);
                        }
                        
                    } else {
                        JOptionPane.showMessageDialog(EnhancedLoginPage.this,
                            "Invalid Credentials",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    loginButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    public void switchToUserAdminPage(UserAccount userAccount) {
        dispose();
        UserAdminPage adminPage = new UserAdminPage(userAccount);
        adminPage.setVisible(true);
    }
    
    public void switchToCleanerPage(UserAccount userAccount) {
        dispose();
        CleanerPage cleanerPage = new CleanerPage(userAccount);
        cleanerPage.setVisible(true);
    }
    
    public void switchToHomeownerPage(UserAccount userAccount) {
        dispose();
        HomeownerPage homeownerPage = new HomeownerPage(userAccount);
        homeownerPage.setVisible(true);
    }
    
    public void switchToPlatformManagerPage(UserAccount userAccount) {
        dispose();
        PlatformManagerPage managerPage = new PlatformManagerPage(userAccount);
        managerPage.setVisible(true);
    }
    
    public void displayLoginPage() {
        setVisible(true);
    }
}