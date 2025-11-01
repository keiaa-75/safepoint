/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import java.security.Principal;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.StudentRepository;
import com.keiaa.safepoint.service.AppointmentService;
import com.keiaa.safepoint.service.utility.InputSanitizer;

@Controller
@RequestMapping("/schedule")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private InputSanitizer inputSanitizer;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public String showForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/student-login";
        }
        
        if (!model.containsAttribute("appointment")) {
            Appointment appointment = new Appointment();
            Student student = studentRepository.findByEmail(principal.getName()).orElse(null);
            if (student != null) {
                appointment.setName(student.getName());
                appointment.setEmail(student.getEmail());
            }
            model.addAttribute("appointment", appointment);
        }
        return "schedule";
    }

    @PostMapping
    public String submitAppointment(@Valid @ModelAttribute("appointment") Appointment appointment, 
                                   BindingResult bindingResult, 
                                   RedirectAttributes redirectAttributes,
                                   Principal principal) {
        if (principal == null) {
            return "redirect:/student-login";
        }
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form");
            return "redirect:/schedule";
        }
        
        // Auto-fill user info from authenticated user
        Student student = studentRepository.findByEmail(principal.getName()).orElse(null);
        if (student != null) {
            appointment.setName(student.getName());
            appointment.setEmail(student.getEmail());
        }
        
        appointment.setName(inputSanitizer.sanitizeName(appointment.getName()));
        appointment.setReason(inputSanitizer.sanitize(appointment.getReason()));
        
        appointmentService.submitAppointment(appointment);
        redirectAttributes.addFlashAttribute("message", "Your counseling session request has been submitted successfully!");
        return "redirect:/schedule";
    }
}
