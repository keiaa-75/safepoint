/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.keiaa.voiz.model.Appointment;
import com.keiaa.voiz.model.Report;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FileStorageService fileStorageService;

    private String loadEmailTemplate(String templateName) {
        try {
            ClassPathResource resource = new ClassPathResource("emails/" + templateName);
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Email template not found.";
        }
    }

    public void sendAppointmentConfirmation(Appointment appointment) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(appointment.getEmail());
            helper.setSubject("SafePoint: Counseling Session Request Received");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
            String formattedDateTime = appointment.getPreferredDateTime().format(formatter);

            String emailBody = loadEmailTemplate("appointment-confirmation.html");
            emailBody = emailBody.replace("${name}", appointment.getName());
            emailBody = emailBody.replace("${preferredDateTime}", formattedDateTime);
            emailBody = emailBody.replace("${reason}", appointment.getReason());

            helper.setText(emailBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendAdminConfirmationEmail(Appointment appointment) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(appointment.getEmail());
            helper.setSubject("SafePoint: Your Counseling Session is Confirmed");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
            String formattedDateTime = appointment.getPreferredDateTime().format(formatter);

            String emailBody = loadEmailTemplate("admin-confirmation.html");
            emailBody = emailBody.replace("${name}", appointment.getName());
            emailBody = emailBody.replace("${preferredDateTime}", formattedDateTime);

            helper.setText(emailBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendRescheduleEmail(Appointment appointment, LocalDateTime oldDateTime) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(appointment.getEmail());
            helper.setSubject("SafePoint: Your Counseling Session has been Rescheduled");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
            String oldFormattedDateTime = oldDateTime.format(formatter);
            String newFormattedDateTime = appointment.getPreferredDateTime().format(formatter);

            String emailBody = loadEmailTemplate("reschedule.html");
            emailBody = emailBody.replace("${name}", appointment.getName());
            emailBody = emailBody.replace("${oldDateTime}", oldFormattedDateTime);
            emailBody = emailBody.replace("${newDateTime}", newFormattedDateTime);

            helper.setText(emailBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendCompletionEmail(Appointment appointment) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(appointment.getEmail());
            helper.setSubject("SafePoint: Your Counseling Session is Complete");

            String emailBody = loadEmailTemplate("completion.html");
            emailBody = emailBody.replace("${name}", appointment.getName());

            helper.setText(emailBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendReportConfirmation(Report report) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true for multipart
            helper.setFrom("your-email@gmail.com"); // Your configured email address
            helper.setTo(report.getEmail());
            helper.setSubject("SafePoint: Report Submitted Successfully");

            String emailBody = loadEmailTemplate("report-confirmation.html");
            emailBody = emailBody.replace("${name}", report.getName());
            emailBody = emailBody.replace("${reportId}", report.getReportId());
            emailBody = emailBody.replace("${category}", report.getCategory());
            emailBody = emailBody.replace("${description}", report.getDescription());

            String externalLinkHtml = "";
            if (report.getExternalLink() != null && !report.getExternalLink().isEmpty()) {
                externalLinkHtml = "<li><strong>External Link:</strong> <a href=\"" + report.getExternalLink() + "\"> " + report.getExternalLink() + "</a></li>";
            }
            emailBody = emailBody.replace("${externalLinkHtml}", externalLinkHtml);

            helper.setText(emailBody, true); // true for HTML

            // Add attachments
            if (report.getEvidenceFilePaths() != null) {
                for (String filePath : report.getEvidenceFilePaths()) {
                    Resource file = fileStorageService.load(filePath);
                    String filename = file.getFilename();
                    if (filename == null) {
                        // Fallback to the path if filename is not available
                        filename = filePath;
                    }
                    helper.addAttachment(filename, file);
                }
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Handle exception
            e.printStackTrace();
        }
    }
}
