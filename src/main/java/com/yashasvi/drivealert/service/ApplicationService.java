package com.yashasvi.drivealert.service;

import com.yashasvi.drivealert.dto.ApplicationResponse;
import com.yashasvi.drivealert.entity.ApplicationEntity;
import com.yashasvi.drivealert.entity.ApplicationStatus;
import com.yashasvi.drivealert.entity.Drive;
import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DriveService driveService;

    public ApplicationService(ApplicationRepository applicationRepository, DriveService driveService) {
        this.applicationRepository = applicationRepository;
        this.driveService = driveService;
    }

    public ApplicationEntity markAsApplied(User user, Long driveId) {
        Drive drive = driveService.getDriveById(driveId);
        ApplicationEntity application = applicationRepository.findByUserAndDrive(user, drive)
                .orElse(ApplicationEntity.builder()
                        .user(user)
                        .drive(drive)
                        .build());

        application.setStatus(ApplicationStatus.APPLIED);
        return applicationRepository.save(application);
    }

    public List<ApplicationResponse> getApplicationsForUser(User user) {
        return applicationRepository.findByUser(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countAppliedApplications(User user) {
        return applicationRepository.findByUser(user).stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPLIED)
                .count();
    }

    public ApplicationResponse toResponse(ApplicationEntity application) {
        Drive drive = application.getDrive();
        return new ApplicationResponse(
                application.getId(),
                drive.getId(),
                drive.getCompanyName(),
                drive.getRole(),
                drive.getPackageAmount(),
                application.getStatus(),
                drive.getDeadline()
        );
    }
}
