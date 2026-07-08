/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.model;

import java.time.LocalDateTime;

import com.keiaa.safepoint.model.enums.FeedbackRating;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "feedback")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One feedback entry per student, ever. The unique constraint backs up
    // the application-level check in FeedbackService so a race between two
    // near-simultaneous submissions can't slip through.
    @NotNull
    @OneToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @NotNull(message = "Rating is required")
    @Enumerated(EnumType.STRING)
    private FeedbackRating rating;

    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String comment;

    private LocalDateTime timestamp = LocalDateTime.now();
}
