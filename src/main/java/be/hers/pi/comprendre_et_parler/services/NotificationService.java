package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DTO.UserCredentials;
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
     */
    public void notifyDelay(Mission mission, String delayInfo) {
        String subject = "Retard signalé pour votre mission";
        String body = "<p>Bonjour,</p>"
                + "<p>Un retard a été signalé pour la mission <strong>\"" + mission.getSubject() + "\"</strong>"
                + " du " + formatTimeSlot(mission.getTimeSlot()) + ".</p>"
                + "<p>Détails : " + delayInfo + "</p>"
                + "<p>Cordialement,<br>L'équipe Comprendre et Parler</p>";

        if (mission.getInterpreters() != null)
            for (Interpreter i : mission.getInterpreters())
                sendEmail(i.getEmail(), subject, body);

        if (mission.getBeneficiary() != null)
            sendEmail(mission.getBeneficiary().getEmail(), subject, body);
    }

    /**
     * Notifies the interpreter(s) and the beneficiary that a mission has been cancelled.
     */
    public void notifyCancellation(Mission mission) {
        String subject = "Mission annulée";
        String body = "<p>Bonjour,</p>"
                + "<p>La mission <strong>\"" + mission.getSubject() + "\"</strong>"
                + " du " + formatTimeSlot(mission.getTimeSlot()) + " a été annulée.</p>"
                + "<p>Cordialement,<br>L'équipe Comprendre et Parler</p>";

        if (mission.getInterpreters() != null)
            for (Interpreter i : mission.getInterpreters())
                sendEmail(i.getEmail(), subject, body);

        if (mission.getBeneficiary() != null)
            sendEmail(mission.getBeneficiary().getEmail(), subject, body);
    }

    /**
     * Notifies the beneficiary that their request has been refused.
     */
    public void notifyRefusal(Mission mission) {
        String subject = "Demande de mission refusée";
        String body = "<p>Bonjour,</p>"
                + "<p>Votre demande de mission <strong>\"" + mission.getSubject() + "\"</strong>"
                + " du " + formatTimeSlot(mission.getTimeSlot()) + " a été refusée.</p>"
                + "<p>Cordialement,<br>L'équipe Comprendre et Parler</p>";

        if (mission.getBeneficiary() != null)
            sendEmail(mission.getBeneficiary().getEmail(), subject, body);
    }

    /**
     * Notifies the User that his account has been created
     */
    public void sendUserCredentials(UserCredentials userCredentials) {
        String subject = "Vos informations de connexion";
        String body = "<p>Bonjour " + userCredentials.getFirstName() + ",</p>"
                + "<p>Votre compte a été créé sur la plateforme Comprendre &amp; Parler.</p>"
                + "<p>Voici vos identifiants de première connexion :</p>"
                + "<p>"
                + "Login : <strong>" + userCredentials.getLogin() + "</strong><br>"
                + "Mot de passe : <strong>" + userCredentials.getPassword() + "</strong><br>"
                + "Connectez-vous via ce lien : "
                + "<a href=\"" + userCredentials.getLoginUrl() + "\">" + userCredentials.getLoginUrl() + "</a>"
                + "</p>"
                + "<p><strong>Attention :</strong> Vous devrez changer votre mot de passe dès votre première connexion.</p>";

        sendEmail(userCredentials.getEmail(), subject, body);
    }

    /**
     * Sends an HTML email to the given recipient.
     * Failures are logged but do not propagate, so a notification error
     * never rolls back the calling business transaction.
     */
    private void sendEmail(String dest, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dest));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
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