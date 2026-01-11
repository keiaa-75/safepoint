/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keiaa.safepoint.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    long countByPreferredDateTimeBefore(LocalDateTime dateTime);

    List<Appointment> findAllByOrderByPreferredDateTimeAsc();

    List<Appointment> findByPreferredDateTimeBetweenOrderByPreferredDateTime(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByEmailOrderByPreferredDateTimeDesc(String email);

    Page<Appointment> findAllByOrderByPreferredDateTimeAsc(Pageable pageable);

    List<Appointment> findAllByPreferredDateTimeNotNull();
    
    default List<String> findAvailableYearMonths() {
        return findAllByPreferredDateTimeNotNull().stream()
                .map(apt -> {
                    String year = String.valueOf(apt.getPreferredDateTime().getYear());
                    String month = String.format("%02d", apt.getPreferredDateTime().getMonthValue() + 1);
                    return year + "-" + month;
                })
                .distinct()
                .sorted((a, b) -> b.compareTo(a))
                .collect(java.util.stream.Collectors.toList());
    }

    long countByPreferredDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
