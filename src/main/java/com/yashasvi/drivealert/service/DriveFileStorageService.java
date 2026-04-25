package com.yashasvi.drivealert.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class DriveFileStorageService {

    private final Path storageDirectory;

    public DriveFileStorageService(@Value("${app.storage.drive-pdf-dir}") String storageDirectory) {
        this.storageDirectory = Paths.get(storageDirectory).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not initialize drive PDF storage", ex);
        }
    }

    public StoredFile storePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "drive.pdf"));
        if (!originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        String storedFilename = UUID.randomUUID() + "-" + originalFilename.replaceAll("\\s+", "_");
        Path target = storageDirectory.resolve(storedFilename).normalize();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            return new StoredFile(originalFilename, storedFilename);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store PDF file", ex);
        }
    }

    public Resource loadAsResource(String storedFilename) {
        if (!StringUtils.hasText(storedFilename)) {
            throw new IllegalArgumentException("PDF not found for this drive");
        }

        try {
            Path filePath = storageDirectory.resolve(storedFilename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("PDF not found for this drive");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("PDF not found for this drive", ex);
        }
    }

    public void deleteIfExists(String storedFilename) {
        if (!StringUtils.hasText(storedFilename)) {
            return;
        }

        try {
            Files.deleteIfExists(storageDirectory.resolve(storedFilename).normalize());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete stored PDF file", ex);
        }
    }

    public record StoredFile(String originalFilename, String storedFilename) {
    }
}
