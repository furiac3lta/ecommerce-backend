package com.colors.ecommerce.backend.infrastucture.config;

import com.colors.ecommerce.backend.infrastucture.jwt.JWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(JWTAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(
                        cors -> cors.configurationSource(
                                request -> {
                                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                                    corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
                                    corsConfiguration.setAllowedMethods(Arrays.asList("*"));
                                    corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
                                    return  corsConfiguration;
                                }
                        )).
                csrf( csrf-> csrf.disable()).authorizeHttpRequests(
                        aut -> aut
                                .requestMatchers("/api/v1/orders/**").permitAll()
                                .requestMatchers("/api/v1/admin/categories").permitAll()
                               // .requestMatchers("/api/v1/users/**").permitAll()
                                .requestMatchers("/api/v1/admin/categories/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/admin/products/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/users/**").hasAnyRole("USER", "ADMIN")

                            //    .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/payments/success").permitAll()
                                .requestMatchers("/api/v1/payments/**").hasRole("USER")
                                .requestMatchers("/api/payments/webhook", "/confirmacion-pago").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/api/v1/home/**").permitAll()
                                .requestMatchers("/api/v1/security/**").permitAll()
                                .requestMatchers("/admin/product").hasRole("ADMIN")
                                .anyRequest().authenticated()
                ).exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/user/login"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.sendRedirect("/user/login")))
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://www.lcosmeticadigital.com.ar"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

}
