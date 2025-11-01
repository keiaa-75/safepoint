/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path root = Paths.get("data/uploads").toAbsolutePath().normalize();
    
    @Autowired
    private FileValidationService fileValidationService;

    /**
     * Initializes the file storage service by creating the root directory for uploads if it does not already exist.
     * This method is called automatically after the bean's properties are set.
     * @throws RuntimeException if the directory cannot be initialized.
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    /**
     * Stores a given MultipartFile in the designated upload directory.
     * Generates a unique filename to prevent collisions.
     *
     * @param file The MultipartFile to store.
     * @return The unique filename under which the file was stored.
     * @throws RuntimeException if the file could not be stored.
     */
    public String store(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            if (!fileValidationService.isValidFilename(file.getOriginalFilename())) {
                throw new RuntimeException("Invalid filename.");
            }

            String mimeType = fileValidationService.detectMimeType(file);
            if (!fileValidationService.isValidImageFile(file)) {
                throw new RuntimeException("Only image files are allowed!");
            }

            String extension = fileValidationService.getExtensionFromMimeType(mimeType);
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path destinationFile = this.root.resolve(uniqueFilename).normalize();

            if (!destinationFile.getParent().equals(this.root)) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            Files.copy(file.getInputStream(), destinationFile);
            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file. Error: " + e.getMessage());
        }
    }
}
