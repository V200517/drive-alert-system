package com.yashasvi.drivealert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String course;
    private Double cgpa;
    private Integer year;
    private String skills;
    private String role;
}
