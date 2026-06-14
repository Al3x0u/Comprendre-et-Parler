package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Base64.getUrlEncoder;

@Service
public class PasswordResetService {
    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(10);
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(4);
    private static final int MAX_REQUESTS_PER_WINDOW = 3;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();
    private final Map<String, List<Instant>> requests = new ConcurrentHashMap<>();
    private final NotificationService notificationService = new NotificationService();

    /**
     * Process a reinitialization request : si the email exist et there is still time in the limit
     * generate a token and send the email. Never reveal to the user if the email exist or not
     * @param email the email entered by the user
     * qparam baseUrl the root of the website, to build the url
     */
    public void requestReset(String email, String baseUrl){
        if(email == null || email.isBlank()) return;
        email = email.trim();
        if(isRateLimited(email)) return;
        try{
            int userId = SQLWrap.call(new DAOAppliUser()::findByEmail, email);
            if(userId < 0) return;//no account, stop
            String token = createToken(userId,  email);
            String resetUrl = baseUrl + "/reinitialiser?token=" + token;
            notificationService.sendPasswordReset(email, resetUrl, (int) TOKEN_VALIDITY.toMinutes());
        } catch (Exception e) {
            e.printStackTrace();//no need to propagate, the controler will do nothing with it
        }
    }

    /**
     * @return true if the token exist and hasn't expired (without consume it)
     */
    public synchronized boolean isTokenValid(String token){
        TokenEntry entry = tokens.get(token);
        return entry != null && entry.expiry.isAfter(Instant.now());
    }

    /**
     * Consume the token (one use only) et return the id of the user
     * @return the id of the user or -1 if the token is unknow or expired
     */
    public synchronized int consumeToken(String token){
        int theId = -1;
        TokenEntry entry = tokens.remove(token);
        if(!(entry == null || entry.expiry.isBefore(Instant.now()))){
            theId = entry.userId;
        }
        return theId;
    }

    private synchronized boolean isRateLimited(String email){
        Instant threshold = Instant.now().minus(RATE_LIMIT_WINDOW);
        List<Instant> times = requests.get(email);

        if(times == null) return false;

        times.removeIf(t -> t.isBefore(threshold));

        return times.size() >= MAX_REQUESTS_PER_WINDOW;
    }

    private synchronized String createToken(int userId, String email){
        String token = generateToken();
        tokens.put(token, new TokenEntry(userId, Instant.now().plus(TOKEN_VALIDITY)));
        requests.computeIfAbsent(email, k -> new ArrayList<>()).add(Instant.now());

        return token;
    }

    private String generateToken(){
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static class TokenEntry{
        final int userId;
        final Instant expiry;

        TokenEntry(int userId, Instant expiry){
            this.userId = userId;
            this.expiry = expiry;
        }
    }
}
