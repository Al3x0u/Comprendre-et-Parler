package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.models.*;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.util.Properties;

public class NotificationService {

    private final String from;
    private final Session session;

    public NotificationService() {
        Properties config = new Properties();
        try {
            config.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger application.properties", e);
        }

        this.from = config.getProperty("mail.from");

        Properties props = new Properties();
        props.put("mail.smtp.host", config.getProperty("mail.host"));
        props.put("mail.smtp.port", config.getProperty("mail.port"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        String user = config.getProperty("mail.user");
        String password = config.getProperty("mail.password");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
    }

    /**
     * Notifies the interpreter(s) and the beneficiary of a reported delay
     * @param mission the mission for which the delay is reported
     * @param delayInfo a message describing the delay
     */
    public void notifyDelay(Mission mission, String delayInfo) {
        String subject = "Retard signalé pour votre mission";
        String body = "Bonjour,\n\n"
                + "Un retard a été signalé pour la mission \"" + mission.getSubject() + "\""
                + " du " + formatTimeSlot(mission.getTimeSlot()) + ".\n"
                + "Détails : " + delayInfo + "\n\n"
                + "Cordialement,\nL'équipe Comprendre et Parler";

        if (mission.getInterpreters() != null)
            for (Interpreter i : mission.getInterpreters())
                sendEmail(i.getEmail(), subject, body);

        if (mission.getBeneficiary() != null)
            sendEmail(mission.getBeneficiary().getEmail(), subject, body);
    }

    /**
     * Notifies the interpreter(s) and the beneficiary that a mission has been cancelled.
     * @param mission the cancelled mission
     */
    public void notifyCancellation(Mission mission) {
        String subject = "Mission annulée";
        String body = "Bonjour,\n\n"
                + "La mission \"" + mission.getSubject() + "\""
                + " du " + formatTimeSlot(mission.getTimeSlot()) + " a été annulée.\n\n"
                + "Cordialement,\nL'équipe Comprendre et Parler";

        if (mission.getInterpreters() != null)
            for (Interpreter i : mission.getInterpreters())
                sendEmail(i.getEmail(), subject, body);

        if (mission.getBeneficiary() != null)
            sendEmail(mission.getBeneficiary().getEmail(), subject, body);
    }


    /**
     * Notifies the beneficiary that their request has been refused.
     * @param mission the refused mission
     */
    public void notifyRefusal(Mission mission) {
        String subject = "Demande de mission refusée";
        String body = "Bonjour,\n\n"
                + "Votre demande de mission \"" + mission.getSubject() + "\""
                + " du " + formatTimeSlot(mission.getTimeSlot()) + " a été refusée.\n\n"
                + "Cordialement,\nL'équipe Comprendre et Parler";

        if (mission.getBeneficiary() != null)
            sendEmail(mission.getBeneficiary().getEmail(), subject, body);
    }

    /**
     * Sends an email to the given recipient.
     * Failures are logged but do not propagate, so a notification error
     * never rolls back the calling business transaction.
     * @param dest      recipient email address
     * @param subject email subject
     * @param body    plain-text email body
     */
    private void sendEmail(String dest, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dest));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String formatTimeSlot(TimeSlot ts) {
        if (ts instanceof PunctualTimeSlot) {
            PunctualTimeSlot pts = (PunctualTimeSlot) ts;
            return pts.getStartDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'de' HH:mm"))
                    + " à "
                    + pts.getEndDate().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        if (ts instanceof BaseTimeSlot) {
            BaseTimeSlot bts = (BaseTimeSlot) ts;
            return "chaque " + bts.getDay()
                    + " de " + bts.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                    + " à "
                    + bts.getEndTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        return ts.toString();
    }
}