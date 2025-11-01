/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.impl;

import java.util.Collections;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.StudentRepository;

@Service
public class StudentDetailsServiceImpl implements UserDetailsService {

    private final StudentRepository studentRepository;

    public StudentDetailsServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with email: " + email));

        // Check if the email is verified, if not, throw an exception
        if (!student.isEmailVerified()) {
            throw new LockedException("Email not verified. Please verify your email before logging in.");
        }

        return new User(student.getEmail(), student.getPassword(), Collections.emptyList());
    }
}
