/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.model.dto.EmailRequestDto;
import com.keiaa.safepoint.service.StudentService;
import com.keiaa.safepoint.service.EmailVerificationService;
import com.keiaa.safepoint.exception.VerificationTokenException;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/student-signup")
    public String showSignupForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-signup";
    }

    @PostMapping("/student-signup")
    public String processSignupForm(@ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "student-signup";
        }
        Student savedStudent = studentService.registerStudent(student);
        // Create and send verification email after successful registration
        emailVerificationService.createVerificationToken(savedStudent);
        return "redirect:/student-login";
    }

    @GetMapping("/student-login")
    public String showLoginForm() {
        return "student-login";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        try {
            emailVerificationService.verifyToken(token);
            model.addAttribute("title", "Email Verification Successful");
            model.addAttribute("message", "Your email has been successfully verified!");
            model.addAttribute("success", true);
        } catch (VerificationTokenException e) {
            model.addAttribute("title", "Email Verification Failed");
            model.addAttribute("message", "Verification failed: " + e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("success", false);
        }
        return "email-verification-result";
    }
    
    @GetMapping("/resend-verification")
    public String showResendVerificationForm(Model model) {
        model.addAttribute("emailRequest", new EmailRequestDto());
        return "resend-verification";
    }
    
    @PostMapping("/resend-verification")
    public String processResendVerificationForm(@ModelAttribute("emailRequest") EmailRequestDto emailRequest, 
                                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "resend-verification";
        }
        
        // Find the student by email
        Student student = studentService.findByEmail(emailRequest.getEmail()).orElse(null);
        if (student != null && !student.isEmailVerified()) {
            // Create a new verification token for the student
            emailVerificationService.createVerificationToken(student);
            model.addAttribute("message", "A new verification email has been sent to your email address.");
        } else if (student != null && student.isEmailVerified()) {
            model.addAttribute("message", "Your email is already verified.");
        } else {
            model.addAttribute("message", "No account found with that email address.");
        }
        
        return "resend-verification";
    }
}
