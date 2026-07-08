/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.model.enums;

/**
 * The five options a student can pick when rating their SafePoint experience.
 * Display label, icon, and numeric score all live here so the Thymeleaf
 * fragment can render the options straight from this enum instead of
 * duplicating the copy in HTML.
 */
public enum FeedbackRating {
    POOR("Poor", "bi-emoji-angry", 1),
    BAD("Bad", "bi-emoji-frown", 2),
    AVERAGE("Average", "bi-emoji-neutral", 3),
    GOOD("Good", "bi-emoji-smile", 4),
    EXCELLENT("Excellent", "bi-emoji-laughing", 5);

    private final String displayName;
    private final String iconClass;
    private final int score;

    FeedbackRating(String displayName, String iconClass, int score) {
        this.displayName = displayName;
        this.iconClass = iconClass;
        this.score = score;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconClass() {
        return iconClass;
    }

    public int getScore() {
        return score;
    }
}
