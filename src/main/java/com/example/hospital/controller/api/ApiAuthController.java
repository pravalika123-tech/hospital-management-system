package com.example.hospital.controller.api;

import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.hospital.config.CustomUserDetailsService;
import com.example.hospital.dto.LoginRequest;
import com.example.hospital.dto.LoginResponse;
import com.example.hospital.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Obtain and inspect JWT tokens")
public class ApiAuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public ApiAuthController(AuthenticationManager authenticationManager,
                             CustomUserDetailsService userDetailsService,
                             JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Log in and receive a JWT access token")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("");
        return new LoginResponse(jwtService.generateToken(userDetails), "Bearer",
                userDetails.getUsername(), role);
    }

    @GetMapping("/me")
    @Operation(summary = "Inspect the token of the current user")
    public Map<String, Object> me(UserDetails userDetails) {
        return Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}
