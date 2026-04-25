package com.yashasvi.drivealert.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class DriveRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Role is required")
    private String role;

    @DecimalMin(value = "0.0", message = "Package must be positive")
    private Double packageAmount;

    @DecimalMin(value = "0.0", message = "Eligibility CGPA cannot be below 0")
    @DecimalMax(value = "10.0", message = "Eligibility CGPA cannot exceed 10")
    private Double eligibilityCgpa;

    private String branchEligibility;

    @Min(value = 2020, message = "Eligibility year must be between 2020 and 2030")
    @Max(value = 2030, message = "Eligibility year must be between 2020 and 2030")
    private Integer yearEligibility;

    private String applyLink;

    @FutureOrPresent(message = "Deadline cannot be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    private String additionalNotes;
    private Boolean urgent;
    private Boolean newDrive;
    private Boolean notificationReady;
    private String tags;
    private MultipartFile pdfFile;
}
