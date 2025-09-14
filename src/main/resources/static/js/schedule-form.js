/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    createMultiStepForm('appointmentForm', {
        validateStep: (stepNumber) => {
            const currentStepFields = document.getElementById('appointmentForm').querySelectorAll('.form-step')[stepNumber - 1].querySelectorAll('[required]');
            let isValid = true;
            currentStepFields.forEach(field => {
                if (!field.value.trim()) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });
            return isValid;
        },
        onStepChange: (currentStep) => {
            const steps = document.getElementById('appointmentForm').querySelectorAll('.form-step');
            if (currentStep === steps.length) {
                document.getElementById('review-name').textContent = document.getElementById('name').value;
                document.getElementById('review-email').textContent = document.getElementById('email').value;
                const dtInput = document.getElementById('preferredDateTime').value;
                const formattedDate = new Date(dtInput).toLocaleString('en-US', { dateStyle: 'full', timeStyle: 'short' });
                document.getElementById('review-datetime').textContent = formattedDate;
                document.getElementById('review-reason').textContent = document.getElementById('reason').value;
            }
        }
    });
});