/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    // Multi-step form logic for report form
    const reportForm = document.getElementById('reportForm');
    if (reportForm) {
        let currentStep = 1;
        const steps = Array.from(reportForm.querySelectorAll('.form-step'));
        const nextBtn = reportForm.querySelector('#nextBtn');
        const prevBtn = reportForm.querySelector('#prevBtn');
        const submitBtn = reportForm.querySelector('#submitBtn');
        const progressBar = reportForm.querySelector('#progressBar');

        const showStep = (stepNumber) => {
            steps.forEach((step, index) => {
                step.style.display = (index + 1 === stepNumber) ? 'block' : 'none';
            });

            const progress = (stepNumber / steps.length) * 100;
            progressBar.style.width = `${progress}%`;
            progressBar.setAttribute('aria-valuenow', progress);

            prevBtn.style.display = (stepNumber > 1) ? 'inline-block' : 'none';
            nextBtn.style.display = (stepNumber < steps.length) ? 'inline-block' : 'none';
            submitBtn.style.display = (stepNumber === steps.length) ? 'inline-block' : 'none';
        };

        const validateStep = (stepNumber) => {
            const currentStepFields = steps[stepNumber - 1].querySelectorAll('[required]');
            let isValid = true;
            currentStepFields.forEach(field => {
                if (!field.value.trim() || (field.type === 'checkbox' && !field.checked)) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });
            return isValid;
        };

        const updateReview = () => {
            document.getElementById('review-name').textContent = document.getElementById('name').value;
            document.getElementById('review-email').textContent = document.getElementById('email').value;
            const categorySelect = document.getElementById('category');
            document.getElementById('review-category').textContent = categorySelect.options[categorySelect.selectedIndex].text;
            document.getElementById('review-description').textContent = document.getElementById('description').value;
        };

        nextBtn.addEventListener('click', () => {
            if (validateStep(currentStep)) {
                if (currentStep < steps.length) {
                    currentStep++;
                    if (currentStep === steps.length) {
                        updateReview();
                    }
                    showStep(currentStep);
                }
            }
        });

        prevBtn.addEventListener('click', () => {
            if (currentStep > 1) {
                currentStep--;
                showStep(currentStep);
            }
        });

        showStep(currentStep);
    }
});