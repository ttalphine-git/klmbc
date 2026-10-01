package com.classifiedads.service;

import com.classifiedads.config.FileStorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "webp"));
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 50MB");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        String fileName = UUID.randomUUID().toString() + "." + extension;
        Path uploadPath = Paths.get(fileStorageConfig.getUploadDir(), directory);

        try {
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            log.info("File uploaded successfully: {}", filePath);
            return directory + "/" + fileName;
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new IOException("Failed to upload file", e);
        }
    }

    public byte[] downloadFile(String filePath) throws IOException {
        Path path = Paths.get(fileStorageConfig.getUploadDir(), filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readAllBytes(path);
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(fileStorageConfig.getUploadDir(), filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("File deleted: {}", path);
        }
    }
}
