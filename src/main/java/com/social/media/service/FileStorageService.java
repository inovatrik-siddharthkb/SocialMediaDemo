package com.social.media.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileStorageService {
	private final String uploadDir = "uploads/";

    public String store(MultipartFile file) {

        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + filename);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }

        return filename;
    }

}
