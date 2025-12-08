/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TokenGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String generateUuidToken() {
        return UUID.randomUUID().toString();
    }
}
