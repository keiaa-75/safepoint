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
        const filesInput = reportForm.querySelector('#files');
        const fileError = reportForm.querySelector('#file-error');

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
            // Step 3 is the optional file upload, so we don't validate it
            if (stepNumber === 3) {
                return true;
            }
            const currentStepFields = steps[stepNumber - 1].querySelectorAll('[required]');
            let isValid = true;
            currentStepFields.forEach(field => {
                if ((field.type === 'checkbox' && !field.checked) || (field.type !== 'checkbox' && !field.value.trim())) {
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

        const validateFiles = () => {
            const maxFileSize = 1024 * 1024 * 1024; // 1 GB
            fileError.textContent = '';
            nextBtn.disabled = false;

            if (filesInput.files.length > 0) {
                for (const file of filesInput.files) {
                    if (file.size > maxFileSize) {
                        fileError.textContent = `File "${file.name}" is too large. Maximum size is 1 GB.`;
                        nextBtn.disabled = true;
                        return;
                    }
                }
            }
        };

        filesInput.addEventListener('change', validateFiles);

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

        reportForm.addEventListener('submit', (e) => {
            if (!validateStep(currentStep)) {
                e.preventDefault();
            }
        });

        showStep(currentStep);
    }

    // Success Modal Logic
    const copyBtn = document.getElementById('copyBtn');
    if (copyBtn) { // If the copy button exists, the modal has the copy functionality
        const referenceIdInput = document.getElementById('referenceId');
        const copyFeedback = document.getElementById('copy-feedback');

        copyBtn.addEventListener('click', () => {
            navigator.clipboard.writeText(referenceIdInput.value).then(() => {
                copyFeedback.classList.remove('d-none');
                copyBtn.innerHTML = '<i class="bi bi-check-lg text-success"></i>';
                setTimeout(() => {
                    copyFeedback.classList.add('d-none');
                    copyBtn.innerHTML = '<i class="bi bi-clipboard"></i>';
                }, 2000);
            }).catch(err => {
                console.error('Failed to copy text: ', err);
            });
        });
    }
});