package controller.Cleaner;

import entity.TrackViewInformation;
import java.util.Map;

    // Controller class for handling profile view tracking functionality
public class TrackViewInformationController {
    private TrackViewInformation trackViewInfo;
    
    public TrackViewInformationController() {
        this.trackViewInfo = new TrackViewInformation();
    }
    
    // Get view statistics for a specific service
    public Map<Integer, Integer> getViewStats(int serviceId) {
        return trackViewInfo.getViewStats(serviceId);
    }
    
    // Increment the view count for a specific service
    public boolean incrementViewCount(int serviceId) {
        return trackViewInfo.incrementViewCount(serviceId);
    }
    
    // Get view statistics for all services of a cleaner
    public Map<Integer, Integer> getViewStatsByUsername(String cleanerUsername) {
        return trackViewInfo.getViewStatsByUsername(cleanerUsername);
    }
}