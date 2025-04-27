package com.loan.auth.controller;

import com.loan.auth.Model.User;
import com.loan.auth.payload.JwtResponse;
import com.loan.auth.payload.LoginRequest;
import com.loan.auth.payload.SignupRequest;
import com.loan.auth.service.UserService;
import com.loan.auth.util.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, 
                         JwtTokenUtil jwtTokenUtil, 
                         UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        // Set ADMIN role if username is 'admin', otherwise CUSTOMER
        String role = "admin".equalsIgnoreCase(signUpRequest.getUsername()) 
            ? "ADMIN" 
            : "CUSTOMER";
        
        User user = new User(signUpRequest.getUsername(), 
                            signUpRequest.getPassword(), 
                            role);
        userService.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}