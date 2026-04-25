package com.yashasvi.drivealert.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yashasvi.drivealert.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationResponse {
    private Long applicationId;
    private Long driveId;
    private String companyName;
    private String role;
    private Double packageAmount;
    private ApplicationStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
}
