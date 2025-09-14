/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    createMultiStepForm('reportForm', {
        validateStep: (stepNumber) => {
            // Step 3 is the optional file upload, so we don't validate it
            if (stepNumber === 3) {
                return true;
            }
            const currentStepFields = document.getElementById('reportForm').querySelectorAll('.form-step')[stepNumber - 1].querySelectorAll('[required]');
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
        },
        onStepChange: (currentStep) => {
            const steps = document.getElementById('reportForm').querySelectorAll('.form-step');
            if (currentStep === steps.length) {
                document.getElementById('review-name').textContent = document.getElementById('name').value;
                document.getElementById('review-email').textContent = document.getElementById('email').value;
                const categorySelect = document.getElementById('category');
                document.getElementById('review-category').textContent = categorySelect.options[categorySelect.selectedIndex].text;
                document.getElementById('review-description').textContent = document.getElementById('description').value;
            }
        }
    });

    const filesInput = document.querySelector('#files');
    const fileError = document.querySelector('#file-error');
    const nextBtn = document.querySelector('#nextBtn');

    if (filesInput) {
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