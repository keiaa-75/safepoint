/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.config;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:mime-types.properties")
@ConfigurationProperties(prefix = "mime-types")
public class MimeTypeProperties {
    private Set<String> allowed = new HashSet<>();
    private Map<String, String> extensions = new HashMap<>();

    public Set<String> getAllowed() {
        return allowed;
    }

    public void setAllowed(Set<String> allowed) {
        this.allowed = allowed;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }
}