package be.hers.pi.comprendre_et_parler.controllers.interceptors;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AppliUser user = getUser(request);
        if(!isAuthenticated(user, response)) return false;
        if(!hasAccess(user, request.getRequestURI(), response)) return false;
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if(modelAndView == null) return;
        AppliUser user = getUser(request);
        if(user == null) return;
        injectCommonAttributes(modelAndView, user, request.getRequestURI());
    }

    // ── AUTHENTICATION ────────────────────────────────────────────────────

    private AppliUser getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null) return null;
        return (AppliUser) session.getAttribute("user");
    }

    private boolean isAuthenticated(AppliUser user, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    // ── ACCESS CONTROL ────────────────────────────────────────────────────

    /**
     * Check if the user has updated their password, and redirect to the password change page if not.
     * This check takes priority over all other access control checks.
     * @param user the authenticated user, must not be null
     * @param path the requested URI, must not be null
     * @param response the HTTP response used to send a redirect if needed
     * @return true if the user has updated their password or is accessing the password change page,
     *         false if the user has been redirected to the password change page
     * @throws IOException if an error occurs during the redirect
     */
    private boolean hasPasswordUpdated(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if(!user.isPasswordUpdated() && !path.startsWith("/profil/modifier-mot-de-passe") && !path.equals("/logout")){
            response.sendRedirect("/profil/modifier-mot-de-passe");
            return false;
        }
        return true;
    }

    /**
     * Check if the user is authorized to access the requested resource.
     * Verifies password update status, role-based access, and profile-specific access rules.
     * @param user the authenticated user, must not be null
     * @param path the requested URI, must not be null
     * @param response the HTTP response used to send a redirect if needed
     * @return true if the user is authorized to access the resource, false if the user has been redirected
     * @throws IOException if an error occurs during the redirect
     */
    private boolean hasAccess(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if(!hasPasswordUpdated(user, path, response)) return false;
        if(!hasManagerAccess(user, path, response)) return false;
        if(!hasInterpreterProfileAccess(user, path, response)) return false;
        if(!hasBeneficiaryProfileAccess(user, path, response)) return false;
        return true;
    }

    private boolean hasManagerAccess(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if(path.startsWith("/dashboard") || path.startsWith("/interpretes") || path.startsWith("/beneficiaires")){
            if(!(user instanceof Manager)){
                response.sendRedirect("/horaire");
                return false;
            }
        }
        return true;
    }

    private boolean hasInterpreterProfileAccess(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if(!path.matches("/interpretes/profil/\\d+.*") || user instanceof Manager) return true;
        int id = extractId(path);
        if(user instanceof Beneficiary b){
            if(b.getInterpreterRef() == null || b.getInterpreterRef().getId() != id){
                response.sendRedirect("/profil");
                return false;
            }
        } else if(user.getId() != id){
            response.sendRedirect("/profil");
            return false;
        }
        return true;
    }

    private boolean hasBeneficiaryProfileAccess(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if(!path.matches("/beneficiaires/profil/\\d+.*") || user instanceof Manager) return true;
        int id = extractId(path);
        if(user.getId() != id){
            response.sendRedirect("/profil");
            return false;
        }
        return true;
    }

    // ── MODEL INJECTION ───────────────────────────────────────────────────

    private void injectCommonAttributes(ModelAndView modelAndView, AppliUser user, String uri) {
        modelAndView.addObject("user", user);
        modelAndView.addObject("isManager", user instanceof Manager);
        modelAndView.addObject("currentPage", extractCurrentPage(uri));
    }

    // ── UTILITIES ─────────────────────────────────────────────────────────

    private String extractCurrentPage(String uri) {
        if(uri.endsWith("/dashboard")) return "dashboard";
        if(uri.endsWith("/horaire")) return "horaire";
        if(uri.contains("/interpretes")) return "interpreters";
        if(uri.contains("/beneficiaires")) return "beneficiaries";
        if(uri.contains("/profil")) return "profile";
        return "";
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        for(int i = 0; i < parts.length; i++){
            if(parts[i].equals("profil") && i + 1 < parts.length){
                try { return Integer.parseInt(parts[i + 1]); }
                catch(NumberFormatException ignored) {}
            }
        }
        return -1;
    }
}
