package com.yashasvi.drivealert.controller;

import com.yashasvi.drivealert.dto.ApplicationResponse;
import com.yashasvi.drivealert.dto.DashboardResponse;
import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.service.ApplicationService;
import com.yashasvi.drivealert.service.DriveService;
import com.yashasvi.drivealert.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final DriveService driveService;
    private final UserService userService;

    public ApplicationController(ApplicationService applicationService,
                                 DriveService driveService,
                                 UserService userService) {
        this.applicationService = applicationService;
        this.driveService = driveService;
        this.userService = userService;
    }

    @PostMapping("/{driveId}/apply")
    public ResponseEntity<ApplicationResponse> markApplied(@PathVariable Long driveId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ApplicationResponse response = applicationService.toResponse(applicationService.markAsApplied(user, driveId));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getUserApplications(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(applicationService.getApplicationsForUser(user));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        long totalDrives = driveService.getAllDrives().size();
        long eligibleDrives = driveService.getEligibleDrivesForUser(user).size();
        long appliedDrives = applicationService.countAppliedApplications(user);
        return ResponseEntity.ok(new DashboardResponse(totalDrives, eligibleDrives, appliedDrives));
    }
}
