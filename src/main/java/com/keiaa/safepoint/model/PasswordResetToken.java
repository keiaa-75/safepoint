/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

package com.keiaa.safepoint.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_reset_tokens")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PasswordResetToken extends BaseToken {

    private String email;
    private boolean used = false;

    public PasswordResetToken(String token, String email) {
        super(token, 1); // 1 hour expiry
        this.email = email;
    }
}
