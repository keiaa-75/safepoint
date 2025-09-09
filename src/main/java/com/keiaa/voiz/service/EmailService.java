package com.keiaa.voiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String toEmail, String reportId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com"); // Your configured email address
        message.setTo(toEmail);
        message.setSubject("SafePoint: Report Submitted Successfully");
        message.setText("Thank you for submitting your report. Your unique tracking ID is: " + reportId + 
                        ". Please save this ID to check the status of your report at a later time.");
        mailSender.send(message);
    }
}