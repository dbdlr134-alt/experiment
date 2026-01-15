package com.mnu.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.path}")
    private String uploadFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/profile/** 로 요청이 오면 file:///C:/blog/upload/ 에서 찾는다.
        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:///" + uploadFolder);
    }
}