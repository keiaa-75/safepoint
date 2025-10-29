/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.model.enums.ReportStatus;
import com.keiaa.safepoint.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Value("${admin.key}")
    private String adminKey;

    @GetMapping("/admin-login")
    public String showAdminLogin() {
        return "admin-login";
    }

    /**
     * Handles the admin login process with key validation.
     *
     * @param key the admin key provided by the user
     * @param session the HTTP session to store login state
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to dashboard if successful, otherwise back to login with error
     */
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

    /**
     * Displays the admin dashboard with statistics and metrics.
     *
     * @param session the HTTP session to check authentication
     * @param model the model to add dashboard statistics to
     * @param redirectAttributes attributes to pass to the redirected page if not authenticated
     * @return the name of the view template to render or redirect to login
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        Map<String, Object> stats = adminService.getDashboardStatistics();
        model.addAllAttributes(stats);

        return "admin-dashboard";
    }

    /**
     * Displays all reports with their history.
     *
     * @param session the HTTP session to check authentication
     * @param model the model to add reports to
     * @param redirectAttributes attributes to pass to the redirected page if not authenticated
     * @return the name of the view template to render or redirect to login
     */
    @GetMapping("/admin/reports")
    public String showAdminReports(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        model.addAttribute("reports", adminService.getAllReportsWithHistory());
        return "admin-reports";
    }

    /**
     * Displays all appointments grouped by week.
     *
     * @param session the HTTP session to check authentication
     * @param model the model to add appointments to
     * @param redirectAttributes attributes to pass to the redirected page if not authenticated
     * @return the name of the view template to render or redirect to login
     */
    @GetMapping("/admin/appointments")
    public String showAdminAppointments(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        model.addAttribute("appointmentsByWeek", adminService.getAppointmentsGroupedByWeek());
        return "admin-appointments";
    }

    /**
     * Displays details for a specific report.
     *
     * @param reportId the ID of the report to display
     * @param session the HTTP session to check authentication
     * @param model the model to add report details to
     * @param redirectAttributes attributes to pass to the redirected page if not authenticated
     * @return the name of the view template to render or redirect to dashboard if report not found
     */
    @GetMapping("/admin/report/{id}")
    public String reportDetails(@PathVariable("id") String reportId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        return adminService.findReportWithHistoryByReportId(reportId)
                .map(report -> {
                    model.addAttribute("report", report);
                    model.addAttribute("statuses", ReportStatus.values());
                    return "report-detail";
                })
                .orElse("redirect:/admin/dashboard");
    }

    /**
     * Displays details for a specific appointment.
     *
     * @param id the ID of the appointment to display
     * @param session the HTTP session to check authentication
     * @param model the model to add appointment details to
     * @param redirectAttributes attributes to pass to the redirected page if not authenticated
     * @return the name of the view template to render or redirect to appointments if not found
     */
    @GetMapping("/admin/appointment/{id}")
    public String appointmentDetails(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        return adminService.findAppointmentById(id)
                .map(appointment -> {
                    model.addAttribute("appointment", appointment);
                    return "appointment-detail";
                })
                .orElse("redirect:/admin/appointments");
    }

    /**
     * Updates the status of a report without description.
     *
     * @param reportId the ID of the report to update
     * @param status the new status for the report
     * @param session the HTTP session to check authentication
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to report details page or back to login if not authenticated
     */
    @PostMapping("/admin/report/update-status")
    public String updateReportStatus(@RequestParam("reportId") String reportId,
                                     @RequestParam("status") ReportStatus status,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }
        
        boolean success = adminService.updateReportStatus(reportId, status);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Report not found");
        }
        
        return "redirect:/admin/report/" + reportId;
    }
    
    /**
     * Updates the status of a report with a description for the history log.
     *
     * @param reportId the ID of the report to update
     * @param status the new status for the report
     * @param description the description of the status change
     * @param session the HTTP session to check authentication
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to report details page or to dashboard if not authenticated
     */
    @PostMapping("/admin/report/update-status-with-description")
    public String updateReportStatusWithDescription(@RequestParam("reportId") String reportId,
                                                    @RequestParam("status") ReportStatus status,
                                                    @RequestParam("description") String description,
                                                    HttpSession session,
                                                    RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }
        
        boolean success = adminService.updateReportStatusWithDescription(reportId, status, description);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Report not found");
            return "redirect:/admin/dashboard";
        }
        
        return "redirect:/admin/report/" + reportId;
    }

    /**
     * Confirms an appointment and sends notification email.
     *
     * @param id the ID of the appointment to confirm
     * @param session the HTTP session to check authentication
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to appointment details page or back to login if not authenticated
     */
    @PostMapping("/admin/appointment/confirm/{id}")
    public String confirmAppointment(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        boolean success = adminService.confirmAppointment(id);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Appointment not found");
        }

        return "redirect:/admin/appointment/" + id;
    }

    /**
     * Reschedules an appointment and sends notification email.
     *
     * @param id the ID of the appointment to reschedule
     * @param newDate the new date for the appointment
     * @param newTime the new time for the appointment
     * @param session the HTTP session to check authentication
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to appointment details page or back to login if not authenticated
     */
    @PostMapping("/admin/appointment/reschedule/{id}")
    public String rescheduleAppointment(@PathVariable("id") Long id,
                                        @RequestParam("newDate") String newDate,
                                        @RequestParam("newTime") String newTime,
                                        HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        boolean success = adminService.rescheduleAppointment(id, newDate, newTime);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Appointment not found");
        }

        return "redirect:/admin/appointment/" + id;
    }

    /**
     * Marks an appointment as completed and sends completion email.
     *
     * @param id the ID of the appointment to complete
     * @param session the HTTP session to check authentication
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to appointment details page or back to login if not authenticated
     */
    @PostMapping("/admin/appointment/complete/{id}")
    public String completeAppointment(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null || !(Boolean) session.getAttribute("adminLoggedIn")) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/admin-login";
        }

        boolean success = adminService.completeAppointment(id);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Appointment not found");
        }

        return "redirect:/admin/appointment/" + id;
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin-login";
    }
}
