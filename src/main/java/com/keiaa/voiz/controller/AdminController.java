/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.voiz.model.Appointment;
import com.keiaa.voiz.model.Report;
import com.keiaa.voiz.repository.AppointmentRepository;
import com.keiaa.voiz.repository.ReportRepository;

import jakarta.servlet.http.HttpSession;

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

    @PostMapping("/admin-login")
    public String handleAdminLogin(@RequestParam("key") String key, HttpSession session, RedirectAttributes redirectAttributes) {
        if (adminKey.equals(key)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid key. Please try again.");
            return "redirect:/admin-login";
        }
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        return "admin-dashboard";
    }

    @GetMapping("/admin/reports")
    public String showAdminReports(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        List<Report> reports = reportRepository.findAll();
        model.addAttribute("reports", reports);
        return "admin-reports";
    }

    @GetMapping("/admin/appointments")
    public String showAdminAppointments(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        List<Appointment> appointments = appointmentRepository.findAll();
        model.addAttribute("appointments", appointments);
        return "admin-appointments";
    }

    @GetMapping("/admin/report/{id}")
    public String reportDetails(@PathVariable("id") String reportId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        return reportRepository.findByReportId(reportId)
                .map(report -> {
                    model.addAttribute("report", report);
                    return "report-detail";
                })
                .orElse("redirect:/admin/dashboard");
    }

    @GetMapping("/admin/appointment/{id}")
    public String appointmentDetails(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        return appointmentRepository.findById(id)
                .map(appointment -> {
                    model.addAttribute("appointment", appointment);
                    return "appointment-detail";
                })
                .orElse("redirect:/admin/appointments");
    }

    @PostMapping("/admin/report/update-status")
    public String updateReportStatus(@RequestParam("reportId") String reportId,
                                     @RequestParam("status") String status,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }
        
        reportRepository.findByReportId(reportId).ifPresent(report -> {
            report.setStatus(status);
            reportRepository.save(report);
        });
        
        return "redirect:/admin/report/" + reportId;
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin-login";
    }
}
