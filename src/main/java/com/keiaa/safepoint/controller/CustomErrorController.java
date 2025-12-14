package com.keiaa.safepoint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.keiaa.safepoint.config.NavigationConfig;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @Autowired
    private NavigationConfig navigationConfig;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (status != null) {
            model.addAttribute("status", status);
        }
        if (message != null) {
            model.addAttribute("error", message.toString());
        } else {
            model.addAttribute("error", "An error occurred");
        }

        String navType = "student";

        if (requestUri != null) {
            String uri = requestUri.toString().toLowerCase();

            if (uri.contains("/admin") ||
                uri.contains("/admin-login")) {
                navType = "admin";
            } else if (uri.contains("/student") ||
                      uri.contains("/student-login") ||
                      uri.contains("/student-signup")) {
                navType = "student";
            }
        }

        // Add navigation items based on type
        if ("admin".equals(navType)) {
            model.addAttribute("navItems", navigationConfig.getAdmin());
        } else {
            model.addAttribute("navItems", navigationConfig.getStudent());
        }

        model.addAttribute("navType", navType);

        return "error";
    }
}