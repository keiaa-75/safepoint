/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.model.dto;

import java.util.List;
import java.util.Map;

import com.keiaa.safepoint.model.enums.AppointmentStatus;
import com.keiaa.safepoint.model.enums.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyReportData {
    private String yearMonth;
    private String monthYearDisplay;
    private List<ReportSummary> reports;
    private List<AppointmentSummary> appointments;
    private Map<String, Long> reportCategoryCounts;
    private Map<ReportStatus, Long> reportStatusCounts;
    private Map<AppointmentStatus, Long> appointmentStatusCounts;
    private long totalReports;
    private long totalAppointments;
}
