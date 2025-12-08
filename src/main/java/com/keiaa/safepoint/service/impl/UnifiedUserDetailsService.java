/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.impl;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.model.Admin;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.AdminRepository;
import com.keiaa.safepoint.repository.StudentRepository;

@Service
public class UnifiedUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    public UnifiedUserDetailsService(StudentRepository studentRepository, AdminRepository adminRepository) {
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try student first (by email)
        Optional<Student> student = studentRepository.findByEmail(username);
        if (student.isPresent()) {
            if (!student.get().isEmailVerified()) {
                throw new LockedException("Email not verified. Please verify your email before logging in.");
            }
            return new User(student.get().getEmail(), student.get().getPassword(), Collections.emptyList());
        }

        // Try admin (by username)
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            return new User(admin.get().getUsername(), admin.get().getPassword(), Collections.emptyList());
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
