/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.config;

import java.io.Console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.keiaa.safepoint.service.AdminService;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    @Override
    public void run(String... args) throws Exception {
        if (!adminService.anyAdminExists()) {
            System.out.println("No admin account found.");
            Console console = System.console();
            
            if (console != null) {
                System.out.println("Setting up the first admin account:");
                String username = console.readLine("Enter admin username: ");

                while (true) {
                    char[] password = console.readPassword("Enter admin password: ");
                    char[] confirmPassword = console.readPassword("Confirm admin password: ");

                    String passwordStr = new String(password);
                    String confirmPasswordStr = new String(confirmPassword);

                    java.util.Arrays.fill(password, ' ');
                    java.util.Arrays.fill(confirmPassword, ' ');

                    if (passwordStr.equals(confirmPasswordStr)) {
                        adminService.createAdmin(username, passwordStr);
                        System.out.println("Admin account created successfully!");
                        break;
                    } else {
                        System.out.println("Passwords do not match. Please try again.");
                    }
                }
            } else {
                System.out.println("No console available. Please create an admin user via the application interface or database.");
            }
        }
    }
}