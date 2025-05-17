package boundary.Homeowner;

import controller.Homeowner.SearchServiceHistoryController;
import entity.Homeowner.BookingService;
import entity.UserAdmin.UserAccount;
import org.jdatepicker.impl.UtilDateModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import utility.DateLabelFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SearchServiceHistoryPage extends JFrame {
    private final SearchServiceHistoryController controller;
    private final UserAccount homeownerAccount;

    private JComboBox<ServiceItem> serviceComboBox;
    private JDatePickerImpl fromDatePicker;
    private JDatePickerImpl toDatePicker;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton backButton;

    private static final Color PRIMARY_COLOR    = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color TEXT_COLOR       = new Color(44, 62, 80);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public SearchServiceHistoryPage(UserAccount homeownerAccount) {
        this.homeownerAccount = homeownerAccount;
        this.controller       = new SearchServiceHistoryController();
        initializeUI();
        setDefaultDateRange();
        loadServices();
        performSearch();
    }

    private void initializeUI() {
        setTitle("Service History");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Service History");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(PRIMARY_COLOR);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBorder(new EmptyBorder(0,0,20,0));
        main.add(header, BorderLayout.NORTH);

        main.add(createFilterPanel(), BorderLayout.CENTER);
        main.add(createResultsPanel(), BorderLayout.SOUTH);

        setContentPane(main);
        setLocationRelativeTo(null);
    }

    private JPanel createFilterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Search Filters",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel svcLbl = new JLabel("Service:");
        svcLbl.setFont(new Font("Arial", Font.BOLD, 14));
        svcLbl.setForeground(TEXT_COLOR);
        p.add(svcLbl, gbc);

        serviceComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        p.add(serviceComboBox, gbc);

        gbc.gridwidth = 1;
        JLabel fromLbl = new JLabel("From Date:");
        fromLbl.setFont(new Font("Arial", Font.BOLD, 14));
        fromLbl.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 1;
        p.add(fromLbl, gbc);

        UtilDateModel fromModel = new UtilDateModel();
        fromDatePicker = new JDatePickerImpl(
            new JDatePanelImpl(fromModel, new java.util.Properties()),
            new DateLabelFormatter());
        fromDatePicker.setPreferredSize(new Dimension(180, 35));
        gbc.gridx = 1; gbc.gridy = 1;
        p.add(fromDatePicker, gbc);

        JLabel toLbl = new JLabel("To Date:");
        toLbl.setFont(new Font("Arial", Font.BOLD, 14));
        toLbl.setForeground(TEXT_COLOR);
        gbc.gridx = 2; gbc.gridy = 1;
        p.add(toLbl, gbc);

        UtilDateModel toModel = new UtilDateModel();
        toDatePicker = new JDatePickerImpl(
            new JDatePanelImpl(toModel, new java.util.Properties()),
            new DateLabelFormatter());
        toDatePicker.setPreferredSize(new Dimension(180, 35));
        gbc.gridx = 3; gbc.gridy = 1;
        p.add(toDatePicker, gbc);

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> performSearch());
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        p.add(searchButton, gbc);

        return p;
    }

    private JPanel createResultsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Results",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));

        String[] cols = {"Booking ID","Date","Time","Service","Cleaner","Address","Price"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(25);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(resultsTable);
        scroll.setPreferredSize(new Dimension(850,300));
        p.add(scroll, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.setBackground(BACKGROUND_COLOR);
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        btnP.setBorder(new EmptyBorder(10,0,0,0));
        btnP.add(backButton);
        p.add(btnP, BorderLayout.SOUTH);

        return p;
    }

    private void setDefaultDateRange() {
        Calendar cal = Calendar.getInstance();
        UtilDateModel toM   = (UtilDateModel) toDatePicker.getModel();
        UtilDateModel fromM = (UtilDateModel) fromDatePicker.getModel();

        toM.setValue(cal.getTime());
        toM.setSelected(true);

        cal.add(Calendar.MONTH, -1);
        fromM.setValue(cal.getTime());
        fromM.setSelected(true);
    }

    private void loadServices() {
    serviceComboBox.removeAllItems();
    serviceComboBox.addItem(new ServiceItem(0, "All Services"));

    // Ask the controller for distinct service IDs
    List<Integer> ids = controller.getHomeownerServiceIds(
        homeownerAccount.getUsername()
    );

    // For each ID, look up its title and add it
    for (int sid : ids) {
        String title = controller.getServiceName(sid);
        serviceComboBox.addItem(new ServiceItem(sid, title));
    }
}

    private void performSearch() {
    // Build the service filter list
    int selectedId = ((ServiceItem)serviceComboBox.getSelectedItem()).getId();
    List<Integer> serviceFilter = selectedId == 0
        ? null
        : Collections.singletonList(selectedId);

     Date fromDate = (Date) fromDatePicker.getModel().getValue();
    Date toDate   = (Date) toDatePicker.getModel().getValue();

    if (fromDate == null || toDate == null) {
        JOptionPane.showMessageDialog(this,
            "Please select both From and To dates.",
            "Missing Date Range",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    LocalDate fromLocal = Instant.ofEpochMilli(fromDate.getTime())
                                 .atZone(ZoneId.systemDefault())
                                 .toLocalDate();
    LocalDate toLocal   = Instant.ofEpochMilli(toDate.getTime())
                                 .atZone(ZoneId.systemDefault())
                                 .toLocalDate();

    // ← NEW: reject if start > end
    if (fromLocal.isAfter(toLocal)) {
        JOptionPane.showMessageDialog(this,
            "Please select a valid date range.",
            "Invalid Date Range",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 3) Proceed with your search…
    List<BookingService> results = controller.getServiceHistory(
        homeownerAccount.getUsername(),
        serviceFilter,
        fromLocal,
        toLocal
    );

    // 4) Clear & populate the table
    tableModel.setRowCount(0);
    for (BookingService b : results) {
        tableModel.addRow(new Object[]{
            b.getBookingId(),
            b.getBookingDate().format(DATE_FORMATTER),
            b.getStartTime().format(TIME_FORMATTER) + " – " +
              b.getEndTime().format(TIME_FORMATTER),
            b.getTitle(),
            b.getCleanerUsername(),
            b.getAddress(),
            String.format("$%.2f", b.getTotalPrice())
        });
    }
}

    private LocalDate toLocalDate(JDatePickerImpl picker) {
        Date d = (Date) picker.getModel().getValue();
        return Instant.ofEpochMilli(d.getTime())
                      .atZone(ZoneId.systemDefault())
                      .toLocalDate();
    }

    private class ServiceItem {
        private final int id;
        private final String title;
        ServiceItem(int id, String title){this.id=id;this.title=title;}
        public int getId(){return id;}
        public String toString(){return title;}
    }

    public void showServiceHistory() {
        setVisible(true);
        SwingUtilities.invokeLater(this::performSearch);
    }
}
