
package com.colors.ecommerce.backend.infrastucture.jwt;

import com.colors.ecommerce.backend.infrastucture.service.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.colors.ecommerce.backend.infrastucture.jwt.JWTValidate.*;

@Component
@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final CustomUserDetailService customUserDetailService;

    public JWTAuthorizationFilter(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Verificamos si el token existe en la solicitud
            if (tokenExists(request, response)) {
                Claims claims = JWTValid(request);

                // Verificamos si el JWT contiene el atributo "authorities" para roles
                if (claims.get("authorities") != null) {
                    setAuthentication(claims, customUserDetailService);

                    // Log para confirmar que la autenticación fue establecida
                    log.info("User authenticated with roles: {}",
                            SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    // Limpiamos el contexto si no hay autoridades
                    SecurityContextHolder.clearContext();
                }
            } else {
                // Limpiamos el contexto si no hay token
                SecurityContextHolder.clearContext();
            }

            // Pasamos la solicitud al siguiente filtro en la cadena
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            // Establecemos el código de estado 403 si hay un problema con el token
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }
}
