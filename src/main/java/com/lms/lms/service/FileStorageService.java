package com.lms.lms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public String store(MultipartFile file, String subDir) throws IOException {
        String dir = uploadDir + "/" + subDir;
        Path dirPath = Paths.get(dir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + extension;

        Path filePath = dirPath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return subDir + "/" + storedName;
    }

    public Path load(String filePath) {
        return Paths.get(uploadDir).resolve(filePath);
    }

    public void delete(String filePath) throws IOException {
        Path path = Paths.get(uploadDir).resolve(filePath);
        Files.deleteIfExists(path);
    }
}
