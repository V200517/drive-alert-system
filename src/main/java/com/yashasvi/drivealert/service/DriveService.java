package com.yashasvi.drivealert.service;

import com.yashasvi.drivealert.dto.DriveRequest;
import com.yashasvi.drivealert.dto.DriveResponse;
import com.yashasvi.drivealert.entity.Drive;
import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.repository.DriveRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DriveService {

    private final DriveRepository driveRepository;
    private final DriveFileStorageService driveFileStorageService;

    public DriveService(DriveRepository driveRepository, DriveFileStorageService driveFileStorageService) {
        this.driveRepository = driveRepository;
        this.driveFileStorageService = driveFileStorageService;
    }

    public Drive createDrive(DriveRequest request) {
        validateDriveRequest(request);
        Drive drive = Drive.builder().build();
        applyDriveChanges(drive, request);
        return driveRepository.save(drive);
    }

    public Drive updateDrive(Long id, DriveRequest request) {
        validateDriveRequest(request);
        Drive drive = getDriveById(id);
        applyDriveChanges(drive, request);
        return driveRepository.save(drive);
    }

    public void deleteDrive(Long id) {
        Drive drive = getDriveById(id);
        driveFileStorageService.deleteIfExists(drive.getPdfStoragePath());
        driveRepository.delete(drive);
    }

    public Drive getDriveById(Long id) {
        return driveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Drive not found with id: " + id));
    }

    public Resource getDrivePdf(Long id) {
        Drive drive = getDriveById(id);
        return driveFileStorageService.loadAsResource(drive.getPdfStoragePath());
    }

    public List<DriveResponse> getAllDrives(String roleFilter, Double minPackage, Double maxPackage, Double minCgpa) {
        return driveRepository.findAll().stream()
                .filter(drive -> roleFilter == null || roleFilter.isBlank() || drive.getRole().toLowerCase().contains(roleFilter.toLowerCase()))
                .filter(drive -> minPackage == null || drive.getPackageAmount() != null && drive.getPackageAmount() >= minPackage)
                .filter(drive -> maxPackage == null || drive.getPackageAmount() != null && drive.getPackageAmount() <= maxPackage)
                .filter(drive -> minCgpa == null || drive.getEligibilityCgpa() == null || drive.getEligibilityCgpa() <= minCgpa)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DriveResponse> getEligibleDrivesForUser(User user) {
        return driveRepository.findAll().stream()
                .filter(drive -> isEligible(user, drive))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public boolean isEligible(User user, Drive drive) {
        boolean cgpaOk = drive.getEligibilityCgpa() == null || user.getCgpa() == null || user.getCgpa() >= drive.getEligibilityCgpa();
        boolean branchOk = drive.getBranchEligibility() == null || drive.getBranchEligibility().isBlank()
                || (user.getCourse() != null && user.getCourse().toLowerCase().contains(drive.getBranchEligibility().toLowerCase()));
        boolean yearOk = drive.getYearEligibility() == null || user.getYear() == null || user.getYear().equals(drive.getYearEligibility());
        return cgpaOk && branchOk && yearOk;
    }

    public List<DriveResponse> getAllDrives() {
        return driveRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public DriveResponse toResponse(Drive drive) {
        return new DriveResponse(
                drive.getId(),
                drive.getCompanyName(),
                drive.getRole(),
                drive.getPackageAmount(),
                drive.getEligibilityCgpa(),
                drive.getBranchEligibility(),
                drive.getYearEligibility(),
                drive.getApplyLink(),
                drive.getDeadline(),
                drive.getAdditionalNotes(),
                drive.getUrgent(),
                drive.getNewDrive(),
                drive.getNotificationReady(),
                drive.getTags(),
                drive.getPdfFileName(),
                drive.getPdfStoragePath() == null ? null : "/api/drives/" + drive.getId() + "/pdf",
                drive.getCreatedAt()
        );
    }

    private void applyDriveChanges(Drive drive, DriveRequest request) {
        drive.setCompanyName(request.getCompanyName().trim());
        drive.setRole(request.getRole().trim());
        drive.setPackageAmount(request.getPackageAmount());
        drive.setEligibilityCgpa(request.getEligibilityCgpa());
        drive.setBranchEligibility(trimToNull(request.getBranchEligibility()));
        drive.setYearEligibility(request.getYearEligibility());
        drive.setApplyLink(trimToNull(request.getApplyLink()));
        drive.setDeadline(request.getDeadline());
        drive.setAdditionalNotes(trimToNull(request.getAdditionalNotes()));
        drive.setUrgent(Boolean.TRUE.equals(request.getUrgent()));
        drive.setNewDrive(request.getNewDrive() == null || request.getNewDrive());
        drive.setNotificationReady(request.getNotificationReady() == null || request.getNotificationReady());
        drive.setTags(parseTags(request.getTags()));

        if (request.getPdfFile() != null && !request.getPdfFile().isEmpty()) {
            driveFileStorageService.deleteIfExists(drive.getPdfStoragePath());
            DriveFileStorageService.StoredFile storedFile = driveFileStorageService.storePdf(request.getPdfFile());
            drive.setPdfFileName(storedFile.originalFilename());
            drive.setPdfStoragePath(storedFile.storedFilename());
        }
    }

    private void validateDriveRequest(DriveRequest request) {
        if (request.getDeadline() != null && request.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }

        if (StringUtils.hasText(request.getApplyLink())
                && !(request.getApplyLink().startsWith("http://") || request.getApplyLink().startsWith("https://"))) {
            throw new IllegalArgumentException("Apply link must start with http:// or https://");
        }
    }

    private Set<String> parseTags(String rawTags) {
        if (!StringUtils.hasText(rawTags)) {
            return new LinkedHashSet<>();
        }

        return StringUtils.commaDelimitedListToSet(rawTags).stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
