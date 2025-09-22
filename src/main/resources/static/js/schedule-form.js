/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener('DOMContentLoaded', () => {
    const validationAlert = document.getElementById('validation-alert');
    const form = document.getElementById('appointmentForm');
    const spinnerOverlay = document.getElementById('spinner-overlay');
    
    setupTimeSlotSelection();

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
                } else if (field.id === 'preferredDateTime') {
                    if (!field.value) {
                        validationAlert.textContent = 'Please select a time slot.';
                        validationAlert.classList.remove('d-none');
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
                if (!validationAlert.textContent) {
                    validationAlert.textContent = 'Please correct the errors before proceeding.';
                }
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

function setupTimeSlotSelection() {
    const appointmentDateInput = document.getElementById('appointmentDate');
    const timeSlotsList = document.getElementById('timeSlotsList');
    const preferredDateTimeInput = document.getElementById('preferredDateTime');
    const noSlotsMessage = document.getElementById('noSlotsMessage');

    // Set min date to today and max date to 1 month from now
    const today = new Date();
    const maxDate = new Date();
    maxDate.setMonth(maxDate.getMonth() + 1);
    
    appointmentDateInput.min = today.toISOString().split('T')[0];
    appointmentDateInput.max = maxDate.toISOString().split('T')[0];

    appointmentDateInput.addEventListener('change', function() {
        loadTimeSlots(this.value);
        // Clear the selected time when date changes
        preferredDateTimeInput.value = '';
    });

    function loadTimeSlots(date) {
        fetch(`/admin/timeslots/available?date=${date}`)
            .then(response => response.json())
            .then(slots => {
                timeSlotsList.innerHTML = '';
                if (slots.length === 0) {
                    noSlotsMessage.style.display = 'block';
                    preferredDateTimeInput.value = '';
                } else {
                    noSlotsMessage.style.display = 'none';
                    slots.forEach(slot => {
                        const col = document.createElement('div');
                        col.className = 'col-md-6 mb-3';
                        
                        const btn = document.createElement('button');
                        btn.type = 'button';
                        btn.className = 'btn btn-outline-primary w-100 time-slot-btn';
                        btn.textContent = `${slot.startTime} - ${slot.endTime}`;
                        
                        btn.addEventListener('click', function() {
                            // Remove active class from all buttons
                            document.querySelectorAll('.time-slot-btn').forEach(b => {
                                b.classList.remove('active');
                            });
                            
                            // Add active class to clicked button
                            this.classList.add('active');
                            
                            // Set the datetime value
                            const selectedDate = appointmentDateInput.value;
                            const [startHour, startMinute] = slot.startTime.split(':');
                            const datetime = new Date(selectedDate);
                            datetime.setHours(parseInt(startHour), parseInt(startMinute));
                            
                            preferredDateTimeInput.value = datetime.toISOString().slice(0, 16);
                        });
                        
                        col.appendChild(btn);
                        timeSlotsList.appendChild(col);
                    });
                }
            });
    }

    // If date is already selected (e.g., after form validation error), load time slots
    if (appointmentDateInput.value) {
        loadTimeSlots(appointmentDateInput.value);
    }
}