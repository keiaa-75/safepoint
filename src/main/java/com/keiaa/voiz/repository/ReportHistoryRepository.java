/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keiaa.voiz.model.ReportHistory;

@Repository
public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {
    List<ReportHistory> findByReportIdOrderByTimestampDesc(Long reportId);
}