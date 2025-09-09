document.addEventListener('DOMContentLoaded', () => {
    const fabToggle = document.getElementById('fabToggle');
    const navLinks = document.querySelector('.nav-links');

    if (fabToggle && navLinks) {
        fabToggle.addEventListener('click', () => {
            fabToggle.classList.toggle('active');
            navLinks.classList.toggle('active');
        });
    }
});