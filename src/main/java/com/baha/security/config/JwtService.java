package com.baha.security.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    final static String SECRET_KEY = "a252e69dd75dcfd36c00b561380d8e32109f17e079a6d1e3969c89db98b68a4a062ec5aae51157201f88cd668a430d56258267bc5fcdcec93b5098380b13f722d01ae2742b7148fd5bb7d682a1b4afe0013a416d798ea184530c1c443016e153c3b94bd7a616f87c071a42a7285966b4718387216479f04ce0d5c769addbb54596966162c451b6c6480fc58132d48b2ee3e3d854bb27ea34d86d21d72840a98b428fa5a98a4594af4f90caf6725bfd16a3467aec389000309e14762ca165b009f6982c8354a389c0ae29ac5a1d5f086cc631065c7e4e4399e2cfbc586d9e35243f8bc5797a5be7d1c97438b84b2488298b340fabba4236bac23d09d36455babb";

    public String extractUserName(String token) {
        return extractClaim(token ,Claims::getSubject); // This is the advantage of generic methods
    }

    public <T> T extractClaim(String token ,Function<Claims ,T> claimsResolver) { // This is a generic method for handling multiple types
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); // This would apply the function passed in param on the body of the JWT token to extract the needed attribute
    }

    public String generateToken(UserDetails userDetails) { // This is used to generate a token without any extra claims
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(
        Map<String ,Object> extraClaims,
        UserDetails userDetails
    ) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
            .signWith(getSignInKey() ,SignatureAlgorithm.HS256)
            .compact(); // This is used to convert to String            
    }

    public boolean isTokenValid(String token ,UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token ,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); // This is used to create a HMAC-SHA key from our secret for JWT signing/verification
    }
}
