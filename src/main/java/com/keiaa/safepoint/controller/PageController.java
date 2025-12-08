/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.repository.AppointmentRepository;
import com.keiaa.safepoint.repository.ReportRepository;
import com.keiaa.safepoint.service.utility.FileLoaderService;

@Controller
public class PageController {

    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private FileLoaderService fileLoaderService;

    @GetMapping("/dashboard")
    public String showDashboard(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/student-login";
        }
        
        String email = principal.getName();
        List<Report> reports = reportRepository.findByEmailOrderByTimestampDesc(email);
        List<Appointment> appointments = appointmentRepository.findByEmailOrderByPreferredDateTimeDesc(email);
        
        model.addAttribute("reports", reports);
        model.addAttribute("appointments", appointments);
        
        return "dashboard";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileLoaderService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}