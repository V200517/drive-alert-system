package com.yashasvi.drivealert.controller;

import com.yashasvi.drivealert.dto.AuthRequest;
import com.yashasvi.drivealert.dto.AuthResponse;
import com.yashasvi.drivealert.dto.AdminRegisterRequest;
import com.yashasvi.drivealert.dto.RegisterRequest;
import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.security.JwtUtil;
import com.yashasvi.drivealert.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User saved = userService.registerUser(buildUser(request.getName(), request.getEmail(), request.getPassword(),
                request.getCourse(), request.getCgpa(), request.getYear(), request.getSkills()));
        String token = jwtUtil.generateToken(saved.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, saved.getEmail(), saved.getName(), saved.getRole()));
    }

    @PostMapping("/admin/register")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        User saved = userService.registerAdminUser(
                buildUser(request.getName(), request.getEmail(), request.getPassword(),
                        request.getCourse(), request.getCgpa(), request.getYear(), request.getSkills()),
                request.getAdminAccessKey()
        );
        String token = jwtUtil.generateToken(saved.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, saved.getEmail(), saved.getName(), saved.getRole()));
    }

    private User buildUser(String name, String email, String password, String course, Double cgpa, Integer year, String skills) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .course(course)
                .cgpa(cgpa)
                .year(year)
                .skills(skills)
                .build();
        return user;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getName(), user.getRole()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).build();
        }
    }
}
