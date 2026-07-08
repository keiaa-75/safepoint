/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keiaa.safepoint.model.Feedback;
import com.keiaa.safepoint.model.Student;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByStudent(Student student);
}
