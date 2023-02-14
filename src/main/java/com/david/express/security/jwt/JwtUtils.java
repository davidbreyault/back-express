package com.david.express.security.jwt;

import com.david.express.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${express.app.JWT_SIGNING_KEY}")
    private String jwtSigningKey;

    @Value("${express.app.JWT_VALIDITY}")
    private int jwtValidation;

    @Value("${express.app.JWT_AUTHORITIES_KEY}")
    private String jwtAuthoritiesKey;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts
                .builder()
                .setSubject(userPrincipal.getUsername())
                .claim(jwtAuthoritiesKey, authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidation))
                .signWith(SignatureAlgorithm.HS512, jwtSigningKey)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isJwtTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
