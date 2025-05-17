package boundary.Homeowner;

import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entity.UserAdmin.UserAccount;
import entity.UserDAO;

public class CleanerListPage extends JFrame {
    private UserDAO userDAO;
    private UserAccount homeownerAccount;
    private JTable cleanerTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    
    public CleanerListPage(UserAccount homeownerAccount) {
        this.homeownerAccount = homeownerAccount;
        this.userDAO = new UserDAO();
        initializeUI();
        loadCleaners();
    }
    
    private void initializeUI() {
        setTitle("All Cleaners");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Available Cleaners");
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
        
        String[] columnNames = {"Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cleanerTable = new JTable(tableModel);
        cleanerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cleanerTable.setRowHeight(25);
        cleanerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(cleanerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadCleaners() {
        tableModel.setRowCount(0);
        
        List<UserAccount> allUsers = userDAO.getAllUsers();
        
        for (UserAccount user : allUsers) {
            if (user.getAccountType().equals("Cleaner")) {
                Object[] rowData = {
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    public void displayPage() {
        setVisible(true);
    }
}