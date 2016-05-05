package com.bionic.kvt.serviceapp.helpers;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.bionic.kvt.serviceapp.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static com.bionic.kvt.serviceapp.Session.addToSessionLog;

public class MailHelper extends javax.mail.Authenticator {
    private String recipient;
    private String subject;
    private String messageBody;
    private String fullFileName;

    public String getFullFileName() {
        return fullFileName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public boolean send() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Setting 30 second timeout for network operations
        properties.put("mail.smtp.connectiontimeout", "30000");
        properties.put("mail.smtp.timeout", "30000");

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(BuildConfig.EMAIL_FROM, BuildConfig.EMAIL_PASSWORD);
                    }
                });

//        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(BuildConfig.EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setSentDate(new Date());


            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(messageBody, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (fullFileName != null && !fullFileName.isEmpty()) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(fullFileName);
                String shortFileName = new File(fullFileName).getName();
                attachPart.setFileName(shortFileName);
                multipart.addBodyPart(attachPart);
            }
            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            addToSessionLog("ERROR during message sent: " + e.toString());
            return false;
        } catch (IOException e) {
            addToSessionLog("ERROR with file during message sent: " + e.toString());
            return false;
        }

        addToSessionLog("The message was sent successfully to " + recipient);
        return true;
    }

    public static class SendMail extends AsyncTaskLoader<Boolean> {
        private final MailHelper mailHelper;

        public SendMail(Context context, MailHelper mailHelper) {
            super(context);
            this.mailHelper = mailHelper;
        }

        @Override
        public Boolean loadInBackground() {
            return mailHelper != null && mailHelper.send();
        }

        @Override
        public void forceLoad() {
            super.forceLoad();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        protected void onStopLoading() {
            super.onStopLoading();
        }

        @Override
        public void deliverResult(Boolean data) {
            super.deliverResult(data);
        }
    }

}
