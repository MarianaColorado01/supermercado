/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.superm.supermercado;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author mariana01colorado
 */

@Configuration
public class securityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // desactiva CSRF (para Postman)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // permite todos los endpoints
            )
            .formLogin(login -> login.disable()) // desactiva login por formulario
            .httpBasic(basic -> basic.disable()); // desactiva autenticación básica

        return http.build();
    }
    
}
