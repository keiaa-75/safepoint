/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.impl;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.StudentRepository;

/**
 * Service for loading student-specific user details for authentication.
 * This service only handles student user authentication and assigns STUDENT role.
 */
@Service("studentDetailsService")
public class StudentDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    public StudentDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            if (!student.get().isEmailVerified()) {
                throw new LockedException("Email not verified. Please verify your email before logging in.");
            }
            return new User(
                student.get().getEmail(), 
                student.get().getPassword(), 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );
        }

        throw new UsernameNotFoundException("Student not found: " + email);
    }
}