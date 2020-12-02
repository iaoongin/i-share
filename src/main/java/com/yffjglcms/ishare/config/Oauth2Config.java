package com.yffjglcms.ishare.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Oauth2Config
 * https://docs.spring.io/spring-security/site/docs/5.0.7.RELEASE/reference/html/oauth2login-advanced.html#oauth2login-advanced-authorization-endpoint
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/10/23 11:48
 */
@EnableOAuth2Client
@Configuration
public class Oauth2Config extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .oauth2Login()
//                .loginPage("/login/oauth2")
                .successHandler((request, response, authentication) -> {
                    Object details = authentication.getDetails();
                    String state = request.getParameter(OAuth2Utils.STATE);
                    System.out.println(JSONUtil.toJsonStr(request.getParameterMap()));

                })
                .and()
                .csrf().disable()
        ;

        //需要的时候创建session，支持从session中获取认证信息，ResourceServerConfiguration中
        //session创建策略是stateless不使用，这里其覆盖配置可创建session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http.cors();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(CollUtil.newArrayList("*"));
        configuration.setAllowedMethods(CollUtil.newArrayList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(CollUtil.newArrayList("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
