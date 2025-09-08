package com.keiaa.voiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.keiaa.voiz.model.Report;
import com.keiaa.voiz.repository.ReportRepository;

@Controller
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("report", new Report());
        return "index";
    }

    @PostMapping("/submit-report")
    public String submitReport(@ModelAttribute("report") Report report, Model model) {
        reportRepository.save(report);
        model.addAttribute("message", "Thank you! Your anonymous report has been submitted successfully.");
        model.addAttribute("report", new Report()); 
        return "index";
    }
}