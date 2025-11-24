package com.social.media.service;

import java.io.IOException;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path storagePath;

    public FileStorageService(@Value("${file.upload-dir}") String path) {
        this.storagePath = Paths.get(path);

        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage folder: " + e.getMessage());
        }
    }

    public String store(MultipartFile file) {
        try {
            String original = file.getOriginalFilename();
            String ext = "";

            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String filename = "file_" + System.currentTimeMillis() + ext;

            Path target = storagePath.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        }
        catch (Exception e) {
            throw new RuntimeException("Could not store file: " + e.getMessage());
        }
    }

    public byte[] loadFile(String filename) {
        try {
            Path file = storagePath.resolve(filename);
            return Files.readAllBytes(file);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not read file: " + e.getMessage());
        }
    }
}
