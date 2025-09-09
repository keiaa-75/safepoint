package com.keiaa.voiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.keiaa.voiz.model.Appointment;
import com.keiaa.voiz.repository.AppointmentRepository;

@Controller
@RequestMapping("/schedule")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        return "schedule";
    }

    @PostMapping
    public String submitAppointment(@ModelAttribute("appointment") Appointment appointment, Model model) {
        appointmentRepository.save(appointment);
        model.addAttribute("message", "Your counseling session request has been submitted successfully!");
        model.addAttribute("appointment", new Appointment()); // Reset the form
        return "schedule";
    }
}