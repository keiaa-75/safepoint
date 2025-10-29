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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.service.AppointmentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/schedule")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * Displays the appointment scheduling form.
     *
     * @param model the model to add appointment object to
     * @return the name of the view template to render
     */
    @GetMapping
    public String showForm(Model model) {
        if (!model.containsAttribute("appointment")) {
            model.addAttribute("appointment", new Appointment());
        }
        return "schedule";
    }

    /**
     * Processes the submitted appointment request.
     *
     * @param appointment the validated appointment object containing user request
     * @param bindingResult result of validation checks
     * @param redirectAttributes attributes to pass to the redirected page
     * @return redirect to schedule page with success or error message
     */
    @PostMapping
    public String submitAppointment(@Valid @ModelAttribute("appointment") Appointment appointment, 
                                   BindingResult bindingResult, 
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form");
            return "redirect:/schedule";
        }
        
        appointmentService.submitAppointment(appointment);
        redirectAttributes.addFlashAttribute("message", "Your counseling session request has been submitted successfully!");
        return "redirect:/schedule";
    }
}
