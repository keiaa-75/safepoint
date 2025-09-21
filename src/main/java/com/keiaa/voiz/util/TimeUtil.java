
package com.keiaa.voiz.util;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component("timeUtil")
public class TimeUtil {

    public String timeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "Submitted just now";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return "Submitted " + minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return "Submitted " + hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (seconds < 2592000) { // 30 days
            long days = seconds / 86400;
            if (days == 1) {
                return "Submitted yesterday";
            }
            return "Submitted " + days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (seconds < 31536000) { // 365 days
            long months = seconds / 2592000;
            return "Submitted " + months + " month" + (months > 1 ? "s" : "") + " ago";
        } else {
            long years = seconds / 31536000;
            return "Submitted " + years + " year" + (years > 1 ? "s" : "") + " ago";
        }
    }

    public boolean isNew(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        // Consider "new" if submitted within the last 24 hours
        return Duration.between(dateTime, LocalDateTime.now()).toHours() < 24;
    }
}
