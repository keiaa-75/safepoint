/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.service.utility;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {
    
    // Basic policy allowing only text formatting tags
    private static final PolicyFactory BASIC_POLICY = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "p", "br", "ul", "ol", "li")
            .toFactory();
    
    // Description policy allowing more tags but still secure
    private static final PolicyFactory DESCRIPTION_POLICY = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "p", "br", "ul", "ol", "li", "h1", "h2", "h3", "blockquote")
            .toFactory();

    /**
     * Sanitizes text input by removing potentially malicious HTML/JavaScript content
     * 
     * @param input The input string to sanitize
     * @return Sanitized input string
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        return BASIC_POLICY.sanitize(input);
    }
    
    /**
     * Sanitizes text input for description fields, allowing more HTML tags if needed
     * 
     * @param input The input string to sanitize
     * @return Sanitized input string
     */
    public String sanitizeDescription(String input) {
        if (input == null) {
            return null;
        }
        
        return DESCRIPTION_POLICY.sanitize(input);
    }
    
    /**
     * Sanitizes name input by removing any HTML tags
     * 
     * @param input The name input string to sanitize
     * @return Sanitized name string
     */
    public String sanitizeName(String input) {
        if (input == null) {
            return null;
        }

        return new HtmlPolicyBuilder().toFactory().sanitize(input);
    }
    
    /**
     * Sanitizes URL input
     * 
     * @param input The URL input string to sanitize
     * @return Sanitized URL string
     */
    public String sanitizeUrl(String input) {
        if (input == null) {
            return null;
        }

        return new HtmlPolicyBuilder().toFactory().sanitize(input);
    }
}