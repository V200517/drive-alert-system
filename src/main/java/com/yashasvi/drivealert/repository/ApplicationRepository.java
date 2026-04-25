package com.yashasvi.drivealert.repository;

import com.yashasvi.drivealert.entity.ApplicationEntity;
import com.yashasvi.drivealert.entity.Drive;
import com.yashasvi.drivealert.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    Optional<ApplicationEntity> findByUserAndDrive(User user, Drive drive);
    List<ApplicationEntity> findByUser(User user);
}
