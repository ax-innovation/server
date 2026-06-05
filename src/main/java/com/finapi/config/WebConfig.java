package com.finapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Spring Boot 백엔드 설정 예시
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://ax-innovation-frontend.vercel.app") // Vercel 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}