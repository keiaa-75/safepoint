/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class FileLoaderService {
    private final Path root = Paths.get("data/uploads").toAbsolutePath().normalize();
    
    @Autowired
    private FileValidationService fileValidationService;

    /**
     * Loads a file as a Spring Resource object.
     *
     * @param filename The name of the file to load.
     * @return The Resource object for the requested file.
     * @throws RuntimeException if the file cannot be read or found.
     */
    public Resource load(String filename) {
        try {
            if (!fileValidationService.isValidFilename(filename)) {
                throw new RuntimeException("Invalid filename!");
            }

            Path file = root.resolve(filename).normalize();

            if (!file.getParent().equals(root)) {
                throw new RuntimeException("Cannot read file outside current directory.");
            }

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}