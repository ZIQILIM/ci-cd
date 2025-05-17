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

public class CategoryManagementPage extends JFrame {
    private CategoryController controller;
    private UserAccount userAccount;
    
    private JTable categoriesTable;
    private DefaultTableModel tableModel;
    private JButton createButton;
    private JButton refreshButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);  // Blue
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light gray
    
    public CategoryManagementPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.controller = new CategoryController();
        initializeUI();
        loadCategories();
    }
    
    private void initializeUI() {
        setTitle("Service Category Management");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Service Category Management");
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
        
        JMenuItem editItem = new JMenuItem("Edit Category");
        editItem.addActionListener(e -> editSelectedCategory());
        
        JMenuItem deleteItem = new JMenuItem("Delete Category");
        deleteItem.addActionListener(e -> deleteSelectedCategory());
        
        popupMenu.add(editItem);
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
        refreshButton.addActionListener(e -> loadCategories());
        
        createButton = new JButton("Create New Category");
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setFocusPainted(false);
        createButton.addActionListener(e -> openCreateCategoryPage());
        
        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(createButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadCategories() {
        tableModel.setRowCount(0); 
        
        List<ServiceCategory> categories = controller.fetchCategories();
        
        for (ServiceCategory category : categories) {
            Object[] rowData = {
                category.getCategoryId(),
                category.getName()
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void openCreateCategoryPage() {
        CreateCategoryPage categoryPage = new CreateCategoryPage(userAccount);
        categoryPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadCategories(); 
            }
        });
        categoryPage.displayForm();
    }
    
    private void editSelectedCategory() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
            String categoryName = (String) tableModel.getValueAt(selectedRow, 1);
            
            ServiceCategory selectedCategory = new ServiceCategory(categoryId, categoryName);
            
            EditCategoryPage editPage = new EditCategoryPage(this, selectedCategory);
            editPage.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCategories(); 
                }
            });
            editPage.displayEditForm();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a category to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deleteSelectedCategory() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
            String categoryName = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the category \"" + categoryName + "\"?\n" +
                "This may affect services that use this category.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.deleteCategory(categoryId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Category successfully deleted.",
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadCategories();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete category. It may be in use by existing services.",
                        "Delete Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a category to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
}