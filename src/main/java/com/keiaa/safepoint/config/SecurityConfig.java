/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.keiaa.safepoint.service.impl.AdminDetailsServiceImpl;
import com.keiaa.safepoint.service.impl.StudentDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AdminDetailsServiceImpl adminDetailsService;

    @Autowired
    private StudentDetailsServiceImpl studentDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**", "/admin-login")
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/admin-login").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/admin-login")
                .loginProcessingUrl("/admin-login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin-login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .userDetailsService(adminDetailsService);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain studentFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/files/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico",
                    "/manifest.json", "/service-worker.js", "/about", "/student-signup", "/student-login", "/admin-login",
                    "/verify-email", "/resend-verification", "/forgot-password", "/reset-password").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/dashboard", "/report", "/submit-report", "/schedule").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/student-login")
                .loginProcessingUrl("/student-login")
                .defaultSuccessUrl("/", true)
                .failureHandler((request, response, exception) -> {
                    if (exception instanceof LockedException) {
                        response.sendRedirect("/student-login?locked=true");
                    } else {
                        response.sendRedirect("/student-login?error=true");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/student/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .userDetailsService(studentDetailsService)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

        return http.build();
    }
}