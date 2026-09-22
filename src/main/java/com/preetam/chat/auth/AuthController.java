package com.preetam.chat.auth;

import com.preetam.chat.security.JwtService;
import com.preetam.chat.user.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    record AuthRequest(String username, String password) {}
    record AuthResponse(String token, String username) {}

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (req.username() == null || req.password() == null ||
            req.username().isBlank() || req.password().length() < 6)
            return ResponseEntity.badRequest().body("Username required and password must be >= 6 characters.");
        if (users.existsByUsername(req.username()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        users.save(new AppUser(req.username(), encoder.encode(req.password())));
        return ResponseEntity.ok(new AuthResponse(jwt.generate(req.username()), req.username()));
    }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody AuthRequest req) {
        return users.findByUsername(req.username())
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(new AuthResponse(jwt.generate(u.getUsername()), u.getUsername())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials."));
    }
}
