package be.hers.pi.comprendre_et_parler.controllers.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    /**
     * Register the SessionInterceptor for all incoming requests, excluding public URLs
     * that do not require authentication such as the login page and static resources
     * @param registry the interceptor registry provided by Spring, must not be null
     * @post the SessionInterceptor has been registered and will intercept all requests
     * except those matching the excluded path patterns
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .excludePathPatterns(
                        "/",
                        "/login",
                        "/logout",
                        "/hash",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico"
                );
    }
}

