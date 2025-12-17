/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.keiaa.safepoint.model.Report;
import com.keiaa.safepoint.model.dto.CategoryCount;
import com.keiaa.safepoint.model.enums.ReportStatus;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // This method will automatically be implemented by Spring Data JPA
    // to find a Report based on its unique reportId.
    Optional<Report> findByReportId(String reportId);

    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusIn(List<ReportStatus> statuses);

    @Query("SELECT new com.keiaa.safepoint.model.dto.CategoryCount(r.category, COUNT(r)) FROM Report r GROUP BY r.category")
    List<CategoryCount> countByCategory();

    List<Report> findTop3ByOrderByTimestampDesc();

    List<Report> findByEmailOrderByTimestampDesc(String email);

    Page<Report> findAllByOrderByTimestampDesc(Pageable pageable);
}