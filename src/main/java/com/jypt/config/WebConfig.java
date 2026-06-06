// src/main/java/com/jypt/config/WebConfig.java
package com.jypt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 请求跨域处理
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 配置跨域请求
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 改为 allowedOriginPatterns
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)  // 允许携带凭证（如 cookies）
                .maxAge(3600);
    }
}
