package com.yashasvi.drivealert.repository;

import com.yashasvi.drivealert.entity.Drive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriveRepository extends JpaRepository<Drive, Long> {
}
