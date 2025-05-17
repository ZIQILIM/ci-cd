package controller.PlatformManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import entity.PlatformManager.DailyBookingReport;
import entity.PlatformManager.MonthlyBookingReport;

public class ReportingController {

    // Fetches the completed‐bookings report for a single date.
    public List<DailyBookingReport> getReportByDate(LocalDate date) {
        return DailyBookingReport.fetchByDate(date);
    }

    // Fetches the completed‐bookings report for each date in [start, end]
    public List<DailyBookingReport> getReportByDateRange(LocalDate start, LocalDate end) {
        List<DailyBookingReport> reports = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            reports.addAll(getReportByDate(current));
            current = current.plusDays(1);
        }
        return reports;
    }

    // Fetches a 7-day report ending on the given weekEnding date.
     
    public List<DailyBookingReport> getWeeklyReport(LocalDate weekEnding) {
        LocalDate start = weekEnding.minusDays(6);
        return getReportByDateRange(start, weekEnding);
    }

    public List<MonthlyBookingReport> getReportByMonth(int month, int year) {
        return MonthlyBookingReport.fetchByMonth(month, year);
    }
}
