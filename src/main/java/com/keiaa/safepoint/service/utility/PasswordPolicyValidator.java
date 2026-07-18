/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Single source of truth for the account password policy, used everywhere a
 * raw (not-yet-hashed) password needs checking: student signup, admin
 * creation, and password reset. Keeping it in one place stops these flows
 * from drifting out of sync with each other.
 */
@Component
public class PasswordPolicyValidator {

    private static final int MIN_LENGTH = 8;
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SYMBOL = Pattern.compile("[^A-Za-z0-9]");

    /**
     * @return null if the password satisfies the policy, otherwise a
     *         user-facing message describing the first unmet requirement.
     */
    public String validate(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return "Password must be at least " + MIN_LENGTH + " characters long.";
        }
        if (!LOWERCASE.matcher(password).find()) {
            return "Password must include at least one lowercase letter.";
        }
        if (!UPPERCASE.matcher(password).find()) {
            return "Password must include at least one uppercase letter.";
        }
        if (!DIGIT.matcher(password).find()) {
            return "Password must include at least one number.";
        }
        if (!SYMBOL.matcher(password).find()) {
            return "Password must include at least one symbol.";
        }
        return null;
    }

    public boolean isValid(String password) {
        return validate(password) == null;
    }
}
