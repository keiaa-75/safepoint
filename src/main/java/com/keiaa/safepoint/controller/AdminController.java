/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.safepoint.model.enums.ReportStatus;
import com.keiaa.safepoint.service.AdminService;
import com.keiaa.safepoint.service.utility.InputSanitizer;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private InputSanitizer inputSanitizer;

    @GetMapping("/admin-login")
    public String showAdminLogin() {
        return "admin-login";
    }

    @GetMapping("/admin/about")
    public String showAdminAbout() {
        return "admin-about";
    }

    /**
     * Displays the admin dashboard with statistics and metrics.
     *
     * @param model the model to add dashboard statistics to
     * @return the name of the view template to render
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        model.addAllAttributes(adminService.getDashboardStatistics());
        return "admin-dashboard";
    }

    /**
     * Displays all reports with their history.
     *
     * @param model the model to add reports to
     * @return the name of the view template to render
     */
    @GetMapping("/admin/reports")
    public String showAdminReports(Model model) {
        model.addAttribute("reports", adminService.getAllReportsWithHistory());
        return "admin-reports";
    }

    /**
     * Displays all appointments grouped by week.
     *
     * @param model the model to add appointments to
     * @return the name of the view template to render
     */
    @GetMapping("/admin/appointments")
    public String showAdminAppointments(Model model) {
        model.addAttribute("appointmentsByWeek", adminService.getAppointmentsGroupedByWeek());
        return "admin-appointments";
    }

    /**
     * Displays details for a specific report.
     *
     * @param reportId the ID of the report to display
     * @param model the model to add report details to
     * @return the name of the view template to render or redirect to dashboard if report not found
     */
    @GetMapping("/admin/report/{id}")
    public String reportDetails(@PathVariable("id") String reportId, Model model) {
        String sanitizedReportId = inputSanitizer.sanitize(reportId);
        return adminService.findReportWithHistoryByReportId(sanitizedReportId)
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
     * @param model the model to add appointment details to
     * @param redirectAttributes attributes to pass to the redirected page if not authenticated
     * @return the name of the view template to render or redirect to appointments if not found
     */
    @GetMapping("/admin/appointment/{id}")
    public String appointmentDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
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
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to report details page or back to login if not authenticated
     */
    @PostMapping("/admin/report/update-status")
    public String updateReportStatus(@RequestParam("reportId") String reportId,
                                     @RequestParam("status") ReportStatus status,
                                     RedirectAttributes redirectAttributes) {
        String sanitizedReportId = inputSanitizer.sanitize(reportId);
        boolean success = adminService.updateReportStatus(sanitizedReportId, status);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Report not found");
        }

        return "redirect:/admin/report/" + sanitizedReportId;
    }

    /**
     * Updates the status of a report with a description for the history log.
     *
     * @param reportId the ID of the report to update
     * @param status the new status for the report
     * @param description the description of the status change
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to report details page or to dashboard if not authenticated
     */
    @PostMapping("/admin/report/update-status-with-description")
    public String updateReportStatusWithDescription(@RequestParam("reportId") String reportId,
                                                    @RequestParam("status") ReportStatus status,
                                                    @RequestParam("description") String description,
                                                    RedirectAttributes redirectAttributes,
                                                    Principal principal) {
        String sanitizedReportId = inputSanitizer.sanitize(reportId);
        String sanitizedDescription = inputSanitizer.sanitize(description);

        boolean success = adminService.updateReportStatusWithDescription(sanitizedReportId, status, sanitizedDescription, principal.getName());
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Report not found");
            return "redirect:/admin/dashboard";
        }

        return "redirect:/admin/report/" + sanitizedReportId;
    }

    /**
     * Confirms an appointment and sends notification email.
     *
     * @param id the ID of the appointment to confirm
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to appointment details page or back to login if not authenticated
     */
    @PostMapping("/admin/appointment/confirm/{id}")
    public String confirmAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
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
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to appointment details page or back to login if not authenticated
     */
    @PostMapping("/admin/appointment/reschedule/{id}")
    public String rescheduleAppointment(@PathVariable("id") Long id,
                                        @RequestParam("newDate") String newDate,
                                        @RequestParam("newTime") String newTime,
                                        RedirectAttributes redirectAttributes) {
        String sanitizedNewDate = inputSanitizer.sanitize(newDate);
        String sanitizedNewTime = inputSanitizer.sanitize(newTime);

        boolean success = adminService.rescheduleAppointment(id, sanitizedNewDate, sanitizedNewTime);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Appointment not found");
        }

        return "redirect:/admin/appointment/" + id;
    }

    /**
     * Marks an appointment as completed and sends completion email.
     *
     * @param id the ID of the appointment to complete
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to appointment details page or back to login if not authenticated
     */
    @PostMapping("/admin/appointment/complete/{id}")
    public String completeAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean success = adminService.completeAppointment(id);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Appointment not found");
        }

        return "redirect:/admin/appointment/" + id;
    }
}
