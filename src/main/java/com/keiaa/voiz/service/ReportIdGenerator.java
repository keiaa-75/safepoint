/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/ *
 */

package com.keiaa.voiz.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keiaa.voiz.exception.DailyReportLimitExceededException;
import com.keiaa.voiz.repository.ReportRepository;

@Service
public class ReportIdGenerator {

    private static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    @Autowired
    private ReportRepository reportRepository;

    public String generateReportId() {
        LocalDate today = LocalDate.now();
        long dailySubmissions = reportRepository.countByTimestampBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        if (dailySubmissions >= 99) {
            throw new DailyReportLimitExceededException("The daily report submission limit has been reached. Please try again tomorrow.");
        }

        String datePart = today.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String indexPart = String.format("%02d", dailySubmissions + 1);
        String randomPart = generateRandomString(4);

        return String.format("%s-%s-%s", datePart, indexPart, randomPart);
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
