/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.model.Report;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FileLoaderService fileLoaderService;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendAppointmentConfirmation(Appointment appointment) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(appointment.getEmail());
            helper.setSubject("SafePoint: Counseling Session Request Received");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
            String formattedDateTime = appointment.getPreferredDateTime().format(formatter);

            Context context = new Context();
            context.setVariable("name", appointment.getName());
            context.setVariable("preferredDateTime", formattedDateTime);
            context.setVariable("reason", appointment.getReason());

            String emailBody = templateEngine.process("appointment-confirmation", context);

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

            Context context = new Context();
            context.setVariable("name", appointment.getName());
            context.setVariable("preferredDateTime", formattedDateTime);

            String emailBody = templateEngine.process("admin-confirmation", context);

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

            Context context = new Context();
            context.setVariable("name", appointment.getName());
            context.setVariable("oldDateTime", oldFormattedDateTime);
            context.setVariable("newDateTime", newFormattedDateTime);

            String emailBody = templateEngine.process("reschedule", context);

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

            Context context = new Context();
            context.setVariable("name", appointment.getName());

            String emailBody = templateEngine.process("completion", context);

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

            Context context = new Context();
            context.setVariable("name", report.getName());
            context.setVariable("reportId", report.getReportId());
            context.setVariable("category", report.getCategory());
            context.setVariable("description", report.getDescription());
            context.setVariable("externalLink", report.getExternalLink());

            String emailBody = templateEngine.process("report-confirmation", context);

            helper.setText(emailBody, true); // true for HTML

            // Add attachments
            if (report.getEvidenceFilePaths() != null) {
                for (String filePath : report.getEvidenceFilePaths()) {
                    Resource file = fileLoaderService.load(filePath);
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

    public void sendEmailVerification(String email, String name, String verificationUrl) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(email);
            helper.setSubject("SafePoint: Please Verify Your Email Address");

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationUrl", verificationUrl);

            String emailBody = templateEngine.process("email-verification", context);

            helper.setText(emailBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String email, String resetLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(email);
            helper.setSubject("SafePoint: Password Reset Request");

            Context context = new Context();
            context.setVariable("resetLink", resetLink);

            String emailBody = templateEngine.process("password-reset", context);

            helper.setText(emailBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
