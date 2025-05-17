package boundary.PlatformManager;

import controller.PlatformManager.ReportingController;
import entity.PlatformManager.DailyBookingReport;
import entity.UserAdmin.UserAccount;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Calendar;

public class viewDailyReportPage extends JFrame {
    private final ReportingController controller = new ReportingController();
    private DefaultTableModel model = new DefaultTableModel();
    private final JLabel               totalLabel;
    private final UserAccount          userAccount;
    private final JDatePickerImpl      datePicker;
    private final JFrame parentDashboard; 

    public viewDailyReportPage(UserAccount userAccount, JFrame parent) {
        super("Completed Bookings Report");
        this.userAccount = userAccount;
        this.parentDashboard = parent;    

        setSize(800, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout(10,10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Select Date:"));

        UtilDateModel dateModel = new UtilDateModel();
        dateModel.setValue(new Date());  // default to today
        Properties props = new Properties();
        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, props);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        topPanel.add(datePicker);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            loadReport(getSelectedDate());
        });
        topPanel.add(searchBtn);

        getContentPane().add(topPanel, BorderLayout.NORTH);

        String[] cols = {
            "Cleaner ID",
            "Cleaner Username",
            "Homeowner Username",  
            "Service Title",       
            "Booking Date",
            "Category",
            "Booking Count",
            "Amount ($)"
        };
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        totalLabel = new JLabel("Total Amount: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            loadReport(getSelectedDate());
        });

        JButton back    = new JButton("Back to Dashboard");
        back.addActionListener(e -> {
            dispose();
            parentDashboard.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(refreshBtn);
        buttonPanel.add(back);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        // initial load
        loadReport(getSelectedDate());
    }

    private LocalDate getSelectedDate() {
        Date sel = (Date) datePicker.getModel().getValue();
        return sel.toInstant()
                  .atZone(ZoneId.systemDefault())
                  .toLocalDate();
    }

    private void loadReport(LocalDate date) {
        List<DailyBookingReport> rows = controller.getReportByDate(date);
        double grandTotal = 0.0;
        for (DailyBookingReport r : rows) {
            model.addRow(new Object[]{
                r.getCleanerId(),
                r.getCleanerUsername(),
                r.getHomeownerUsername(), 
                r.getServiceTitle(),       
                r.getBookingDate().toString(),
                r.getServiceCategory(),
                r.getBookingCount(),
                String.format("%.2f", r.getTotalEarned())
            });
            grandTotal += r.getTotalEarned();
        }
        totalLabel.setText(String.format("Total Amount: $%.2f", grandTotal));
    }

    private static class DateLabelFormatter extends AbstractFormatter {
        private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws ParseException {
            return df.parse(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value instanceof Calendar) {
                return df.format(((Calendar) value).getTime());
            }
            return "";
        }
    }

    public void displayDailyReportPage() {
        setVisible(true);
    }
}
