/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.voiz.model.Appointment;
import com.keiaa.voiz.model.Report;
import com.keiaa.voiz.model.ReportStatus;
import com.keiaa.voiz.model.TimeSlot;
import com.keiaa.voiz.repository.AppointmentRepository;
import com.keiaa.voiz.repository.ReportRepository;
import com.keiaa.voiz.repository.TimeSlotRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

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

        model.addAttribute("pendingReportsCount", reportRepository.countByStatusIn(List.of(ReportStatus.PENDING_REVIEW, ReportStatus.UNDER_INVESTIGATION)));
        model.addAttribute("resolvedReportsCount", reportRepository.countByStatusIn(List.of(ReportStatus.ACTION_TAKEN, ReportStatus.RESOLVED)));
        model.addAttribute("totalAppointmentsCount", appointmentRepository.count());
        model.addAttribute("resolvedAppointmentsCount", appointmentRepository.countByPreferredDateTimeBefore(java.time.LocalDateTime.now()));
        model.addAttribute("categoryCounts", reportRepository.countByCategory());
        model.addAttribute("recentReports", reportRepository.findTop3ByOrderByTimestampDesc());

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
                    model.addAttribute("statuses", ReportStatus.values());
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
                                     @RequestParam("status") ReportStatus status,
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

    @PostMapping("/admin/timeslot/add")
    public String addTimeSlot(@RequestParam("dayOfWeek") DayOfWeek dayOfWeek,
                            @RequestParam("startTime") String startTime,
                            @RequestParam("endTime") String endTime,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        TimeSlot timeSlot = new TimeSlot(
            dayOfWeek,
            LocalTime.parse(startTime),
            LocalTime.parse(endTime)
        );
        timeSlotRepository.save(timeSlot);
        redirectAttributes.addFlashAttribute("success", "Time slot added successfully");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/timeslot/update/{id}")
    public String updateTimeSlot(@PathVariable("id") Long id,
                               @RequestParam("dayOfWeek") DayOfWeek dayOfWeek,
                               @RequestParam("startTime") String startTime,
                               @RequestParam("endTime") String endTime,
                               @RequestParam("active") boolean active,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        timeSlotRepository.findById(id).ifPresent(timeSlot -> {
            timeSlot.setDayOfWeek(dayOfWeek);
            timeSlot.setStartTime(LocalTime.parse(startTime));
            timeSlot.setEndTime(LocalTime.parse(endTime));
            timeSlot.setActive(active);
            timeSlotRepository.save(timeSlot);
        });

        redirectAttributes.addFlashAttribute("success", "Time slot updated successfully");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/timeslot/delete/{id}")
    public String deleteTimeSlot(@PathVariable("id") Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        timeSlotRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Time slot deleted successfully");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/timeslots/available")
    @ResponseBody
    public List<TimeSlot> getAvailableTimeSlots(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timeSlotRepository.findByDayOfWeekAndIsActiveTrue(date.getDayOfWeek());
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin-login";
    }
}
