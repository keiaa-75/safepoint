package com.keiaa.voiz.exception;

public class DailyReportLimitExceededException extends RuntimeException {
    public DailyReportLimitExceededException(String message) {
        super(message);
    }
}
