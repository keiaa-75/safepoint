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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.model.Admin;
import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.model.ReportHistory;
import com.keiaa.safepoint.model.dto.AppointmentSummary;
import com.keiaa.safepoint.model.dto.AvailableYear;
import com.keiaa.safepoint.model.dto.ReportSummary;
import com.keiaa.safepoint.model.dto.YearlyReportData;
import com.keiaa.safepoint.model.enums.AppointmentStatus;
import com.keiaa.safepoint.model.enums.ReportStatus;
import com.keiaa.safepoint.repository.AdminRepository;
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
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Gets dashboard statistics for the admin panel
     * 
     * @return Map containing various counts and metrics
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("pendingReportsCount", reportRepository.countByStatusIn(List.of(ReportStatus.PENDING_REVIEW, ReportStatus.UNDER_REVIEW)));
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
     * Gets paginated reports with their associated history
     * 
     * @param pageable pagination information
     * @return Page of reports with history
     */
    public Page<Report> getAllReportsWithHistory(Pageable pageable) {
        Page<Report> reportsPage = reportRepository.findAllByOrderByTimestampDesc(pageable);
        
        // Fetch history for each report
        reportsPage.getContent().forEach(report -> {
            List<ReportHistory> history = reportHistoryRepository.findByReportIdOrderByTimestampDesc(report.getId());
            report.setHistory(history);
        });
        
        return reportsPage;
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
     * Gets paginated appointments
     * 
     * @param pageable pagination information
     * @return Page of appointments
     */
    public Page<Appointment> getAllAppointmentsPaginated(Pageable pageable) {
        return appointmentRepository.findAllByOrderByPreferredDateTimeAsc(pageable);
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
    public boolean updateReportStatusWithDescription(String reportId, ReportStatus status, String description, String username) {
        return reportRepository.findByReportId(reportId)
            .map(report -> {
                // Create history record
                ReportHistory history = new ReportHistory();
                history.setReport(report);
                history.setOldStatus(report.getStatus());
                history.setNewStatus(status);
                history.setDescription(description);
                history.setUpdatedBy(username);
                
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


    /**
     * Creates a new admin user with hashed password
     * 
     * @param username The admin username
     * @param password The admin password
     * @return The created admin user
     */
    public Admin createAdmin(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);  // This will hash the password
        return adminRepository.save(admin);
    }

    /**
     * Gets the count of all admin users
     * 
     * @return The number of admin users in the database
     */
    public long getAllAdminsCount() {
        return adminRepository.count();
    }

    /**
     * Checks if any admin exists in the database
     * 
     * @return true if any admin exists, false otherwise
     */
    public boolean anyAdminExists() {
        return adminRepository.count() > 0;
    }

    /**
     * Gets available years with data for report generation
     * 
     * @return List of available years
     */
    public List<AvailableYear> getAvailableYears() {
        List<String> reportYears = reportRepository.findAvailableYears();
        List<String> appointmentYears = appointmentRepository.findAvailableYears();
        
        reportYears.addAll(appointmentYears);
        List<String> allYears = reportYears.stream()
                .distinct()
                .sorted((a, b) -> b.compareTo(a))
                .toList();
        
        return allYears.stream()
                .map(this::convertToAvailableYear)
                .toList();
    }

    /**
     * Gets yearly report data for a specific year
     * 
     * @param year The year in "YYYY" format
     * @return Yearly report data
     */
    public YearlyReportData getYearlyReportData(String year) {
        int yearInt = Integer.parseInt(year);
        
        LocalDateTime startOfYear = LocalDateTime.of(yearInt, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(yearInt, 12, 31, 23, 59, 59);
        
        List<Report> reports = reportRepository.findByTimestampBetweenOrderByTimestamp(startOfYear, endOfYear);
        List<ReportSummary> reportSummaries = reports.stream()
                .map(report -> new ReportSummary(report.getReportId(), report.getCategory(), report.getStatus()))
                .toList();
        
        List<Appointment> appointments = appointmentRepository.findByPreferredDateTimeBetweenOrderByPreferredDateTime(startOfYear, endOfYear);
        List<AppointmentSummary> appointmentSummaries = appointments.stream()
                .map(apt -> new AppointmentSummary(apt.getId(), apt.getStatus()))
                .toList();
        
        Map<String, Long> reportCategoryCounts = new HashMap<>();
        reportSummaries.stream()
                .collect(Collectors.groupingBy(ReportSummary::getCategory, Collectors.counting()))
                .forEach(reportCategoryCounts::put);
        
        Map<ReportStatus, Long> reportStatusCounts = new EnumMap<>(ReportStatus.class);
        for (ReportStatus status : ReportStatus.values()) {
            reportStatusCounts.put(status, 0L);
        }
        reportSummaries.stream()
                .collect(Collectors.groupingBy(ReportSummary::getStatus, Collectors.counting()))
                .forEach(reportStatusCounts::put);
        
        Map<AppointmentStatus, Long> appointmentStatusCounts = new EnumMap<>(AppointmentStatus.class);
        for (AppointmentStatus status : AppointmentStatus.values()) {
            appointmentStatusCounts.put(status, 0L);
        }
        appointmentSummaries.stream()
                .collect(Collectors.groupingBy(AppointmentSummary::getStatus, Collectors.counting()))
                .forEach(appointmentStatusCounts::put);
        
        String yearDisplay = convertToAvailableYear(year).getDisplay();
        
        return new YearlyReportData(
                year,
                yearDisplay,
                reportSummaries,
                appointmentSummaries,
                reportCategoryCounts,
                reportStatusCounts,
                appointmentStatusCounts,
                reportSummaries.size(),
                appointmentSummaries.size()
        );
    }

    /**
     * Converts year string to AvailableYear object
     */
    private AvailableYear convertToAvailableYear(String year) {
        return new AvailableYear(year, year);
    }
}
