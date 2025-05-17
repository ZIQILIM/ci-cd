package boundary.PlatformManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.PlatformManager.CategoryController;
import entity.PlatformManager.ServiceCategory;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class CategorySearchPage extends JFrame {
    private CategoryController controller;
    private UserAccount userAccount;
    
    private JTextField keywordField;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public CategorySearchPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.controller = new CategoryController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Search Service Categories");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Search Service Categories");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel keywordLabel = new JLabel("Enter Keyword:");
        keywordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        keywordLabel.setForeground(TEXT_COLOR);
        
        keywordField = new JTextField(20);
        keywordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        
        keywordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> performSearch());
        
        searchPanel.add(keywordLabel);
        searchPanel.add(keywordField);
        searchPanel.add(searchButton);
        
        return searchPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(BACKGROUND_COLOR);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Search Results",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        String[] columnNames = {"ID", "Category Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setRowHeight(25);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        scrollPane.setPreferredSize(new Dimension(550, 200));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(backButton);
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return resultsPanel;
    }
    
    private void performSearch() {
        String keyword = keywordField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a keyword to search.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableModel.setRowCount(0);
        
        List<ServiceCategory> results = controller.searchCategories(keyword);
    
        if (results.isEmpty()) {
            displayNoResultsMessage();
        } else {
            displaySearchResults(results);
        }
    }
    
    private void displaySearchResults(List<ServiceCategory> results) {
        for (ServiceCategory category : results) {
            Object[] rowData = {
                category.getCategoryId(),
                category.getName()
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void displayNoResultsMessage() {
        JOptionPane.showMessageDialog(this,
            "No categories found matching your keyword.",
            "No Results",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void displaySearchPage() {
        setVisible(true);
    }
}