package com.synchronoss.capsyl.gmailplugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private GmailService gmailService;

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return Collections.singletonMap("name", "Guest");
        }
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/api/emails")
        public EmailPage getEmails(@RequestParam(required = false) String pageToken, OAuth2AuthenticationToken authentication) throws IOException {
        if (authentication == null) {
            return new EmailPage(Collections.emptyList(), null);
        }
        return gmailService.getEmailsWithAttachments(authentication, pageToken);
    }

    @GetMapping("/api/attachments/{messageId}")
    public ResponseEntity<List<Map<String, String>>> getAttachments(@PathVariable String messageId, OAuth2AuthenticationToken authentication) throws IOException {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        List<Map<String, String>> attachments = gmailService.getAttachments(authentication, messageId);
        return ResponseEntity.ok(attachments);
    }
}
