package com.synchronoss.capsyl.gmailplugin;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GmailService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GmailService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    private Gmail getGmailClient(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName()
                );

        String accessToken = client.getAccessToken().getTokenValue();
        Credential credential = new GoogleCredential().setAccessToken(accessToken);

        return new Gmail.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Gmail Capsyl Plugin")
                .build();
    }

        public EmailPage getEmailsWithAttachments(OAuth2AuthenticationToken authentication, String pageToken) throws IOException {
        Gmail gmail = getGmailClient(authentication);

                ListMessagesResponse response = gmail.users().messages().list("me")
                .setQ("has:attachment")
                .setPageToken(pageToken)
                .execute();

                List<EmailTeaser> emails = new ArrayList<>();
        if (response.getMessages() != null) {
            emails = response.getMessages().stream().map(message -> {
            try {
                Message msg = gmail.users().messages().get("me", message.getId()).setFormat("metadata").execute();
                String subject = msg.getPayload().getHeaders().stream()
                        .filter(header -> header.getName().equals("Subject"))
                        .findFirst()
                        .map(header -> header.getValue())
                        .orElse("No Subject");
                return new EmailTeaser(message.getId(), subject);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
                    }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
        }

        return new EmailPage(emails, response.getNextPageToken());
    }

    public List<Map<String, String>> getAttachments(OAuth2AuthenticationToken authentication, String messageId) throws IOException {
        Gmail gmail = getGmailClient(authentication);
        Message message = gmail.users().messages().get("me", messageId).execute();
        List<MessagePart> parts = message.getPayload().getParts();
        List<Map<String, String>> attachments = new ArrayList<>();

        if (parts != null) {
            for (MessagePart part : parts) {
                if (part.getFilename() != null && part.getFilename().length() > 0) {
                    String filename = part.getFilename();
                    String attachmentId = part.getBody().getAttachmentId();
                    MessagePartBody body = gmail.users().messages().attachments().get("me", messageId, attachmentId).execute();

                    Map<String, String> attachmentData = new java.util.HashMap<>();
                    attachmentData.put("filename", filename);
                    attachmentData.put("mimeType", part.getMimeType());
                    attachmentData.put("data", body.getData()); // Data is already Base64 encoded
                    attachments.add(attachmentData);
                }
            }
        }
        return attachments;
    }
}
