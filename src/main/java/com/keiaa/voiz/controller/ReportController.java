/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.keiaa.voiz.model.Report;
import com.keiaa.voiz.repository.ReportRepository;

@Controller
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    // Mapping for the main report submission page
    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("report", new Report());
        return "index";
    }

    // Mapping to handle report submission
    @PostMapping("/submit-report")
    public String submitReport(@ModelAttribute("report") Report report, Model model) {
        // Generate a unique ID before saving the report
        report.setReportId(UUID.randomUUID().toString());
        
        reportRepository.save(report);
        
        model.addAttribute("message", "Thank you! Your report has been submitted successfully.");
        model.addAttribute("reportId", report.getReportId()); // Pass the unique ID to the view
        
        // Add a new, empty Report object to reset the form
        model.addAttribute("report", new Report()); 
        return "index";
    }

    // Mapping for the tracking page
    @GetMapping("/track")
    public String showTrackingPage() {
        return "track";
    }

    // Mapping to handle report ID submission for tracking
    @GetMapping("/track-report")
    public String trackReport(@RequestParam("reportId") String reportId, Model model) {
        Optional<Report> reportOptional = reportRepository.findByReportId(reportId);

        if (reportOptional.isPresent()) {
            model.addAttribute("report", reportOptional.get());
        } else {
            model.addAttribute("error", "Report not found. Please check your ID and try again.");
        }

        return "track";
    }
}