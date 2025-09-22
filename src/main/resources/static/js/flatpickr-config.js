/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

function initializeFlatpickr(selector, options = {}) {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    const defaultConfig = {
        minDate: tomorrow,
        disable: [
            function(date) {
                // return true to disable
                return (date.getDay() === 0 || date.getDay() === 6);
            }
        ],
        dateFormat: "Y-m-d",
    };

    const finalConfig = { ...defaultConfig, ...options };
    flatpickr(selector, finalConfig);
}
