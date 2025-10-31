/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.safepoint.exception.DailyReportLimitExceededException;
import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.StudentRepository;
import com.keiaa.safepoint.service.ReportService;

import jakarta.validation.Valid;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/")
    public String showForm(Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("report")) {
            model.addAttribute("report", new Report());
        }
        return "index";
    }

    @GetMapping("/report")
    public String showReportForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/student-login";
        }
        
        if (!model.containsAttribute("report")) {
            Report report = new Report();
            Student student = studentRepository.findByEmail(principal.getName()).orElse(null);
            if (student != null) {
                report.setName(student.getName());
                report.setEmail(student.getEmail());
            }
            model.addAttribute("report", report);
        }
        return "report";
    }

    @PostMapping("/submit-report")
    public String submitReport(@Valid @ModelAttribute("report") Report report, 
                               @RequestParam("files") MultipartFile[] files, 
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Principal principal) {
        if (principal == null) {
            return "redirect:/student-login";
        }
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form");
            return "redirect:/report";
        }

        // Auto-fill user info from authenticated user
        Student student = studentRepository.findByEmail(principal.getName()).orElse(null);
        if (student != null) {
            report.setName(student.getName());
            report.setEmail(student.getEmail());
        }

        try {
            Report savedReport = reportService.submitReport(report, files);
            redirectAttributes.addFlashAttribute("message", "We've received your report and will review it with care. If you're unsafe right now, please contact campus security or emergency services first.");
            redirectAttributes.addFlashAttribute("reportId", savedReport.getReportId()); 
        } catch (DailyReportLimitExceededException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/report";
        }
        
        return "redirect:/report";
    }
}
