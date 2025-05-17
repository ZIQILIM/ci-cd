package boundary.PlatformManager;

import controller.PlatformManager.ReportingController;
import entity.PlatformManager.MonthlyBookingReport;
import entity.UserAdmin.UserAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class viewMonthlyReportPage extends JFrame {
    private final ReportingController controller = new ReportingController();
    private final DefaultTableModel model;
    private final JLabel totalLabel;
    private final JComboBox<String> monthBox;
    private final JComboBox<Integer> yearBox;
    private final JFrame parentDashboard;

    public viewMonthlyReportPage(UserAccount user, JFrame parent) {
        super("Monthly Booking Report");
        this.parentDashboard = parent;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        monthBox = new JComboBox<>(new String[]{
            "01 – Jan","02 – Feb","03 – Mar","04 – Apr",
            "05 – May","06 – Jun","07 – Jul","08 – Aug",
            "09 – Sep","10 – Oct","11 – Nov","12 – Dec"
        });
        yearBox = new JComboBox<>();
        int year = LocalDate.now().getYear();
        for (int y = year - 5; y <= year + 1; y++) yearBox.addItem(y);

        JButton gen = new JButton("Generate Report");
        gen.addActionListener(e -> loadReport());

        top.add(new JLabel("Month:")); top.add(monthBox);
        top.add(new JLabel("Year:"));  top.add(yearBox);
        top.add(gen);
        add(top, BorderLayout.NORTH);

        String[] cols = {
            "Cleaner ID", "Cleaner Username", "Homeowner Username",
            "Service Title", "Booking Date", "Category",
            "Booking Count", "Amount ($)"
        };
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        totalLabel = new JLabel("Total Amount: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadReport());
        JButton back    = new JButton("Back");
        back.addActionListener(e -> {
            dispose();
            parentDashboard.setVisible(true);
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.add(refresh);
        btns.add(back);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(0, 10, 10, 10));
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(btns,      BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        monthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        yearBox.setSelectedItem(year);
        loadReport();

        setVisible(true);
    }

    private void loadReport() {
        int m = monthBox.getSelectedIndex() + 1;
        int y = (Integer) yearBox.getSelectedItem();

        List<MonthlyBookingReport> rows = controller.getReportByMonth(m, y);
        model.setRowCount(0);
        double total = 0;

        for (var r : rows) {
            model.addRow(new Object[]{
                r.getCleanerId(),
                r.getCleanerUsername(),
                r.getHomeownerUsername(),
                r.getServiceTitle(),
                r.getBookingDate(),
                r.getCategory(),
                r.getBookingCount(),
                String.format("%.2f", r.getAmount())
            });
            total += r.getAmount();
        }
        totalLabel.setText(String.format("Total Amount: $%.2f", total));
    }
}
