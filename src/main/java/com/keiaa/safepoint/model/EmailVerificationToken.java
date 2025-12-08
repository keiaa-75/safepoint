/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_verification_tokens")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EmailVerificationToken extends BaseToken {

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    public EmailVerificationToken(String token, Student student) {
        super(token, 24); // 24 hours expiry
        this.student = student;
    }
}