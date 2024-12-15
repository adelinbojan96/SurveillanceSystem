package alert;

import database_connections.DataBaseManager;
import camera_share.SharedData;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class AlertManager {
    private final boolean emailChecked;
    private final boolean snapshotChecked;
    private final boolean dbChecked;

    public AlertManager(boolean emailChecked, boolean snapshotChecked, boolean dbChecked) {
        this.emailChecked = emailChecked;
        this.snapshotChecked = snapshotChecked;
        this.dbChecked = dbChecked;
    }

    private Properties getConfig() {
        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream(getClass().getClassLoader().getResource("./resources/mail.properties").getFile())) {
            config.load(input);
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading mail configuration: " + e.getMessage());
            return null;
        }
        return config;
    }

    private Properties getMailProps(String host)
    {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", "587");
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return mailProps;
    }

    private void sendFinalEmail(Session session, String from, String address, String subject, String messageText) {
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));

            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
            System.out.println("Email sent successfully to " + address);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void saveSnapshot(BufferedImage image, boolean movementDetected) {
        if (snapshotChecked && movementDetected && image != null) {
            try {
                String filePath = "../SurveillanceSystem/snapshots/snapshot_" + System.currentTimeMillis() + ".png";
                File outputFile = new File(filePath);
                outputFile.getParentFile().mkdirs();
                ImageIO.write(image, "png", outputFile);
                System.out.println("Snapshot saved to: " + filePath);
            } catch (IOException e) {
                System.err.println("Error saving snapshot: " + e.getMessage());
            }
        }
    }

    public void saveDB(BufferedImage image, boolean movementDetected)
    {
        if(dbChecked && movementDetected)
        {
            SharedData sharedData = new SharedData();
            byte[] imageBytes = null;
            if (image != null) {
                imageBytes = sharedData.bufferedImageToByteArray(image);
            }
            DataBaseManager dbManager = new DataBaseManager();
            dbManager.insertRecord(imageBytes);
        }
    }

    public void sendMail(String address, boolean movementDetected) {
        if(emailChecked && movementDetected) {
            Properties config = getConfig();

            assert config != null;
            String host = config.getProperty("host");
            String from = config.getProperty("from");
            String password = config.getProperty("password");

            String subject = "Surveillance Alert: Motion Detected";
            String warningMessage = "Warning! The surveillance system has detected motion within the monitored area.";

            Properties mailProps = getMailProps(host);

            Session session = Session.getInstance(mailProps, new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(from, password);
                }
            });

            sendFinalEmail(session, from, address, subject, warningMessage);
        }
    }
}
