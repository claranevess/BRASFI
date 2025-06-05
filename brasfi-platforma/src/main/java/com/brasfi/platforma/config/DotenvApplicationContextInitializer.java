package com.brasfi.platforma.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class DotenvApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        // Verifica se o perfil 'render' está ativo
        boolean isRenderProfileActive = environment.acceptsProfiles("render");

        // Se o perfil 'render' NÃO estiver ativo, tenta carregar o .env
        if (!isRenderProfileActive) {
            Dotenv.load();
            System.out.println("DotEnv: .env loaded successfully for local environment.");

        } else {
            System.out.println("DotEnv: 'render' profile active. Skipping .env file loading.");
        }
    }
}