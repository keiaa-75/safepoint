/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.exception.FeedbackAlreadySubmittedException;
import com.keiaa.safepoint.model.Feedback;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.model.enums.FeedbackRating;
import com.keiaa.safepoint.repository.FeedbackRepository;
import com.keiaa.safepoint.service.utility.InputSanitizer;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private InputSanitizer inputSanitizer;

    /**
     * Checks whether a student has already rated their experience. Once true,
     * the rating prompt (CTA and the one-minute auto-popup alike) should
     * never be shown to that student again.
     *
     * @param student The student to check
     * @return true if this student already has a feedback entry
     */
    public boolean hasSubmittedFeedback(Student student) {
        return feedbackRepository.existsByStudent(student);
    }

    /**
     * Records a student's feedback. A student may only submit once; the
     * unique constraint on Feedback.student backs up this check in case of
     * a race between two near-simultaneous requests.
     *
     * @param student The student submitting feedback
     * @param rating  The selected rating
     * @param comment The optional free-text comment
     * @return The saved feedback entry
     * @throws FeedbackAlreadySubmittedException If this student has already submitted feedback
     */
    public Feedback submitFeedback(
        Student student,
        FeedbackRating rating,
        String comment
    ) {
        if (hasSubmittedFeedback(student)) {
            throw new FeedbackAlreadySubmittedException(
                "You've already shared your feedback with us."
            );
        }

        Feedback feedback = new Feedback();
        feedback.setStudent(student);
        feedback.setRating(rating);
        feedback.setComment(
            comment != null && !comment.isBlank()
                ? inputSanitizer.sanitize(comment)
                : null
        );

        try {
            return feedbackRepository.save(feedback);
        } catch (DataIntegrityViolationException e) {
            // Two submissions from the same student raced past the check above.
            throw new FeedbackAlreadySubmittedException(
                "You've already shared your feedback with us."
            );
        }
    }
}
