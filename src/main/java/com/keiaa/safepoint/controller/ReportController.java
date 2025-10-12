/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/ *
 */

package com.keiaa.safepoint.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.safepoint.exception.DailyReportLimitExceededException;
import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.repository.ReportRepository;
import com.keiaa.safepoint.service.EmailService;
import com.keiaa.safepoint.service.FileLoaderService;
import com.keiaa.safepoint.service.FileStorageService;
import com.keiaa.safepoint.service.ReportIdGenerator;

@Controller
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private FileLoaderService fileLoaderService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ReportIdGenerator reportIdGenerator;

    @GetMapping("/")
    public String showForm(Model model) {
        if (!model.containsAttribute("report")) {
            model.addAttribute("report", new Report());
        }
        return "index";
    }

    @GetMapping("/report")
    public String showReportForm(Model model) {
        if (!model.containsAttribute("report")) {
            model.addAttribute("report", new Report());
        }
        return "report";
    }

    @PostMapping("/submit-report")
    public String submitReport(@ModelAttribute("report") Report report, 
                               @RequestParam("files") MultipartFile[] files, 
                               RedirectAttributes redirectAttributes) {
        try {
            report.setReportId(reportIdGenerator.generateReportId());
        } catch (DailyReportLimitExceededException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/report";
        }

        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = fileStorageService.store(file);
                fileNames.add(fileName);
            }
        }
        report.setEvidenceFilePaths(fileNames);

        reportRepository.save(report);

        if (report.getEmail() != null && !report.getEmail().isEmpty()) {
            emailService.sendReportConfirmation(report);
        }
        
        redirectAttributes.addFlashAttribute("message", "We’ve received your report and will review it with care. If you’re unsafe right now, please contact campus security or emergency services first.");
        redirectAttributes.addFlashAttribute("reportId", report.getReportId()); 
        
        return "redirect:/report";
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

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileLoaderService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
