package org.Esprit.TripNShip.Services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USERNAME = "youssefharrabi99@gmail.com";
    private static final String PASSWORD = "lbemaahclveyljgb"; // App password, not regular password
    private static final String FROM_EMAIL = "youssefharrabi99@gmail.com";
    private static final String FROM_NAME = "TripNShip Notifications";

    private static Properties getEmailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    private static Session getEmailSession() {
        return Session.getInstance(getEmailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    /**
     * Sends an email to a client about transporter assignment
     *
     * @param toEmail Client's email address
     * @param clientName Client's name
     * @param expeditionId Expedition ID
     * @param transporterName Transporter's name
     * @param transporterType Type of transport service
     * @param route The expedition route (from -> to)
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendTransporterAssignmentEmail(String toEmail, String clientName,
                                                  int expeditionId, String transporterName, String transporterType, String route) {

        try {
            Session session = getEmailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            message.setSubject("Transporter Assigned to Your Expedition #" + expeditionId);

            // Create HTML email content
            String emailContent =
                    "<html>" +
                            "<head>" +
                            "  <style>" +
                            "    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; }" +
                            "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                            "    .header { background: linear-gradient(to right, #4a90e2, #8e44ad); color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                            "    .content { background: #fff; padding: 20px; border-left: 1px solid #ddd; border-right: 1px solid #ddd; }" +
                            "    .expedition-info { background: #f9f9f9; padding: 15px; margin: 15px 0; border-radius: 5px; }" +
                            "    .expedition-detail { margin: 10px 0; }" +
                            "    .buttons { text-align: center; margin: 25px 0 15px; }" +
                            "    .button { display: inline-block; padding: 10px 20px; margin: 0 10px; text-decoration: none; color: white; border-radius: 5px; font-weight: bold; }" +
                            "    .accept-btn { background-color: #28a745; }" +
                            "    .reject-btn { background-color: #dc3545; }" +
                            "    .footer { text-align: center; padding: 20px; color: #777; font-size: 12px; border-radius: 0 0 5px 5px; background: #f5f5f5; }" +
                            "  </style>" +
                            "</head>" +
                            "<body>" +
                            "  <div class='container'>" +
                            "    <div class='header'>" +
                            "      <h2>TripNShip Notification</h2>" +
                            "    </div>" +
                            "    <div class='content'>" +
                            "      <p>Hello " + clientName + ",</p>" +
                            "      <p>We're pleased to inform you that a transporter has been assigned to your expedition.</p>" +
                            "      <div class='expedition-info'>" +
                            "        <div class='expedition-detail'><strong>Expedition ID:</strong> #" + expeditionId + "</div>" +
                            "        <div class='expedition-detail'><strong>Route:</strong> " + route + "</div>" +
                            "        <div class='expedition-detail'><strong>Transporter Name:</strong> " + transporterName + "</div>" +
                            "        <div class='expedition-detail'><strong>Transport Type:</strong> " + transporterType + "</div>" +
                            "      </div>" +
                            "      <p>Please review the assigned transporter details and accept or reject this assignment by logging into your TripNShip account.</p>" +

                            "      <p>This assignment will be awaiting your approval. Once accepted, your package will be shipped immediately.</p>" +
                            "    </div>" +
                            "    <div class='footer'>" +
                            "      <p>This is an automated message, please do not reply to this email. If you need assistance, please contact our support team.</p>" +
                            "      <p>&copy; " + java.time.Year.now().getValue() + " TripNShip, All Rights Reserved.</p>" +
                            "    </div>" +
                            "  </div>" +
                            "</body>" +
                            "</html>";

            // Set email content as HTML
            message.setContent(emailContent, "text/html; charset=utf-8");

            // Send the message
            Transport.send(message);
            System.out.println("Transporter assignment email sent to " + clientName);

            return true;

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
