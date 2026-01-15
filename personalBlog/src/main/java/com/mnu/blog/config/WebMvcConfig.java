package com.mnu.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 내 프로젝트의 절대 경로(Root)를 가져옵니다.
        String projectPath = System.getProperty("user.dir"); 

        // 2. /images/profile/로 시작하는 주소는 -> 실제 src/main/resources/... 폴더를 바라보게 함
        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:///" + projectPath + "/src/main/resources/static/images/profile/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}