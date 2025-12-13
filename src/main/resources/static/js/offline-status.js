document.addEventListener('DOMContentLoaded', () => {
    const offlineStatusBar = document.getElementById('offline-status-bar');
    
    // Check initial online status
    if (navigator.onLine) {
        offlineStatusBar.classList.remove('show');
    } else {
        offlineStatusBar.classList.add('show');
    }

    // Listen for online event
    window.addEventListener('online', () => {
        offlineStatusBar.classList.remove('show');
    });

    // Listen for offline event
    window.addEventListener('offline', () => {
        offlineStatusBar.classList.add('show');
    });
});
