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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if(modelAndView == null) return;

        HttpSession session = request.getSession();
        AppliUser user = (AppliUser) session.getAttribute("user");

        modelAndView.addObject("user", user);
        modelAndView.addObject("currentPage", extractCurrentPage(request.getRequestURI()));
    }

    private String extractCurrentPage(String url){
        if(url.contains("dashboard")) return "dashboard";
        if(url.contains("horaire")) return "horaire";
        if(url.contains("interpreters")) return "interpreters";
        if(url.contains("beneficiaries")) return "beneficiaries";
        if(url.contains("profile")) return "profile";
        return "";
    }
}
