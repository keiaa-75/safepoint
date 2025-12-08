/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.keiaa.safepoint.service.EmailVerificationService;
import com.keiaa.safepoint.service.PasswordResetService;

@Component
public class ScheduledTasks {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Scheduled(cron = "0 0 * * * ?") // Run every hour
    public void cleanupExpiredTokens() {
        passwordResetService.cleanupExpiredTokens();
        emailVerificationService.purgeExpiredTokens();
    }
}
