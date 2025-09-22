/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

/**
 * Creates a multi-step form functionality.
 * @param {string} formId The ID of the form element.
 * @param {object} options The options for the multi-step form.
 * @param {function} options.validateStep A function to validate the current step. It should return true if the step is valid, false otherwise.
 * @param {function} [options.onStepChange] A function to be called when the step changes. It receives the new step number as an argument.
 */
function createMultiStepForm(formId, options) {
    const form = document.getElementById(formId);
    if (!form) {
        return;
    }

    let currentStep = 1;
    const steps = Array.from(form.querySelectorAll('.form-step'));
    const nextBtn = form.querySelector('#nextBtn');
    const prevBtn = form.querySelector('#prevBtn');
    const submitBtn = form.querySelector('#submitBtn');

    const showStep = (stepNumber) => {
        steps.forEach((step, index) => {
            step.style.display = (index + 1 === stepNumber) ? 'block' : 'none';
        });

        const progressItems = form.querySelectorAll('.step-progress-item');
        progressItems.forEach((item, index) => {
            item.classList.remove('is-current', 'is-done');
            if (index + 1 < stepNumber) {
                item.classList.add('is-done');
            } else if (index + 1 === stepNumber) {
                item.classList.add('is-current');
            }
        });

        prevBtn.style.display = (stepNumber > 1) ? 'inline-block' : 'none';
        nextBtn.style.display = (stepNumber < steps.length) ? 'inline-block' : 'none';
        submitBtn.style.display = (stepNumber === steps.length) ? 'inline-block' : 'none';
    };

    nextBtn.addEventListener('click', () => {
        if (options.validateStep(currentStep)) {
            if (currentStep < steps.length) {
                currentStep++;
                if (options.onStepChange) {
                    options.onStepChange(currentStep);
                }
                showStep(currentStep);
            }
        }
    });

    prevBtn.addEventListener('click', () => {
        if (currentStep > 1) {
            currentStep--;
            if (options.onStepChange) {
                options.onStepChange(currentStep);
            }
            showStep(currentStep);
        }
    });

    form.addEventListener('submit', (e) => {
        if (!options.validateStep(currentStep)) {
            e.preventDefault();
        }
    });

    showStep(currentStep);
}