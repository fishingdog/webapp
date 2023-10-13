
//package com.example.webapp.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//
//    private static final String SECRET_KEY = "SUdfJM+l167hy/kHSczI9+iRHWAOaLBLuMfYafyYzbD4PWIgzBnDHANSsl1loutFpoi/6MMD+BQ/9KBv4OCTJ3DbWE2uz+62cRhw7XQaCq75jRpMOIgTuRXk5LGr/N5RvWyC54I+H/5TccqfZ2/kM2y5C5asDE04MAUGneH6hvfA+7DPdckNjSIXVSSSzQMXvisS7AT/xhnP72lKEPOEGkyf15dMCddJpEeqG6URGRKnkKdodHZPHwIwUW2DtCQObLyaArkTN7PP8DK75BB0qq9fjmLwbhFH5vaX1aNsLRATA6oFa1zlasOCe0qSZgTFPN06h4cJE0jA2xvR4VJidKEP3ZAvAKGSz6ds+d3WQVE=\n";
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts
//                .parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    private Key getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//
//    public String generateToken(
//            Map<String, Object> extraClaims,
//            UserDetails userDetails
//    ) {
//        return Jwts
//                .builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) //valid for 2 hours
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//
//
//    //generate token without claims
//    public String generateToken(
//            UserDetails userDetails
//    ) {
//        return generateToken(new HashMap<>(), userDetails);
//    }
//
//
//
////    public Date extractExpiration(String token) {
////        return extractClaim(token, Claims::getExpiration);
////    }
//
////
////    private Boolean isTokenExpired(String token) {
////        return extractExpiration(token).before(new Date());
////    }
////
////    public Boolean validateToken(String token, UserDetails userDetails) {
////        final String username = extractUsername(token);
////        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
////    }
//
//}

