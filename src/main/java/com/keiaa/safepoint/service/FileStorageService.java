/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    private final Path root = Paths.get("data/uploads");

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
            String originalFilenameRaw = file.getOriginalFilename();
            String originalFilename = StringUtils.cleanPath(originalFilenameRaw != null ? originalFilenameRaw : "unknown");
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Files.copy(file.getInputStream(), this.root.resolve(uniqueFilename));
            return uniqueFilename;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    /**
     * Loads a file as a Spring Resource object.
     *
     * @param filename The name of the file to load.
     * @return The Resource object for the requested file.
     * @throws RuntimeException if the file cannot be read or found.
     */
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    /**
     * Loads all files currently stored in the upload directory.
     *
     * @return A Stream of Path objects representing the files in the upload directory.
     * @throws RuntimeException if the files could not be loaded.
     */
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
