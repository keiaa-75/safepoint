/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.service.StudentService;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/student-signup")
    public String showSignupForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-signup";
    }

    @PostMapping("/student-signup")
    public String processSignupForm(@ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "student-signup";
        }
        studentService.registerStudent(student);
        return "redirect:/student-login";
    }

    @GetMapping("/student-login")
    public String showLoginForm() {
        return "student-login";
    }
}
