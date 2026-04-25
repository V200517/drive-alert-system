package com.yashasvi.drivealert.controller;

import com.yashasvi.drivealert.dto.UserProfileResponse;
import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCourse(),
                user.getCgpa(),
                user.getYear(),
                user.getSkills(),
                user.getRole()
        );
        return ResponseEntity.ok(response);
    }
}
