/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/ *
 */

package com.keiaa.safepoint.controller;

import java.util.Optional;

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
import com.keiaa.safepoint.service.ReportService;

import jakarta.validation.Valid;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Displays the home page with the report form.
     *
     * @param model the model to add report object to
     * @return the name of the view template to render
     */
    @GetMapping("/")
    public String showForm(Model model) {
        if (!model.containsAttribute("report")) {
            model.addAttribute("report", new Report());
        }
        return "index";
    }

    /**
     * Displays the report submission form page.
     *
     * @param model the model to add report object to
     * @return the name of the view template to render
     */
    @GetMapping("/report")
    public String showReportForm(Model model) {
        if (!model.containsAttribute("report")) {
            model.addAttribute("report", new Report());
        }
        return "report";
    }

    /**
     * Processes the submitted report with optional evidence files.
     *
     * @param report the validated report object containing user submission
     * @param files array of multipart files containing evidence
     * @param bindingResult result of validation checks
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to report page with success or error message
     */
    @PostMapping("/submit-report")
    public String submitReport(@Valid @ModelAttribute("report") Report report, 
                               @RequestParam("files") MultipartFile[] files, 
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form");
            return "redirect:/report";
        }

        try {
            Report savedReport = reportService.submitReport(report, files);
            redirectAttributes.addFlashAttribute("message", "We’ve received your report and will review it with care. If you’re unsafe right now, please contact campus security or emergency services first.");
            redirectAttributes.addFlashAttribute("reportId", savedReport.getReportId()); 
        } catch (DailyReportLimitExceededException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/report";
        }
        
        return "redirect:/report";
    }
}
