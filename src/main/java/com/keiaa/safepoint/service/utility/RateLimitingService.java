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
public class RateLimitingService {

    private final ConcurrentHashMap<String, LocalDateTime> lastRequestTimes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> requestCounts = new ConcurrentHashMap<>();

    public boolean isAllowedForPasswordReset(String key) {
        return isAllowedWithInterval(key, 5); // 5 minutes between password reset requests
    }

    public boolean isAllowedForReports(String key) {
        LocalDateTime now = LocalDateTime.now();
        Integer count = requestCounts.get(key);
        LocalDateTime lastRequest = lastRequestTimes.get(key);

        // Reset count if more than 10 minutes passed
        if (lastRequest == null || lastRequest.plusMinutes(10).isBefore(now)) {
            requestCounts.put(key, 1);
            lastRequestTimes.put(key, now);
            return true;
        }

        if (count == null || count < 5) {
            requestCounts.put(key, count == null ? 1 : count + 1);
            lastRequestTimes.put(key, now);
            return true;
        }

        return false; // Blocked for 10 minutes after 5 requests
    }

    private boolean isAllowedWithInterval(String key, int intervalMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequest = lastRequestTimes.get(key);
        
        if (lastRequest == null || lastRequest.plusMinutes(intervalMinutes).isBefore(now)) {
            lastRequestTimes.put(key, now);
            return true;
        }
        
        return false;
    }
}
