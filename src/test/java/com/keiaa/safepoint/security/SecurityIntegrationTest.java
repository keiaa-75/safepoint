/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.keiaa.safepoint.model.Admin;
import com.keiaa.safepoint.model.Student;
import com.keiaa.safepoint.repository.AdminRepository;
import com.keiaa.safepoint.repository.StudentRepository;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        // Clean up and create test users
        adminRepository.deleteAll();
        studentRepository.deleteAll();
        
        // Create test admin
        Admin admin = new Admin();
        admin.setUsername("testadmin");
        admin.setPassword("adminpassword");
        adminRepository.save(admin);
        
        // Create test student
        Student student = new Student();
        student.setName("Test Student");
        student.setEmail("student@test.com");
        student.setLrn("123456789012");
        student.setPassword("studentpassword");
        student.setEmailVerified(true);
        studentRepository.save(student);
    }

    @Test
    void testAdminLoginSuccess() throws Exception {
        mockMvc.perform(formLogin("/admin-login")
                .user("username", "testadmin")
                .password("password", "adminpassword"))
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    void testStudentLoginSuccess() throws Exception {
        mockMvc.perform(formLogin("/student-login")
                .user("email", "student@test.com")
                .password("password", "studentpassword"))
                .andExpect(authenticated().withRoles("STUDENT"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testAdminCannotAccessStudentEndpoints() throws Exception {
        // Login as admin first
        mockMvc.perform(formLogin("/admin-login")
                .user("username", "testadmin")
                .password("password", "adminpassword"));

        // Try to access student dashboard
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testStudentCannotAccessAdminEndpoints() throws Exception {
        // Login as student first
        mockMvc.perform(formLogin("/student-login")
                .user("email", "student@test.com")
                .password("password", "studentpassword"));

        // Try to access admin dashboard
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());

        // Try to access admin reports
        mockMvc.perform(get("/admin/report"))
                .andExpect(status().isForbidden());

        // Try to access admin appointments
        mockMvc.perform(get("/admin/appointment"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedAccessToAdminEndpoints() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/admin-login"));

        mockMvc.perform(get("/admin/report"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/admin-login"));
    }

    @Test
    void testUnauthenticatedAccessToStudentEndpoints() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/student-login"));
    }

    @Test
    void testInvalidAdminCredentials() throws Exception {
        mockMvc.perform(formLogin("/admin-login")
                .user("username", "testadmin")
                .password("password", "wrongpassword"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/admin-login?error=true"));
    }

    @Test
    void testInvalidStudentCredentials() throws Exception {
        mockMvc.perform(formLogin("/student-login")
                .user("email", "student@test.com")
                .password("password", "wrongpassword"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/student-login?error=true"));
    }

    @Test
    void testAdminLogout() throws Exception {
        // Login and then logout
        mockMvc.perform(logout("/admin/logout"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testStudentLogout() throws Exception {
        // Login and then logout
        mockMvc.perform(logout("/student/logout"))
                .andExpect(redirectedUrl("/"));
    }
}
