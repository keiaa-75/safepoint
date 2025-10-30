/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.exception.VerificationTokenException;
import com.keiaa.safepoint.model.EmailVerificationToken;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.EmailVerificationTokenRepository;
import com.keiaa.safepoint.service.utility.EmailService;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;
    
    @Autowired
    private EmailService emailService;
    
    public void createVerificationToken(Student student, String baseUrl) {
        // Remove any existing tokens for this student
        Optional<EmailVerificationToken> existingToken = tokenRepository.findByStudent(student);
        if (existingToken.isPresent()) {
            tokenRepository.delete(existingToken.get());
        }
        
        // Generate a new token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailToken = new EmailVerificationToken(token, student);
        tokenRepository.save(emailToken);
        
        // Send verification email
        sendVerificationEmail(student, token, baseUrl);
    }
    
    private void sendVerificationEmail(Student student, String token, String baseUrl) {
        String verificationUrl = baseUrl + "/verify-email?token=" + token;
        emailService.sendEmailVerification(student.getEmail(), student.getName(), verificationUrl);
    }
    
    public Optional<EmailVerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
    
    public void verifyToken(String token) {
        Optional<EmailVerificationToken> tokenOpt = findByToken(token);
        
        if (tokenOpt.isPresent()) {
            EmailVerificationToken emailToken = tokenOpt.get();
            
            if (emailToken.isExpired()) {
                throw new VerificationTokenException("Verification token has expired");
            }
            
            // Mark the student as verified
            Student student = emailToken.getStudent();
            student.setEmailVerified(true);
            
            // Delete the verification token
            tokenRepository.delete(emailToken);
        } else {
            throw new VerificationTokenException("Invalid verification token");
        }
    }
}