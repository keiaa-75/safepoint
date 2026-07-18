/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

 package com.keiaa.safepoint.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.keiaa.safepoint.exception.DuplicateEmailException;
import com.keiaa.safepoint.exception.DuplicateLrnException;
import com.keiaa.safepoint.exception.VerificationTokenException;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.model.dto.EmailRequestDto;
import com.keiaa.safepoint.service.EmailVerificationService;
import com.keiaa.safepoint.service.StudentService;
import com.keiaa.safepoint.service.utility.RateLimitingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

 @Controller
 public class StudentController {

     private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

     @Autowired
     private StudentService studentService;

     @Autowired
     private EmailVerificationService emailVerificationService;

     @Autowired
     private RateLimitingService rateLimitingService;

     @GetMapping("/student-signup")
     public String showSignupForm(Model model) {
         model.addAttribute("student", new Student());
         return "student-signup";
     }

     @PostMapping("/student-signup")
     public String processSignupForm(@Valid @ModelAttribute("student") Student student,
                                      BindingResult result, Model model) {
         if (result.hasErrors()) {
             return "student-signup";
         }

         Student savedStudent;
         try {
             savedStudent = studentService.registerStudent(student);
         } catch (DuplicateEmailException e) {
             result.rejectValue("email", "duplicate", e.getMessage());
             return "student-signup";
         } catch (DuplicateLrnException e) {
             result.rejectValue("lrn", "duplicate", e.getMessage());
             return "student-signup";
         } catch (Exception e) {
             LOGGER.error("Unexpected error while registering student", e);
             model.addAttribute("error", "Something went wrong on our end. Please try again in a moment.");
             return "student-signup";
         }

         try {
             HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
             String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
             emailVerificationService.createVerificationToken(savedStudent, appUrl);
         } catch (Exception e) {
             // The account already exists at this point - a mail server
             // hiccup shouldn't take the student back to a crash. They can
             // request a fresh verification email from the login page.
             LOGGER.warn("Verification email failed to send for {}", savedStudent.getEmail(), e);
         }

         return "redirect:/student-login?success=true";
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
                                                BindingResult result, Model model, HttpServletRequest request) {
         if (result.hasErrors()) {
             return "resend-verification";
         }

         String ipAddress = request.getRemoteAddr();
         if (!rateLimitingService.isAllowedForReports(ipAddress)) {
             model.addAttribute("message", "You have made too many requests. Please try again later.");
             return "resend-verification";
         }

         Student student = studentService.findByEmail(emailRequest.getEmail()).orElse(null);
         if (student != null && !student.isEmailVerified()) {
             String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
             emailVerificationService.createVerificationToken(student, appUrl);
             model.addAttribute("message", "A new verification email has been sent to your email address.");
         } else if (student != null && student.isEmailVerified()) {
             model.addAttribute("message", "Your email is already verified.");
         } else {
             model.addAttribute("message", "No account found with that email address.");
         }

         return "resend-verification";
     }
 }
