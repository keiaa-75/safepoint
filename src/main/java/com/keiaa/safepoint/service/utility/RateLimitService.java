/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, LocalDateTime> requestTimes = new ConcurrentHashMap<>();
    private final int limitMinutes = 5; // 5 minutes between requests

    public boolean isAllowed(String ipAddress) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequest = requestTimes.get(ipAddress);
        
        if (lastRequest == null || lastRequest.plusMinutes(limitMinutes).isBefore(now)) {
            requestTimes.put(ipAddress, now);
            return true;
        }
        
        return false;
    }
}
