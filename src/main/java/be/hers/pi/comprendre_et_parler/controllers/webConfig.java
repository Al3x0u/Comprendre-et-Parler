package be.hers.pi.comprendre_et_parler.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class webConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(sessionInterceptor)
                .excludePathPatterns("/login", "/", "/hash", "/css/**", "/js/**", "/images/**");
    }
}
