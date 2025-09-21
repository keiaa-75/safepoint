/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keiaa.voiz.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // This method will automatically be implemented by Spring Data JPA
    // to find a Report based on its unique reportId.
    Optional<Report> findByReportId(String reportId);

    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);
}