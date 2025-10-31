/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keiaa.safepoint.model.PasswordResetToken;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.PasswordResetTokenRepository;
import com.keiaa.safepoint.repository.StudentRepository;
import com.keiaa.safepoint.service.utility.EmailService;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean sendResetEmail(String email) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isEmpty()) {
            return false;
        }

        String token = generateToken();
        PasswordResetToken resetToken = new PasswordResetToken(token, email);
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:9090/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
        return true;
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByTokenAndUsedFalse(token);
        
        if (resetToken.isEmpty() || resetToken.get().isExpired()) {
            return false;
        }

        Optional<Student> student = studentRepository.findByEmail(resetToken.get().getEmail());
        if (student.isEmpty()) {
            return false;
        }

        student.get().setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student.get());

        resetToken.get().setUsed(true);
        tokenRepository.save(resetToken.get());

        return true;
    }

    public boolean isValidToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByTokenAndUsedFalse(token);
        return resetToken.isPresent() && !resetToken.get().isExpired();
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
