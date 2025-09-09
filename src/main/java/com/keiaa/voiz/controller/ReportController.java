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
import com.keiaa.voiz.service.EmailService;

@Controller
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("report", new Report());
        return "index";
    }

    @PostMapping("/submit-report")
    public String submitReport(@ModelAttribute("report") Report report, Model model) {
        report.setReportId(UUID.randomUUID().toString());
        reportRepository.save(report);

        if (report.getEmail() != null && !report.getEmail().isEmpty()) {
            emailService.sendConfirmationEmail(report.getEmail(), report.getReportId());
        }
        
        model.addAttribute("message", "Thank you! Your report has been submitted successfully.");
        model.addAttribute("reportId", report.getReportId()); 
        
        model.addAttribute("report", new Report()); 
        return "index";
    }

    @GetMapping("/track")
    public String showTrackingPage() {
        return "track";
    }

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