package com.yashasvi.drivealert.controller;

import com.yashasvi.drivealert.dto.DriveRequest;
import com.yashasvi.drivealert.dto.DriveResponse;
import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.service.DriveService;
import com.yashasvi.drivealert.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/drives")
public class DriveController {

    private final DriveService driveService;
    private final UserService userService;

    public DriveController(DriveService driveService, UserService userService) {
        this.driveService = driveService;
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriveResponse> createDrive(@Valid @ModelAttribute DriveRequest request) {
        return ResponseEntity.ok(driveService.toResponse(driveService.createDrive(request)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriveResponse> updateDrive(@PathVariable Long id, @Valid @ModelAttribute DriveRequest request) {
        return ResponseEntity.ok(driveService.toResponse(driveService.updateDrive(id, request)));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDrive(@PathVariable Long id) {
        driveService.deleteDrive(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DriveResponse>> getAllDrives(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Double minPackage,
            @RequestParam(required = false) Double maxPackage,
            @RequestParam(required = false) Double minCgpa) {
        return ResponseEntity.ok(driveService.getAllDrives(role, minPackage, maxPackage, minCgpa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriveResponse> getDrive(@PathVariable Long id) {
        return ResponseEntity.ok(driveService.toResponse(driveService.getDriveById(id)));
    }

    @GetMapping("/eligible")
    public ResponseEntity<List<DriveResponse>> getEligibleDrives(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(driveService.getEligibleDrivesForUser(user));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadDrivePdf(@PathVariable Long id) {
        DriveResponse drive = driveService.toResponse(driveService.getDriveById(id));
        Resource resource = driveService.getDrivePdf(id);
        String filename = drive.getPdfFileName() == null ? "drive-details.pdf" : drive.getPdfFileName();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename).build().toString())
                .body(resource);
    }
}
