/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.model.Appointment;
import com.keiaa.safepoint.repository.AppointmentRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Submits a new appointment request
     * 
     * @param appointment The appointment to submit
     * @return The saved appointment
     */
    public Appointment submitAppointment(Appointment appointment) {
        Appointment savedAppointment = appointmentRepository.save(appointment);
        emailService.sendAppointmentConfirmation(savedAppointment);
        return savedAppointment;
    }

    /**
     * Finds an appointment by its ID
     * 
     * @param id The ID of the appointment to find
     * @return Optional containing the appointment if found, empty otherwise
     */
    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Finds all appointments
     * 
     * @return List of all appointments
     */
    public List<Appointment> findAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    /**
     * Finds all appointments ordered by preferred date/time
     * 
     * @return List of all appointments ordered by preferred date/time
     */
    public List<Appointment> findAllAppointmentsByOrderByPreferredDateTimeAsc() {
        return appointmentRepository.findAllByOrderByPreferredDateTimeAsc();
    }
}