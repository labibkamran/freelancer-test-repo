// HTMX loading states
htmx.config.globalViewTransitions = true;

// Loading indicator management
document.addEventListener('htmx:beforeRequest', function() {
    const indicator = document.getElementById('loading-indicator');
    if (indicator) {
        indicator.style.opacity = '1';
    }
});

document.addEventListener('htmx:afterRequest', function() {
    const indicator = document.getElementById('loading-indicator');
    if (indicator) {
        indicator.style.opacity = '0';
    }
});