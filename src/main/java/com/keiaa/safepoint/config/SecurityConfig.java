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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.keiaa.safepoint.service.impl.AdminDetailsService;
import com.keiaa.safepoint.service.impl.StudentDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private AdminDetailsService adminDetailsService;

    @Autowired
    private StudentDetailsService studentDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new org.springframework.security.core.session.SessionRegistryImpl();
    }

    @Bean
    public org.springframework.security.web.session.HttpSessionEventPublisher httpSessionEventPublisher() {
        return new org.springframework.security.web.session.HttpSessionEventPublisher();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**", "/admin-login")
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/admin-login").permitAll()
                .anyRequest().hasRole("ADMIN")
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
            .userDetailsService(adminDetailsService)
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain studentFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/files/**", "/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**", "/favicon.ico",
                    "/manifest.json", "/service-worker.js", "/about", "/student-signup", "/student-login", "/admin-login",
                    "/verify-email", "/resend-verification", "/forgot-password", "/reset-password").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/dashboard", "/report", "/submit-report", "/schedule").hasRole("STUDENT")
                .anyRequest().denyAll()
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
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

        return http.build();
    }
}
