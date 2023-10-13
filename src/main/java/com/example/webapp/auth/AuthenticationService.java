
//package com.example.webapp.auth;
//
//import com.example.webapp.repository.UserRepository;
//import com.example.webapp.util.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PostMapping;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//
//
////    @PostMapping("/register")
////    public AuthenticationResponse register(RegisterRequest request) {
////        var user = User.builder()
////                .firstName (request.getFirstName())
////                .lastName(request.getLastName())
////                .email(request.getEmail()z)
////                .password(passwordEncoder.encode(request.getPassword()))
////                .role(Role.USER)
////                .build();
////        userRepository.save(user);
////
////        var jwtToken = jwtService.generateToken(user);
////        return AuthenticationResponse.builder()
////                .token(jwtToken)
////                .build();
////    }
//    @PostMapping("/authenticate")
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User with email " + request.getEmail() + " not found"));
