package org.Esprit.TripNShip.Utils;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EmailSender {

    private static final String FROM_EMAIL = "islem24762048@gmail.com";
    private static final String EMAIL_PASSWORD = "jmqi jkcf esrk ezig";
    public static final String welcomeEmailTemplate = "src/main/resources/templates/email_template.html";
    public static final String changePwdTemplate = "src/main/resources/templates/changePwd_template.html";

    public static void accountEmail(String toEmail, String name , String code, String template, String subject, String sender) {


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });

        try {
            String htmlTemplate = Files.readString(Paths.get(template));

// Replace placeholders
            htmlTemplate = htmlTemplate.replace("{{name}}", name);
            htmlTemplate = htmlTemplate.replace("{{code}}", code);
            htmlTemplate = htmlTemplate.replace("{{image}}", "cid:logoImage"); // Make sure the src matches

// Create the message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

// Create HTML part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlTemplate, "text/html; charset=utf-8");

// Create image part
            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource fds = new FileDataSource("src/main/resources/images/logo.png"); // 👈 use your local image path
            imagePart.setDataHandler(new DataHandler(fds));
            imagePart.setHeader("Content-ID", "<logoImage>");
            imagePart.setDisposition(MimeBodyPart.INLINE); // So it's shown in email

// Combine parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            multipart.addBodyPart(imagePart);

// Set content and send
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("✅ HTML email with embedded image sent to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
