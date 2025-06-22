package com.synchronoss.capsyl.gmailplugin;

import java.util.List;

public class EmailPage {
    private List<EmailTeaser> emails;
    private String nextPageToken;

    public EmailPage(List<EmailTeaser> emails, String nextPageToken) {
        this.emails = emails;
        this.nextPageToken = nextPageToken;
    }

    // Getters
    public List<EmailTeaser> getEmails() {
        return emails;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
