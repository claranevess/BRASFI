package com.brasfi.platforma.config; // Ou um pacote de sua preferência

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.load(); // Carrega o .env da raiz do projeto
        Map<String, Object> dotenvMap = new HashMap<>();
        dotenv.entries().forEach(entry -> dotenvMap.put(entry.getKey(), entry.getValue()));

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap));
    }
}