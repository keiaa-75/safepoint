/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller.advice;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.keiaa.safepoint.controller.AppointmentController;
import com.keiaa.safepoint.controller.PageController;
import com.keiaa.safepoint.controller.ReportController;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.StudentRepository;
import com.keiaa.safepoint.service.FeedbackService;

/**
 * Adds the model attributes the feedback-widget fragment needs
 * ("isAuthenticatedStudent" and "feedbackPromptEligible") to every page a
 * logged-in student can land on, without having to repeat the same lookup
 * in each controller. Scoped to the student-facing page controllers only,
 * so it never runs for admin pages or auth flows.
 */
@ControllerAdvice(
    assignableTypes = {
        PageController.class,
        ReportController.class,
        AppointmentController.class,
    }
)
public class FeedbackPromptAdvice {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FeedbackService feedbackService;

    @ModelAttribute
    public void addFeedbackPromptAttributes(Principal principal, Model model) {
        Student student =
            principal != null
                ? studentRepository
                      .findByEmail(principal.getName())
                      .orElse(null)
                : null;

        model.addAttribute("isAuthenticatedStudent", student != null);
        model.addAttribute(
            "feedbackPromptEligible",
            student != null && !feedbackService.hasSubmittedFeedback(student)
        );
    }
}
