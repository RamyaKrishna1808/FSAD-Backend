package com.lms.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.lms.config.UploadProperties;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(UploadProperties uploadProperties) {
        this.uploadPath = uploadProperties.getUploadPath();
        createUploadDirectory();
    }

    public String storeFile(MultipartFile file, List<String> allowedFileTypes) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        if (allowedFileTypes == null || allowedFileTypes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed file types are missing for this assignment");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        if (originalName.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }

        String extension = getExtension(originalName);
        if (!isAllowed(extension, allowedFileTypes)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File type not allowed for this assignment");
        }

        try {
            createUploadDirectory();
            String storedName = UUID.randomUUID() + "_" + originalName.replaceAll("\\s+", "_");
            Path target = uploadPath.resolve(storedName).normalize();
            if (!target.startsWith(uploadPath)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
            }
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + storedName;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store file", ex);
        }
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create upload directory: " + uploadPath, ex);
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File extension is required");
        }
        return fileName.substring(dotIndex).toLowerCase();
    }

    private boolean isAllowed(String extension, List<String> allowedFileTypes) {
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        return allowedFileTypes.stream()
                .map(type -> type.trim().toLowerCase())
                .anyMatch(type -> type.equals(ext) || ("." + type).equals(extension));
    }
}
