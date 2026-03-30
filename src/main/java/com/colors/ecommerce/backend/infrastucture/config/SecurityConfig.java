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

        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(aut -> aut

                        // PUBLICOS
                        .requestMatchers("/api/v1/orders/**").permitAll()
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .requestMatchers("/api/v1/variants/**").permitAll()
                        .requestMatchers("/api/v1/size-guides/**").permitAll()
                        .requestMatchers("/api/v1/security/**").permitAll()
                        .requestMatchers("/api/v1/home/**").permitAll()
                        .requestMatchers("/api/v1/categories/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // ADMIN
                        .requestMatchers("/api/v1/admin/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/orders/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/products/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/variants/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/size-guides/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/stock/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/import/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/tools/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/reports/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/shipments/**").hasRole("ADMIN")

                        // USER
                        .requestMatchers("/api/v1/shipments/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/payments/**").hasRole("USER")

                        // WEBHOOK / EXTERNOS
                        .requestMatchers("/api/payments/webhook", "/confirmacion-pago").permitAll()

                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(401, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(403, "Forbidden"))
                )

                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CONFIGURACION CORS PARA FRONTEND
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "https://lionsbrand.com.ar",
                "https://www.lionsbrand.com.ar",
                "https://ecommerce-angular-production.up.railway.app"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
