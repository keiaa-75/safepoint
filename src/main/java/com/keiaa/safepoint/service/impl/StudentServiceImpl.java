/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keiaa.safepoint.exception.DuplicateEmailException;
import com.keiaa.safepoint.exception.DuplicateLrnException;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.StudentRepository;
import com.keiaa.safepoint.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Student registerStudent(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new DuplicateEmailException("An account with this email address already exists.");
        }
        if (studentRepository.findByLrn(student.getLrn()).isPresent()) {
            throw new DuplicateLrnException("An account with this LRN already exists.");
        }

        student.setPassword(passwordEncoder.encode(student.getPassword()));

        try {
            return studentRepository.save(student);
        } catch (DataIntegrityViolationException e) {
            // Safety net if two signups for the same email/LRN land within
            // milliseconds of each other, past the checks above.
            throw new DuplicateEmailException("An account with this email or LRN already exists.");
        }
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Override
    public Optional<Student> findByLrn(String lrn) {
        return studentRepository.findByLrn(lrn);
    }
}
