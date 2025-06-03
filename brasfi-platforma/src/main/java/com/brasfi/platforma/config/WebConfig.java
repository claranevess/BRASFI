    package com.brasfi.platforma.config;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.*;

    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // Expõe arquivos da pasta "uploads/" no sistema de arquivos para URLs acessíveis via /uploads/**
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:uploads/"); // Caminho relativo ao diretório do projeto
        }
    }
