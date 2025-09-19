/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    const validationAlert = document.getElementById('validation-alert');

    createMultiStepForm('appointmentForm', {
        validateStep: (stepNumber) => {
            validationAlert.classList.add('d-none');
            const currentStepFields = document.getElementById('appointmentForm').querySelectorAll('.form-step')[stepNumber - 1].querySelectorAll('[required]');
            let isValid = true;
            currentStepFields.forEach(field => {
                let isFieldValid = true;
                if (!field.value.trim()) {
                    isFieldValid = false;
                } else if (field.id === 'name') {
                    // Regex allows letters (including unicode), spaces, hyphens, and apostrophes
                    const nameRegex = /^[\p{L}' -]+$/u;
                    if (!nameRegex.test(field.value)) {
                        isFieldValid = false;
                    }
                }

                if (!isFieldValid) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            if (!isValid) {
                validationAlert.textContent = 'Please correct the errors before proceeding.';
                validationAlert.classList.remove('d-none');
            }

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