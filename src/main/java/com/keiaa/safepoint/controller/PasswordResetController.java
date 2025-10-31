/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.keiaa.safepoint.service.PasswordResetService;
import com.keiaa.safepoint.service.utility.RateLimitService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;
    
    @Autowired
    private RateLimitService rateLimitService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, 
                                      HttpServletRequest request, 
                                      Model model) {
        String clientIP = getClientIP(request);
        
        if (!rateLimitService.isAllowed(clientIP)) {
            model.addAttribute("error", "Too many requests. Please wait 5 minutes before trying again.");
            return "forgot-password";
        }

        boolean sent = passwordResetService.sendResetEmail(email);
        if (sent) {
            model.addAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        } else {
            model.addAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        }
        
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        if (!passwordResetService.isValidToken(token)) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset-password-error";
        }
        
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword,
                                     Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        boolean success = passwordResetService.resetPassword(token, password);
        if (success) {
            model.addAttribute("message", "Password reset successful. You can now log in with your new password.");
            return "reset-password-success";
        } else {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset-password-error";
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
