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

    /**
     * Retrieve the authenticated user from the session.
     * @param request the HTTP request containing the session
     * @return the authenticated user, or null if no session or no user in session
     */
    private AppliUser getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null) return null;
        return (AppliUser) session.getAttribute("user");
    }

    /**
     * Check if the user is authenticated, and redirect to the login page if not.
     * @param user the user to check, may be null
     * @param response the HTTP response used to send a redirect if needed
     * @return true if the user is authenticated, false if redirected to login
     * @throws IOException if an error occurs during the redirect
     */
    private boolean isAuthenticated(AppliUser user, HttpServletResponse response) throws IOException {
        if(user == null){
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    // ── ACCESS CONTROL ────────────────────────────────────────────────────

    /**
     * Check that the user has already updated their initial password before allowing navigation.
     * Users still on their temporary password may only access the profile page and the
     * password change endpoint; any other request is redirected to the profile page,
     * where the password change modal is forced open.
     * @param user the authenticated user, must not be null
     * @param path the requested URI, must not be null
     * @param response the HTTP response used to send a redirect if needed
     * @return true if the user has already updated their password, or if the requested path is
     * exactly /profil or /profil/modifier-mot-de-passe; false if the user was redirected to /profil
     * @throws IOException if an error occurs during the redirect
     */
    private boolean hasUpdatedPassword(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if (user.isPasswordUpdated()) return true;
        if (path.equals("/profil") || path.equals("/profil/modifier-mot-de-passe")) return true;
        response.sendRedirect("/profil");
        return false;
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
        if(!hasUpdatedPassword(user, path, response)) return false;
        if(!hasManagerAccess(user, path, response)) return false;
        if(!hasInterpreterProfileAccess(user, path, response)) return false;
        if(!hasBeneficiaryProfileAccess(user, path, response)) return false;
        return true;
    }
    /**
     * Check if the user has the required role to access manager-restricted resources.
     * Beneficiaries are granted access to their own profile and profile modification pages,
     * and to the read-only view of an interpreter's profile.
     * Interpreters are granted access to the interpreter profile pages
     * and to the read-only view of a beneficiary's profile.
     * Ownership of the accessed profile is enforced by the profile-specific access checks.
     * @param user the authenticated user, must not be null
     * @param path the requested URI, must not be null
     * @param response the HTTP response used to send a redirect if needed
     * @return true if the user is authorized, false if redirected to the schedule page
     * @throws IOException if an error occurs during the redirect
     */
    private boolean hasManagerAccess(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if (path.startsWith("/dashboard") || path.startsWith("/interpretes")
                || path.startsWith("/beneficiaires") || path.startsWith("/gestion")) {
            if (!(user instanceof Manager)) {
                if (user instanceof Beneficiary) {
                    if (path.matches("/beneficiaires/profil/\\d+") ||
                            path.matches("/beneficiaires/profil/\\d+/modifier") ||
                            path.matches("/interpretes/profil/\\d+")) {
                        return true;
                    }
                } else if (user instanceof Interpreter) {
                    if (path.matches("/interpretes/profil/\\d+.*") ||
                            path.matches("/beneficiaires/profil/\\d+")) {
                        return true;
                    }
                }
                response.sendRedirect("/horaire");
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the user is authorized to access a beneficiary's profile.
     * Managers have unrestricted access. Beneficiaries can only access their own profile.
     * Interpreters are let through: the check that the beneficiary references them
     * is performed in the BeneficiaryController, as it requires data from the database.
     * @param user the authenticated user, must not be null
     * @param path the requested URI, must not be null
     * @param response the HTTP response used to send a redirect if needed
     * @return true if the user is authorized, false if redirected to the profile page
     * @throws IOException if an error occurs during the redirect
     */
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

    /**
     * Check if the user is authorized to access a beneficiary's profile.
     * Managers have unrestricted access. Beneficiaries can only access their own profile.
     * @param user the authenticated user, must not be null
     * @param path the requested URI, must not be null
     * @param response the HTTP response used to send a redirect if needed
     * @return true if the user is authorized, false if redirected to the profile page
     * @throws IOException if an error occurs during the redirect
     */
    private boolean hasBeneficiaryProfileAccess(AppliUser user, String path, HttpServletResponse response) throws IOException {
        if(!path.matches("/beneficiaires/profil/\\d+.*") || user instanceof Manager) return true;
        if(user instanceof Interpreter) return true;
        int id = extractId(path);
        if(user.getId() != id){
            response.sendRedirect("/profil");
            return false;
        }
        return true;
    }

    // ── MODEL INJECTION ───────────────────────────────────────────────────

    /**
     * Inject common attributes into the model for use in all views.
     * @param modelAndView the model to populate, must not be null
     * @param user the authenticated user, must not be null
     * @param uri the requested URI used to determine the current page
     */
    private void injectCommonAttributes(ModelAndView modelAndView, AppliUser user, String uri) {
        modelAndView.addObject("user", user);
        modelAndView.addObject("isManager", user instanceof Manager);
        modelAndView.addObject("currentPage", extractCurrentPage(uri));
        modelAndView.addObject("mustChangePassword", !user.isPasswordUpdated());
    }

    // ── UTILITIES ─────────────────────────────────────────────────────────

    /**
     * Extract the current page identifier from the requested URI.
     * @param uri the requested URI, must not be null
     * @return a string identifying the current page, or an empty string if no match
     */
    private String extractCurrentPage(String uri) {
        if(uri.endsWith("/dashboard")) return "dashboard";
        if(uri.endsWith("/horaire")) return "schedule";
        if(uri.contains("/interpretes")) return "interpreters";
        if(uri.contains("/beneficiaires")) return "beneficiaries";
        if(uri.contains("/gestion")) return "gestion";
        if(uri.contains("/profil")) return "profile";
        return "";
    }

    /**
     * Extract the numeric id from a path containing a profil segment.
     * @param path the requested URI, must not be null
     * @return the extracted id, or -1 if no valid id was found
     */

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
