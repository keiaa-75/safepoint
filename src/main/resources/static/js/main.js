/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    const fabToggle = document.getElementById('fabToggle');
    const navLinks = document.querySelector('.nav-links');

    if (fabToggle && navLinks) {
        fabToggle.addEventListener('click', () => {
            fabToggle.classList.toggle('active');
            navLinks.classList.toggle('active');
        });
    }
});