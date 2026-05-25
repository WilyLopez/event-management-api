package com.playzone.pems.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StorageWebConfig implements WebMvcConfigurer {

    @Value("${storage.local.ruta-base:/uploads}")
    private String rutaBase;

    @Value("${storage.local.url-base:/files}")
    private String urlBase;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(urlBase + "/**")
                .addResourceLocations("file:" + rutaBase + "/");
    }
}
