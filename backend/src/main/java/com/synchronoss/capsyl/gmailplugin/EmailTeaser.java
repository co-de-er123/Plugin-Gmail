package com.synchronoss.capsyl.gmailplugin;

public class EmailTeaser {
    private String id;
    private String subject;

    public EmailTeaser(String id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
