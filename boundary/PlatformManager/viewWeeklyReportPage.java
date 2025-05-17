package boundary.PlatformManager;

import controller.PlatformManager.ReportingController;
import entity.PlatformManager.DailyBookingReport;
import entity.UserAdmin.UserAccount;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jdatepicker.impl.DateComponentFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class viewWeeklyReportPage extends JFrame {
    private final UserAccount userAccount;
    private final JFrame parentDashboard;
    private final DefaultTableModel model;
    private final JLabel totalLabel;
    private final JLabel dateRangeLabel;           
    private final JDatePickerImpl datePicker;      

    public viewWeeklyReportPage(UserAccount user, JFrame parent) {
        super("Weekly Booking Report");
        this.userAccount     = user;
        this.parentDashboard = parent;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        UtilDateModel modelDate = new UtilDateModel();
        modelDate.setValue(Date.from(LocalDate.now()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()));
        Properties p = new Properties();
        JDatePanelImpl datePanel = new JDatePanelImpl(modelDate, p);
        datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());

        JButton gen = new JButton("Generate Report");
        gen.addActionListener(e -> reloadData());

        top.add(new JLabel("Start Date:"));
        top.add(datePicker);
        top.add(gen);
        add(top, BorderLayout.NORTH);

        String[] cols = { "Date", "Cleaner", "Homeowner", "Service", "Category", "Count", "Earned" };
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        dateRangeLabel = new JLabel(" ");  
        dateRangeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        totalLabel = new JLabel("Total Amount: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> reloadData());

        JButton back = new JButton("Back to Dashboard");
        back.addActionListener(e -> {
            dispose();
            parentDashboard.setVisible(true);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.add(refresh);
        btnPanel.add(back);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBorder(new EmptyBorder(8, 10, 10, 10));
        south.add(dateRangeLabel);
        JPanel totalAndButtons = new JPanel(new BorderLayout());
        totalAndButtons.add(totalLabel, BorderLayout.WEST);
        totalAndButtons.add(btnPanel, BorderLayout.EAST);
        south.add(totalAndButtons);

        add(south, BorderLayout.SOUTH);

        reloadData();
        setVisible(true);
    }

    private void reloadData() {
        Date picked = (Date) datePicker.getModel().getValue();
        LocalDate start = picked.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end   = start.plusDays(6);

        dateRangeLabel.setText(
            String.format("Showing results for week: %s to %s", start, end)
        );

        List<DailyBookingReport> rows =
            new ReportingController().getReportByDateRange(start, end);

        model.setRowCount(0);
        double total = 0;
        for (DailyBookingReport r : rows) {
            model.addRow(new Object[]{
                r.getBookingDate(),
                r.getCleanerUsername(),
                r.getHomeownerUsername(),
                r.getServiceTitle(),
                r.getServiceCategory(),
                r.getBookingCount(),
                String.format("%.2f", r.getTotalEarned())
            });
            total += r.getTotalEarned();
        }
        totalLabel.setText(String.format("Total Amount: $%.2f", total));
    }
}
