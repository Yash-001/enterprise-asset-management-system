package com.yashconsulting.eams.document.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements StorageService {

    private final Path rootLocation;

    public LocalFileStorageService(@Value("${eams.storage.local.path:./uploads}") String storagePath) {
        this.rootLocation = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + this.rootLocation, e);
        }
    }

    @Override
    public String store(MultipartFile file, String referenceType, Long referenceId) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Cannot store empty file");
            }

            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String storedFileName = UUID.randomUUID() + extension;
            String relativePath = referenceType.toLowerCase() + "/" + referenceId + "/" + storedFileName;

            Path destinationDir = this.rootLocation.resolve(referenceType.toLowerCase() + "/" + referenceId);
            Files.createDirectories(destinationDir);

            Path destinationFile = this.rootLocation.resolve(relativePath).normalize();

            // Security check: ensure path is within root
            if (!destinationFile.startsWith(this.rootLocation)) {
                throw new SecurityException("Cannot store file outside storage directory");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: {} at path: {}", originalFileName, relativePath);

            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Resource load(String storagePath) {
        try {
            Path filePath = this.rootLocation.resolve(storagePath).normalize();

            if (!filePath.startsWith(this.rootLocation)) {
                throw new SecurityException("Cannot access file outside storage directory");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + storagePath);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + storagePath, e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Path filePath = this.rootLocation.resolve(storagePath).normalize();

            if (!filePath.startsWith(this.rootLocation)) {
                throw new SecurityException("Cannot delete file outside storage directory");
            }

            Files.deleteIfExists(filePath);
            log.info("Deleted file at path: {}", storagePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", storagePath, e);
            throw new RuntimeException("Failed to delete file: " + storagePath, e);
        }
    }
}
