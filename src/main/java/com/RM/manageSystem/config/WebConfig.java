package com.RM.manageSystem.config;

import com.RM.manageSystem.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer { // 實現WebMvcConfigurer接口來自定義Spring MVC的配置

    @Autowired // 通過自動裝配獲取LoginInterceptor實例
    private LoginInterceptor loginInterceptor;

    @Override // 覆寫接口中的方法，用於註冊攔截器
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加攔截器到Spring MVC攔截器鏈
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/user/register",
                        "/user/login",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**", "/v3/api-docs/**", "/swagger-resources/**"
                        ); // 設置排除攔截的路徑，例如用戶註冊和登錄路徑不需要攔截
    }
}
