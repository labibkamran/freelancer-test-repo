// HTMX Configuration
htmx.config.globalViewTransitions = true;

// Sidebar Management
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const mainWrapper = document.getElementById('mainWrapper');
    const overlay = document.getElementById('sidebarOverlay');

    if (window.innerWidth <= 768) {
        // Mobile behavior
        sidebar.classList.toggle('mobile-open');
        overlay.classList.toggle('active');
    } else {
        // Desktop behavior
        sidebar.classList.toggle('collapsed');
        mainWrapper.classList.toggle('sidebar-collapsed');

        // Store sidebar state
        localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
    }
}

function openSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');

    if (window.innerWidth <= 768) {
        sidebar.classList.add('mobile-open');
        overlay.classList.add('active');
    }
}

function closeSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');

    sidebar.classList.remove('mobile-open');
    overlay.classList.remove('active');
}

// Submenu Management
function toggleSubmenu(button) {
    const submenu = button.nextElementSibling;
    const arrow = button.querySelector('.menu-arrow');

    // Close other submenus
    const allButtons = document.querySelectorAll('.menu-link');
    const allSubmenus = document.querySelectorAll('.submenu');

    allButtons.forEach(btn => {
        if (btn !== button) {
            btn.classList.remove('expanded');
        }
    });

    allSubmenus.forEach(sub => {
        if (sub !== submenu) {
            sub.classList.remove('expanded');
        }
    });

    // Toggle current submenu
    button.classList.toggle('expanded');
    submenu.classList.toggle('expanded');
}

// Active Menu Management
function setActiveMenu(link) {
    // Remove active class from all menu links
    const allLinks = document.querySelectorAll('.menu-link');
    allLinks.forEach(l => l.classList.remove('active'));

    // Add active class to clicked link
    link.classList.add('active');

    // Store active menu
    localStorage.setItem('activeMenu', link.href);
}

// Initialize sidebar state on page load
document.addEventListener('DOMContentLoaded', function () {
    // Restore sidebar state
    const sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
    if (sidebarCollapsed && window.innerWidth > 768) {
        document.getElementById('sidebar').classList.add('collapsed');
        document.getElementById('mainWrapper').classList.add('sidebar-collapsed');
    }

    // Set active menu based on current URL
    const currentPath = window.location.pathname;
    const menuLinks = document.querySelectorAll('.sidebar-menu a[href]');
    menuLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            setActiveMenu(link);
        }
    });

    // Handle window resize
    window.addEventListener('resize', function () {
        const sidebar = document.getElementById('sidebar');
        const mainWrapper = document.getElementById('mainWrapper');
        const overlay = document.getElementById('sidebarOverlay');

        if (window.innerWidth > 768) {
            // Desktop mode
            sidebar.classList.remove('mobile-open');
            overlay.classList.remove('active');
        } else {
            // Mobile mode
            sidebar.classList.remove('collapsed');
            mainWrapper.classList.remove('sidebar-collapsed');
        }
    });
});

// Company switching function
function switchCompany(tenantId) {
    const dashboardUrl = new URL('/dashboard', window.location.origin);
    dashboardUrl.searchParams.set('tenantId', tenantId);
    window.open(dashboardUrl.toString(), '_blank');
}

// Get current tenant ID from URL
function getCurrentTenantId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('tenantId');
}

// Show loading states
document.addEventListener('htmx:beforeRequest', function (evt) {
    const target = evt.target;
    if (target.classList.contains('btn')) {
        target.disabled = true;
    }
});

document.addEventListener('htmx:afterRequest', function (evt) {
    const target = evt.target;
    if (target.classList.contains('btn')) {
        target.disabled = false;
    }
});

// Auto-hide success alerts after 5 seconds
setTimeout(function () {
    const alerts = document.querySelectorAll('.alert-success');
    alerts.forEach(function (alert) {
        alert.style.transition = 'opacity 0.5s';
        alert.style.opacity = '0';
        setTimeout(() => alert.remove(), 500);
    });
}, 5000);