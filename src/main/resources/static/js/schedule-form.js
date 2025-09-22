/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    const preferredDateInput = document.getElementById('preferredDate');
    if (preferredDateInput) {
        flatpickr(preferredDateInput, {
            minDate: "today",
            maxDate: new Date().getFullYear() + "-12-31",
            disable: [
                function(date) {
                    // return true to disable
                    return (date.getDay() === 0 || date.getDay() === 6);
                }
            ],
            dateFormat: "Y-m-d",
            disableMobile: true
        });
    }

    const validationAlert = document.getElementById('validation-alert');
    const form = document.getElementById('appointmentForm');
    const spinnerOverlay = document.getElementById('spinner-overlay');

    form.addEventListener('submit', () => {
        spinnerOverlay.classList.add('show');
    });

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
                const preferredDate = document.getElementById('preferredDate').value;
                const preferredTime = document.getElementById('preferredTime').value;

                if (preferredDate && preferredTime) {
                    const combinedDateTime = `${preferredDate}T${preferredTime}`;
                    document.getElementById('preferredDateTime').value = combinedDateTime;

                    const formattedDate = new Date(combinedDateTime).toLocaleString('en-US', { dateStyle: 'full', timeStyle: 'short' });
                    document.getElementById('review-datetime').textContent = formattedDate;
                }
                
                document.getElementById('review-name').textContent = document.getElementById('name').value;
                document.getElementById('review-email').textContent = document.getElementById('email').value;
                document.getElementById('review-reason').textContent = document.getElementById('reason').value;
            }
        }
    });
});