/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/ *
 */

package com.keiaa.safepoint.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.service.FileLoaderService;
import com.keiaa.safepoint.service.ReportService;

@Controller
public class PageController {

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private FileLoaderService fileLoaderService;

    @GetMapping("/track")
    public String showTrackingPage() {
        return "track";
    }

    /**
     * Tracks a report by its unique ID and displays details.
     *
     * @param reportId the unique identifier for the report
     * @param model the model to add report or error message to
     * @return the name of the view template to render
     */
    @GetMapping("/track-report")
    public String trackReport(@RequestParam("reportId") String reportId, Model model) {
        Optional<Report> reportOptional = reportService.findReportByReportId(reportId);

        if (reportOptional.isPresent()) {
            model.addAttribute("report", reportOptional.get());
        } else {
            model.addAttribute("error", "Report not found. Please check your ID and try again.");
        }

        return "track";
    }

    /**
     * Serves a requested file for download.
     *
     * @param filename the name of the file to download
     * @return response entity containing the file resource
     */
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