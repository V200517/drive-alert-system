package com.yashasvi.drivealert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardResponse {
    private long totalDrives;
    private long eligibleDrives;
    private long appliedDrives;
}
