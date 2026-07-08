/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.keiaa.safepoint.exception.FeedbackAlreadySubmittedException;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.model.enums.FeedbackRating;
import com.keiaa.safepoint.repository.StudentRepository;
import com.keiaa.safepoint.service.FeedbackService;

/**
 * Backs the feedback-widget fragment. The widget submits via fetch() rather
 * than a normal form post so it can swap from the rating pane to the
 * thank-you pane in place, on whichever page it happens to be open.
 */
@RestController
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/submit-feedback")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitFeedback(
        @RequestParam("rating") FeedbackRating rating,
        @RequestParam(value = "comment", required = false) String comment,
        Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                    "success",
                    false,
                    "message",
                    "Please log in to submit feedback."
                )
            );
        }

        Student student = studentRepository
            .findByEmail(principal.getName())
            .orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                    "success",
                    false,
                    "message",
                    "Please log in to submit feedback."
                )
            );
        }

        try {
            feedbackService.submitFeedback(student, rating, comment);
            String firstName = student.getName().split(" ")[0];
            return ResponseEntity.ok(
                Map.of("success", true, "studentFirstName", firstName)
            );
        } catch (FeedbackAlreadySubmittedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}
