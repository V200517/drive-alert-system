package com.yashasvi.drivealert.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "DRIVES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String role;

    private Double packageAmount;

    private Double eligibilityCgpa;

    private String branchEligibility;

    private Integer yearEligibility;

    private String applyLink;

    private LocalDate deadline;

    @Column(length = 2000)
    private String additionalNotes;

    @Builder.Default
    private Boolean urgent = false;

    @Column(name = "is_new")
    @Builder.Default
    private Boolean newDrive = true;

    @Builder.Default
    private Boolean notificationReady = true;

    private String pdfFileName;

    private String pdfStoragePath;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DRIVE_TAGS", joinColumns = @JoinColumn(name = "drive_id"))
    @Column(name = "tag")
    @Builder.Default
    private Set<String> tags = new LinkedHashSet<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
