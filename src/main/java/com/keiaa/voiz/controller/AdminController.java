package com.keiaa.voiz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import com.keiaa.voiz.model.Appointment;
import com.keiaa.voiz.model.Report;
import com.keiaa.voiz.repository.AppointmentRepository;
import com.keiaa.voiz.repository.ReportRepository;

@Controller
public class AdminController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Value("${admin.key}")
    private String adminKey;

    @GetMapping("/admin-login")
    public String showAdminLogin() {
        return "admin-login";
    }

    @RequestMapping(value = "/admin-dashboard", method = {RequestMethod.GET, RequestMethod.POST})
    public String adminDashboard(@RequestParam("key") String key, Model model, RedirectAttributes redirectAttributes) {
        if (!adminKey.equals(key)) {
            redirectAttributes.addFlashAttribute("error", "Invalid key. Please try again.");
            return "redirect:/admin-login";
        }

        List<Report> reports = reportRepository.findAll();
        List<Appointment> appointments = appointmentRepository.findAll();

        model.addAttribute("reports", reports);
        model.addAttribute("appointments", appointments);
        model.addAttribute("adminKey", key);
        return "admin-dashboard";
    }

    @PostMapping("/update-report-status")
    public String updateReportStatus(@RequestParam("reportId") String reportId,
                                     @RequestParam("status") String status,
                                     @RequestParam("key") String key,
                                     RedirectAttributes redirectAttributes) {
        if (!adminKey.equals(key)) {
            redirectAttributes.addFlashAttribute("error", "Invalid key. Please try again.");
            return "redirect:/admin-login";
        }
        
        reportRepository.findByReportId(reportId).ifPresent(report -> {
            report.setStatus(status);
            reportRepository.save(report);
        });
        
        redirectAttributes.addAttribute("key", key);
        return "redirect:/admin-dashboard";
    }

    @Transactional
    @GetMapping("/delete-report")
    public String deleteReport(@RequestParam("reportId") String reportId,
                               @RequestParam("key") String key,
                               RedirectAttributes redirectAttributes) {
        if (!adminKey.equals(key)) {
            redirectAttributes.addFlashAttribute("error", "Invalid key. Please try again.");
            return "redirect:/admin-login";
        }

        reportRepository.findByReportId(reportId).ifPresent(report -> {
            reportRepository.delete(report);
        });

        redirectAttributes.addAttribute("key", key);
        return "redirect:/admin-dashboard";
    }
}