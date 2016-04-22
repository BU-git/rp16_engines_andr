package com.bionic.kvt.serviceapp.helpers;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.bionic.kvt.serviceapp.BuildConfig;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
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
    private Properties properties;
    private String recipient;
    private String subject;
    private String body;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public MailHelper() {
        properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
    }


    public boolean send() {
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(BuildConfig.EMAIL_FROM, BuildConfig.EMAIL_PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(BuildConfig.EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            multipart.addBodyPart(messageBodyPart);

            if (fullFileName != null && !fullFileName.isEmpty()) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(fullFileName);
                messageBodyPart.setDataHandler(new DataHandler(source));
                String shortFileName = new File(fullFileName).getName();
                messageBodyPart.setFileName(shortFileName);
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            if (BuildConfig.IS_LOGGING_ON)
                addToSessionLog("ERROR during message sent: " + e.toString());
            return false;
        }

        if (BuildConfig.IS_LOGGING_ON)
            addToSessionLog("The message was sent successfully to " + recipient);
        return true;
    }

    public static class SendMail extends AsyncTaskLoader<Boolean> {
        private MailHelper mailHelper;

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
