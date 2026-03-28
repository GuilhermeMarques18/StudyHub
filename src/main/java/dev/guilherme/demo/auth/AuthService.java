package dev.guilherme.demo.auth;


import dev.guilherme.demo.auth.dtos.ChangePasswordDTO;
import dev.guilherme.demo.auth.exception.TokenInvalidException;
import dev.guilherme.demo.user.UserModel;
import dev.guilherme.demo.user.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class AuthService {

    @Value("${APP_JWT_SECRET}")
    private String jwtSecret;

    @Value("${APP_JWT_EXPIRATION}")
    private long jwtExpirationMs;

    @Value("${APP_JWT_REFRESH}")
    private long jwtRefreshMs;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        String subject = parseClaims(token).getSubject();
        if(subject == null){
            throw new TokenInvalidException("O token não contém um identificador de usuário");
        }
        return subject;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch(ExpiredJwtException e){
            throw new TokenInvalidException("Token expirado");
        } catch (JwtException | IllegalArgumentException e){
            throw new TokenInvalidException("Token invalido");
        }
    }

    public void changePassword(UserModel user, ChangePasswordDTO dto) {
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }


}
