/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

document.addEventListener("DOMContentLoaded", () => {
  const modalEl = document.getElementById("feedbackModal");
  if (!modalEl) {
    // Fragment wasn't rendered on this page - guest, admin, or the
    // student has already submitted feedback. Nothing to do.
    return;
  }

  const feedbackModal = new bootstrap.Modal(modalEl);
  const ratingPane = document.getElementById("feedbackRatingPane");
  const thanksPane = document.getElementById("feedbackThanksPane");
  const ratingOptions = modalEl.querySelectorAll(".feedback-rating-option");
  const commentInput = document.getElementById("feedbackComment");
  const submitBtn = document.getElementById("feedbackSubmitBtn");
  const errorBox = document.getElementById("feedbackError");
  const thanksName = document.getElementById("feedbackThanksName");

  const csrfToken = modalEl.dataset.csrfToken;
  const csrfHeader = modalEl.dataset.csrfHeader;

  const AUTO_PROMPT_DELAY_MS = 60 * 1000;

  let selectedRating = null;
  let feedbackSubmitted = false;
  let autoPromptTimer = null;

  const showError = (message) => {
    errorBox.textContent = message;
    errorBox.classList.remove("d-none");
  };

  ratingOptions.forEach((option) => {
    option.addEventListener("click", () => {
      ratingOptions.forEach((o) => {
        o.classList.remove("is-selected");
        o.setAttribute("aria-pressed", "false");
      });
      option.classList.add("is-selected");
      option.setAttribute("aria-pressed", "true");
      selectedRating = option.dataset.rating;
      submitBtn.disabled = false;
    });
  });

  submitBtn.addEventListener("click", async () => {
    if (!selectedRating) {
      return;
    }

    submitBtn.disabled = true;
    errorBox.classList.add("d-none");

    try {
      const response = await fetch("/submit-feedback", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          [csrfHeader]: csrfToken,
        },
        body: new URLSearchParams({
          rating: selectedRating,
          comment: commentInput.value.trim(),
        }),
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        showError(data.message || "Something went wrong. Please try again.");
        submitBtn.disabled = false;
        return;
      }

      feedbackSubmitted = true;
      clearTimeout(autoPromptTimer);

      thanksName.textContent = data.studentFirstName;
      ratingPane.classList.add("d-none");
      thanksPane.classList.remove("d-none");
    } catch (error) {
      showError("Something went wrong. Please try again.");
      submitBtn.disabled = false;
    }
  });

  // The About page CTA opens this same modal instead of the old external survey link.
  document.querySelectorAll("[data-open-feedback-modal]").forEach((trigger) => {
    trigger.addEventListener("click", (event) => {
      event.preventDefault();
      if (!feedbackSubmitted) {
        feedbackModal.show();
      }
    });
  });

  // Auto-prompt once, after the student has spent about a minute on the
  // page. setTimeout fires once (not a loop), and gets cancelled outright
  // if the student submits before it fires.
  autoPromptTimer = setTimeout(() => {
    if (!feedbackSubmitted && !modalEl.classList.contains("show")) {
      feedbackModal.show();
    }
  }, AUTO_PROMPT_DELAY_MS);
});
