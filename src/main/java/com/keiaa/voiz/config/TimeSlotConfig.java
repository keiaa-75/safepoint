/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.voiz.config;

import com.keiaa.voiz.model.TimeSlot;
import com.keiaa.voiz.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class TimeSlotConfig {

    @Bean
    CommandLineRunner initDefaultTimeSlots(@Autowired TimeSlotRepository timeSlotRepository) {
        return args -> {
            // Only initialize if no time slots exist
            if (timeSlotRepository.count() == 0) {
                // Standard office hours: Monday to Friday, 9 AM to 4 PM
                // Split into morning and afternoon sessions with lunch break
                List<TimeSlot> defaultSlots = Arrays.asList(
                    // Morning sessions (9 AM - 12 PM)
                    new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)),
                    new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)),
                    new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)),
                    new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)),
                    new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)),

                    // Afternoon sessions (1 PM - 4 PM)
                    new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(16, 0)),
                    new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0), LocalTime.of(16, 0)),
                    new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(16, 0)),
                    new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(13, 0), LocalTime.of(16, 0)),
                    new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(13, 0), LocalTime.of(16, 0))
                );

                timeSlotRepository.saveAll(defaultSlots);
            }
        };
    }
}