/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    const fabToggle = document.getElementById('fabToggle');
    const navLinks = document.querySelector('.nav-links');
    const backdrop = document.getElementById('backdrop');
    const mainContent = document.querySelector('.main-content');

    if (fabToggle && navLinks && backdrop && mainContent) {
        fabToggle.addEventListener('click', () => {
            fabToggle.classList.toggle('active');
            navLinks.classList.toggle('active');
            backdrop.classList.toggle('active');
            mainContent.classList.toggle('blurred');
        });

        backdrop.addEventListener('click', () => {
            fabToggle.classList.remove('active');
            navLinks.classList.remove('active');
            backdrop.classList.remove('active');
            mainContent.classList.remove('blurred');
        });
    }
});