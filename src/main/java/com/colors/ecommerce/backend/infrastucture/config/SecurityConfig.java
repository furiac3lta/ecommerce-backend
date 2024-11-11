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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
                        aut -> aut.requestMatchers("/api/v1/admin/categories/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/admin/products/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/orders/**").hasRole("USER")
                                .requestMatchers("/api/v1/payments/success").permitAll()
                                .requestMatchers("/api/v1/payments/**").hasRole("USER")
                                .requestMatchers("/api/payments/webhook", "/confirmacion-pago").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/api/v1/home/**").permitAll()
                                .requestMatchers("/api/v1/security/**").permitAll().anyRequest().authenticated()
//        httpSecurity.csrf(csrf -> csrf.disable()).authorizeHttpRequests(
//                aut -> aut.requestMatchers("/api/v1/admin/categories/**").hasRole("ADMIN")
//                        .requestMatchers("/api/v1/admin/products/**").hasRole("ADMIN")
//                        .requestMatchers("/api/v1/orders/**").hasRole("USER")
//                        .requestMatchers("/api/v1/payments/success").permitAll()
//                        .requestMatchers("/api/v1/payments/**").hasRole("USER")
//                        .requestMatchers("/api/v1/home/**").permitAll()
//                        .requestMatchers("/api/v1/security/**").permitAll().anyRequest().authenticated()
                      //  .requestMatchers("/api/v1/security/**").permitAll()
                    //    .requestMatchers("/api/v1/security/register").permitAll()  // Permitir acceso sin autenticación a /register
                      //  .requestMatchers("/api/v1/security/login").permitAll()
                 //       .requestMatchers("/api/v1/security/**").permitAll().anyRequest().authenticated()
        ).addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("https://ecommerce-angular-five.vercel.app"); // Permite el dominio específico de Vercel
//        config.addAllowedHeader("*"); // Permite todos los headers
//        config.addAllowedMethod("*"); // Permite todos los métodos (GET, POST, etc.)
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }

}
