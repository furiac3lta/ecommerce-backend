package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.infrastucture.dto.JWTClient;
import com.colors.ecommerce.backend.infrastucture.dto.UserDTO;
import com.colors.ecommerce.backend.infrastucture.jwt.JWTGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/security")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "https://ecommerce-angular-five.vercel.app"})
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public LoginController(AuthenticationManager authenticationManager, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }
    @PostMapping("/login")

    public ResponseEntity<JWTClient> login(@RequestBody UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Rol de user: {}",SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get().toString());
        String token = jwtGenerator.getToken(userDTO.username());
        JWTClient jwtClient = new JWTClient(token);
        return new ResponseEntity<>(jwtClient, HttpStatus.OK);
    }
}
