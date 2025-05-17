package controller.Homeowner;

import java.util.List;
import entity.Cleaner.CleanerAccount;
import entity.Homeowner.Shortlist;
import entity.Homeowner.ShortlistService;

public class ShortlistController {
    private ShortlistService shortlistService;
    private Shortlist shortlist;
    
    public ShortlistController() {
        this.shortlistService = new ShortlistService();
        this.shortlist = new Shortlist();
    }
    
    // Methods from original ShortlistController
    public boolean addCleanerToShortlist(String homeownerUsername, String cleanerId) {
        return shortlistService.add(homeownerUsername, cleanerId);
    }
    
    public boolean removeCleanerFromShortlist(String homeownerUsername, String cleanerId) {
        return shortlistService.remove(homeownerUsername, cleanerId);
    }
    
    public List<CleanerAccount> getShortlist(String homeownerUsername) {
        return shortlistService.getShortlist(homeownerUsername);
    }
    
    public boolean isCleanerInShortlist(String homeownerUsername, String cleanerId) {
        return shortlistService.isCleanerInShortlist(homeownerUsername, cleanerId);
    }
    
    // Methods from ShortlistSearchController
    public List<CleanerAccount> searchShortlist(String criteria, String homeownerUsername) {
        return shortlist.getMatchingCleaners(criteria, homeownerUsername);
    }
    
    // New method to get shortlist count
    public int getShortlistCount(String cleanerUsername) {
        return shortlistService.getCountByUsername(cleanerUsername);
    }

    public List<String> getHomeownersWhoShortlisted(String cleanerUsername) {
        return shortlistService.getHomeownersWhoShortlisted(cleanerUsername);
    }
}