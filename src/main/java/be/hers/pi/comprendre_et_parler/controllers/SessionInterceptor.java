package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    /**
     * Intercept every incoming HTTP request before it reaches the controller.
     * Redirects unauthenticated users to the login page.
     * @param request the incoming HTTP request containing session and URI information
     * @param response the HTTP response used to send a redirect if needed
     * @param handler the controller that would handle the request
     * @return true if the request should proceed to the controller, false if the user
     * has been redirected to the login page
     * @throws Exception if an error occurs during the redirect
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        AppliUser user = (AppliUser) session.getAttribute("user");

        String path = request.getRequestURI();
        if(user == null){
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    /**
     * Intercept every request after the controller has processed it, before the view is rendered.
     * Injects the authenticated user and the current page identifier into the model
     * so that all templates have access to them without each controller having to do so manually.
     * @param request the incoming HTTP request
     * @param response the HTTP response
     * @param handler the controller that handled the request
     * @param modelAndView the model and view returned by the controller, may be null
     * @post if modelAndView is not null, the authenticated user and the current page
     * identifier have been added to the model
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if(modelAndView == null) return;

        HttpSession session = request.getSession();
        AppliUser user = (AppliUser) session.getAttribute("user");

        modelAndView.addObject("user", user);
        modelAndView.addObject("currentPage", extractCurrentPage(request.getRequestURI()));
    }

    /**
     * Extract the current page identifier from the request URI by matching the last segment of the URL
     * @param url the full request URI, must not be null
     * @return the page identifier matching the last URL segment, or an empty string if no match was found
     */
    private String extractCurrentPage(String url){
        if(url.endsWith("/dashboard")) return "dashboard";
        if(url.endsWith("/horaire")) return "horaire";
        if(url.endsWith("/interpreters")) return "interpreters";
        if(url.endsWith("/beneficiaries")) return "beneficiaries";
        if(url.endsWith("/profile")) return "profile";
        return "";
    }
}
