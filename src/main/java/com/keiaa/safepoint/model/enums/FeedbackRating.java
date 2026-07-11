/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.model.enums;

/**
 * The five options a student can pick when rating their SafePoint experience,
 * shown as a 1-5 heart widget (score == number of filled hearts). Display
 * label and score live here so the Thymeleaf fragment can render the
 * options straight from this enum instead of duplicating the copy in HTML.
 */
public enum FeedbackRating {
    POOR("Poor", 1),
    BAD("Bad", 2),
    AVERAGE("Average", 3),
    GOOD("Good", 4),
    EXCELLENT("Excellent", 5);

    private final String displayName;
    private final int score;

    FeedbackRating(String displayName, int score) {
        this.displayName = displayName;
        this.score = score;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getScore() {
        return score;
    }
}
