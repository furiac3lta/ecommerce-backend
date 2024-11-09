package com.colors.ecommerce.backend.infrastucture.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.colors.ecommerce.backend.infrastucture.jwt.Constants.*;

@Service
public class JWTGenerator {

public String getToken(String username) {
    List<GrantedAuthority> authorityList = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream().collect(Collectors.toList());

    String token = Jwts.builder()
            .setId("ecommerce")
            .setSubject(username)
            .claim("authorities", authorityList.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
            .signWith(getSignedKey(SUPER_SECRET_KEY), SignatureAlgorithm.HS512).compact();

    return TOKEN_BEARER_PREFIX + token;
}

}
