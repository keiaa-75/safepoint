/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.service;

import com.keiaa.voiz.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.keiaa.voiz.model.Report;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FileStorageService fileStorageService;

    public void sendAppointmentConfirmation(Appointment appointment) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(appointment.getEmail());
            helper.setSubject("SafePoint: Counseling Session Request Received");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
            String formattedDateTime = appointment.getPreferredDateTime().format(formatter);

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body>");
            emailBody.append("<p>Dear ").append(appointment.getName()).append(",</p>");
            emailBody.append("<p>We have received your request for a counseling session. We will review your preferred time and get back to you shortly to confirm the schedule.</p>");
            emailBody.append("<h3>Your Request Details:</h3>");
            emailBody.append("<ul>");
            emailBody.append("<li><strong>Preferred Date and Time:</strong> ").append(formattedDateTime).append("</li>");
            emailBody.append("<li><strong>Reason for Session:</strong> ").append(appointment.getReason()).append("</li>");
            emailBody.append("</ul>");
            emailBody.append("</body></html>");

            helper.setText(emailBody.toString(), true);

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

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body>");
            emailBody.append("<p>Dear ").append(appointment.getName()).append(",</p>");
            emailBody.append("<p>Your counseling session has been confirmed. Please see the details below:</p>");
            emailBody.append("<h3>Appointment Details:</h3>");
            emailBody.append("<ul>");
            emailBody.append("<li><strong>Date and Time:</strong> ").append(formattedDateTime).append("</li>");
            emailBody.append("</ul>");
            emailBody.append("<p>If you need to reschedule, please contact us as soon as possible.</p>");
            emailBody.append("</body></html>");

            helper.setText(emailBody.toString(), true);

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

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body>");
            emailBody.append("<p>Dear ").append(appointment.getName()).append(",</p>");
            emailBody.append("<p>Your counseling session has been rescheduled. Please see the updated details below:</p>");
            emailBody.append("<h3>Updated Appointment Details:</h3>");
            emailBody.append("<ul>");
            emailBody.append("<li><strong>Previous Date and Time:</strong> ").append(oldFormattedDateTime).append("</li>");
            emailBody.append("<li><strong>New Date and Time:</strong> ").append(newFormattedDateTime).append("</li>");
            emailBody.append("</ul>");
            emailBody.append("<p>If this new time does not work for you, please contact us immediately.</p>");
            emailBody.append("</body></html>");

            helper.setText(emailBody.toString(), true);

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

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body>");
            emailBody.append("<p>Dear ").append(appointment.getName()).append(",</p>");
            emailBody.append("<p>This email is to confirm that your recent counseling session is now marked as complete. We hope it was a helpful and supportive experience.</p>");
            emailBody.append("<p>If you need further assistance or wish to schedule another session, please don't hesitate to reach out.</p>");
            emailBody.append("</body></html>");

            helper.setText(emailBody.toString(), true);

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

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body>");
            emailBody.append("<p>Dear ").append(report.getName()).append(",</p>");
            emailBody.append("<p>We’ve received your report and will review it with care. Your unique tracking ID is: <strong>").append(report.getReportId()).append("</strong>.</p>");
            emailBody.append("<p>Please save this ID to check the status of your report at a later time.</p>");
            emailBody.append("<h3>Here are the details you submitted:</h3>");
            emailBody.append("<ul>");
            emailBody.append("<li><strong>Category:</strong> ").append(report.getCategory()).append("</li>");
            emailBody.append("<li><strong>Description:</strong> ").append(report.getDescription()).append("</li>");
            if (report.getExternalLink() != null && !report.getExternalLink().isEmpty()) {
                emailBody.append("<li><strong>External Link:</strong> <a href=\"").append(report.getExternalLink()).append("\"> ").append(report.getExternalLink()).append("</a></li>");
            }
            emailBody.append("</ul>");
            emailBody.append("<p>If you’re unsafe right now, please contact campus security or emergency services first.</p>");
            emailBody.append("</body></html>");

            helper.setText(emailBody.toString(), true); // true for HTML

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