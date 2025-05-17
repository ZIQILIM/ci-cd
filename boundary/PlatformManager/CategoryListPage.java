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

public class CategoryListPage extends JFrame {
    private CategoryController controller;
    private UserAccount userAccount;
    
    private JTable categoriesTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    public CategoryListPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.controller = new CategoryController();
        initializeUI();
        displayCategories(controller.fetchCategories());
    }
    
    private void initializeUI() {
        setTitle("Service Categories");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Service Categories", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
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
        
        String[] columnNames = {"ID", "Category Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        categoriesTable = new JTable(tableModel);
        categoriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoriesTable.setRowHeight(25);
        categoriesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        categoriesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID column
        categoriesTable.getColumnModel().getColumn(1).setPreferredWidth(550); // Name column
        
        JScrollPane scrollPane = new JScrollPane(categoriesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Category");
        deleteItem.addActionListener(e -> confirmDeletion()); 
        
        popupMenu.add(deleteItem);
        categoriesTable.setComponentPopupMenu(popupMenu);
        
        categoriesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = categoriesTable.rowAtPoint(point);
                if (row >= 0 && row < categoriesTable.getRowCount()) {
                    categoriesTable.setRowSelectionInterval(row, row);
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
        refreshButton.addActionListener(e -> refreshCategories());
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    // Display the list of categories
    public void displayCategories(List<ServiceCategory> categories) {
        tableModel.setRowCount(0);
        
        if (categories.isEmpty()) {
            showEmptyMessage();
        } else {
            for (ServiceCategory category : categories) {
                Object[] rowData = {
                    category.getCategoryId(),
                    category.getName()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    // Show message when no categories are found
    public void showEmptyMessage() {
        JOptionPane.showMessageDialog(this,
            "No categories found in the system.",
            "No Categories",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Refresh the categories list
    private void refreshCategories() {
        displayCategories(controller.fetchCategories());
    }
    
    // Confirm deletion of selected category 
    private void confirmDeletion() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
            String categoryName = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the category \"" + categoryName + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirmation == JOptionPane.YES_OPTION) {
                // Check if category can be deleted
                boolean isDeletable = controller.checkIfDeletable(categoryId);
                
                if (isDeletable) {
                    // Delete the category
                    boolean success = controller.deleteCategory(categoryId);
                    
                    if (success) {
                        showSuccessMessage();
                        refreshCategories();
                    } else {
                        showError("Failed to delete category.");
                    }
                } else {
                    showError("Category in use: This category is linked to one or more services and cannot be deleted.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a category to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(this,
            "Category deleted successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public void displayCategoryPage() {
        setVisible(true);
    }
}