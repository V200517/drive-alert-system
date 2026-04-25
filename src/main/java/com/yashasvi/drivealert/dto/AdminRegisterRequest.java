package com.yashasvi.drivealert.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String course;
    private Double cgpa;

    @Min(value = 2020, message = "Year must be between 2020 and 2030")
    @Max(value = 2030, message = "Year must be between 2020 and 2030")
    private Integer year;

    private String skills;

    @NotBlank(message = "Admin access key is required")
    private String adminAccessKey;
}
