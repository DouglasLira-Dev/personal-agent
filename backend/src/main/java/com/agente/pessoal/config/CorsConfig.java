package com.agente.pessoal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracao de CORS para permitir o front-end (Vite/Vercel) chamar o back-end.
 * CorsConfig
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * Origens permitidas, lidas de variavel de ambiente.
     * Em dev: http://localhost:5173
     * Em prod: https://agente-pessoal.vercel.app (e localhost se quiser testar)
     */
    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173, http://127.0.0.1:5173}")
    private String[] origensPermitidas;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origensPermitidas)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
