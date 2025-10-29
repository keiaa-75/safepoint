/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.keiaa.safepoint.exception.DailyReportLimitExceededException;
import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.repository.ReportRepository;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ReportIdGenerator reportIdGenerator;

    /**
     * Submits a new report with associated files
     * 
     * @param report The report to submit
     * @param files The evidence files to store with the report
     * @return The saved report with generated ID and file paths
     * @throws DailyReportLimitExceededException If the daily limit for reports is exceeded
     */
    public Report submitReport(Report report, MultipartFile[] files) throws DailyReportLimitExceededException {
        // Generate report ID
        report.setReportId(reportIdGenerator.generateReportId());

        // Store evidence files
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = fileStorageService.store(file);
                fileNames.add(fileName);
            }
        }
        report.setEvidenceFilePaths(fileNames);

        // Save the report to the database
        Report savedReport = reportRepository.save(report);

        // Send confirmation email if an email address is provided
        if (report.getEmail() != null && !report.getEmail().isEmpty()) {
            emailService.sendReportConfirmation(savedReport);
        }

        return savedReport;
    }

    /**
     * Finds a report by its unique report ID
     * 
     * @param reportId The unique report ID to search for
     * @return Optional containing the report if found, empty otherwise
     */
    public Optional<Report> findReportByReportId(String reportId) {
        return reportRepository.findByReportId(reportId);
    }

    /**
     * Finds all reports in the system
     * 
     * @return List of all reports
     */
    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }
}