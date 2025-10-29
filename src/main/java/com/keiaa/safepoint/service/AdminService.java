/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.model.AppointmentStatus;
import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.model.ReportHistory;
import com.keiaa.safepoint.model.ReportStatus;
import com.keiaa.safepoint.repository.AppointmentRepository;
import com.keiaa.safepoint.repository.ReportHistoryRepository;
import com.keiaa.safepoint.repository.ReportRepository;
import com.keiaa.safepoint.service.utility.EmailService;

@Service
public class AdminService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportHistoryRepository reportHistoryRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Gets dashboard statistics for the admin panel
     * 
     * @return Map containing various counts and metrics
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("pendingReportsCount", reportRepository.countByStatusIn(List.of(ReportStatus.PENDING_REVIEW, ReportStatus.UNDER_INVESTIGATION, ReportStatus.ACTION_TAKEN)));
        stats.put("resolvedReportsCount", reportRepository.countByStatusIn(List.of(ReportStatus.RESOLVED)));
        stats.put("totalAppointmentsCount", appointmentRepository.count());
        stats.put("resolvedAppointmentsCount", appointmentRepository.countByPreferredDateTimeBefore(LocalDateTime.now()));
        stats.put("categoryCounts", reportRepository.countByCategory());
        stats.put("recentReports", reportRepository.findTop3ByOrderByTimestampDesc());
        
        return stats;
    }

    /**
     * Finds an appointment by its ID
     * 
     * @param id The ID of the appointment to find
     * @return Optional containing the appointment if found, empty otherwise
     */
    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Gets all reports with their associated history
     * 
     * @return List of reports with history
     */
    public List<Report> getAllReportsWithHistory() {
        List<Report> reports = reportRepository.findAll();
        
        // Fetch history for each report
        for (Report report : reports) {
            List<ReportHistory> history = reportHistoryRepository.findByReportIdOrderByTimestampDesc(report.getId());
            report.setHistory(history);
        }
        
        return reports;
    }

    /**
     * Groups appointments by week for display
     * 
     * @return Map of weeks to appointments in that week
     */
    public Map<String, List<Appointment>> getAppointmentsGroupedByWeek() {
        List<Appointment> appointments = appointmentRepository.findAllByOrderByPreferredDateTimeAsc();
        
        return appointments.stream()
                .collect(Collectors.groupingBy(appointment -> {
                    TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
                    LocalDate startOfWeek = appointment.getPreferredDateTime().toLocalDate().with(fieldISO, 1);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    return "Week of " + startOfWeek.format(formatter);
                }, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * Finds a report by its report ID with history
     * 
     * @param reportId The report ID to search for
     * @return Optional containing the report if found, empty otherwise
     */
    public Optional<Report> findReportWithHistoryByReportId(String reportId) {
        return reportRepository.findByReportId(reportId)
                .map(report -> {
                    // Fetch history records for the report
                    List<ReportHistory> history = reportHistoryRepository.findByReportIdOrderByTimestampDesc(report.getId());
                    report.setHistory(history);
                    return report;
                });
    }

    /**
     * Updates the status of a report
     * 
     * @param reportId The ID of the report to update
     * @param status The new status
     * @return true if the update was successful, false otherwise
     */
    public boolean updateReportStatus(String reportId, ReportStatus status) {
        return reportRepository.findByReportId(reportId)
            .map(report -> {
                report.setStatus(status);
                reportRepository.save(report);
                return true;
            })
            .orElse(false);
    }

    /**
     * Updates the status of a report with a description for the history
     * 
     * @param reportId The ID of the report to update
     * @param status The new status
     * @param description Description of the status change
     * @return true if the update was successful, false otherwise
     */
    public boolean updateReportStatusWithDescription(String reportId, ReportStatus status, String description) {
        return reportRepository.findByReportId(reportId)
            .map(report -> {
                // Create history record
                ReportHistory history = new ReportHistory();
                history.setReport(report);
                history.setOldStatus(report.getStatus());
                history.setNewStatus(status);
                history.setDescription(description);
                history.setUpdatedBy("Admin"); // In a real implementation, get the actual admin name
                
                reportHistoryRepository.save(history);
                
                // Update report status
                report.setStatus(status);
                reportRepository.save(report);
                
                return true;
            })
            .orElse(false);
    }

    /**
     * Confirms an appointment and sends confirmation email
     * 
     * @param id The ID of the appointment to confirm
     * @return true if the confirmation was successful, false otherwise
     */
    public boolean confirmAppointment(Long id) {
        return appointmentRepository.findById(id)
            .map(appointment -> {
                appointment.setStatus(AppointmentStatus.CONFIRMED);
                appointmentRepository.save(appointment);
                emailService.sendAdminConfirmationEmail(appointment);
                return true;
            })
            .orElse(false);
    }

    /**
     * Reschedules an appointment and sends notification email
     * 
     * @param id The ID of the appointment to reschedule
     * @param newDate The new date for the appointment
     * @param newTime The new time for the appointment
     * @return true if the rescheduling was successful, false otherwise
     */
    public boolean rescheduleAppointment(Long id, String newDate, String newTime) {
        return appointmentRepository.findById(id)
            .map(appointment -> {
                LocalDateTime oldDateTime = appointment.getPreferredDateTime();
                LocalDate date = LocalDate.parse(newDate);
                LocalTime time = LocalTime.parse(newTime);
                appointment.setPreferredDateTime(LocalDateTime.of(date, time));
                appointment.setStatus(AppointmentStatus.CONFIRMED);
                appointmentRepository.save(appointment);
                emailService.sendRescheduleEmail(appointment, oldDateTime);
                return true;
            })
            .orElse(false);
    }

    /**
     * Marks an appointment as completed and sends completion email
     * 
     * @param id The ID of the appointment to complete
     * @return true if the completion was successful, false otherwise
     */
    public boolean completeAppointment(Long id) {
        return appointmentRepository.findById(id)
            .map(appointment -> {
                appointment.setStatus(AppointmentStatus.COMPLETED);
                appointmentRepository.save(appointment);
                emailService.sendCompletionEmail(appointment);
                return true;
            })
            .orElse(false);
    }
}