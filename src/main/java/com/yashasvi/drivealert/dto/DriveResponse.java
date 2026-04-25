package com.yashasvi.drivealert.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class DriveResponse {
    private Long id;
    private String companyName;
    private String role;
    private Double packageAmount;
    private Double eligibilityCgpa;
    private String branchEligibility;
    private Integer yearEligibility;
    private String applyLink;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private String additionalNotes;
    private Boolean urgent;
    private Boolean newDrive;
    private Boolean notificationReady;
    private Set<String> tags;
    private String pdfFileName;
    private String pdfDownloadUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
