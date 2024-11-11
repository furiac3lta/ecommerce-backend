//package com.colors.ecommerce.backend.infrastucture.config;
//
//import com.colors.ecommerce.backend.infrastucture.jwt.JWTAuthorizationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final JWTAuthorizationFilter jwtAuthorizationFilter;
//
//    public SecurityConfig(JWTAuthorizationFilter jwtAuthorizationFilter) {
//        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.cors(
//                        cors -> cors.configurationSource(
//                                request -> {
//                                    CorsConfiguration corsConfiguration = new CorsConfiguration();
//                                    corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
//                                    corsConfiguration.setAllowedMethods(Arrays.asList("*"));
//                                    corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
//                                    return  corsConfiguration;
//                                }
//                        )).
//                csrf( csrf-> csrf.disable()).authorizeHttpRequests(
//                        aut -> aut.requestMatchers("/api/v1/admin/categories/**").hasRole("ADMIN")
//                                .requestMatchers("/api/v1/admin/products/**").hasRole("ADMIN")
//                                .requestMatchers("/api/v1/users/**").hasRole("USER")
//                                .requestMatchers("/api/v1/orders").permitAll()
//                                .requestMatchers("/api/v1/orders/**").permitAll()
//                                .requestMatchers("/api/v1/payments/success").permitAll()
//                                .requestMatchers("/api/v1/payments/**").hasRole("USER")
//                                .requestMatchers("/api/payments/webhook", "/confirmacion-pago").permitAll()
//                                .requestMatchers("/images/**").permitAll()
//                                .requestMatchers("/api/v1/home/**").permitAll()
//                                .requestMatchers("/api/v1/security/**").permitAll().anyRequest().authenticated()
//        ).addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
//        return httpSecurity.build();
//    }
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://ecommerce-angular-five.vercel.app"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/api/**", configuration);
//        return source;
//    }
//
//}
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/admin/categories/**").hasRole("ADMIN")
                .antMatchers("/api/v1/admin/products/**").hasRole("ADMIN")
                .antMatchers("/api/v1/users/**").hasRole("USER")
                .antMatchers("/api/v1/orders").permitAll()
                .antMatchers("/api/v1/orders/**").permitAll()
                .antMatchers("/api/v1/payments/success").permitAll()
                .antMatchers("/api/v1/payments/**").hasRole("USER")
                .antMatchers("/api/payments/webhook", "/confirmacion-pago").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/api/v1/home/**").permitAll()
                .antMatchers("/api/v1/security/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://ecommerce-angular-five.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
