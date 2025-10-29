/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

import java.io.IOException;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.keiaa.safepoint.config.MimeTypeProperties;

@Service
public class FileValidationService {
    private final Tika tika = new Tika();
    
    @Autowired
    private MimeTypeProperties mimeTypeProperties;

    public boolean isValidImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String detectedType = tika.detect(file.getInputStream());
        return mimeTypeProperties.getAllowed().contains(detectedType);
    }

    public String getExtensionFromMimeType(String mimeType) {
        return mimeTypeProperties.getExtensions().getOrDefault(mimeType, ".jpg");
    }

    public boolean isValidFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        return !filename.contains("..") && 
               filename.matches("^[a-zA-Z0-9._-]+$");
    }

    public String detectMimeType(MultipartFile file) throws IOException {
        return tika.detect(file.getInputStream());
    }
}