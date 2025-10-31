/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RateLimitingService {

    private final Map<String, Long> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockTimestamps = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 5;
    private static final long BLOCK_DURATION_MS = 600000;

    public boolean isBlocked(String key) {
        Long blockTimestamp = blockTimestamps.get(key);
        if (blockTimestamp != null && System.currentTimeMillis() - blockTimestamp < BLOCK_DURATION_MS) {
            return true;
        }
        blockTimestamps.remove(key);
        return false;
    }

    public void incrementRequestCount(String key) {
        long newCount = requestCounts.merge(key, 1L, Long::sum);
        if (newCount >= MAX_REQUESTS) {
            blockTimestamps.put(key, System.currentTimeMillis());
            requestCounts.remove(key);
        }
    }
}
