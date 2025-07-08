// Function to select company from search results
function selectCompany(name, orgNumber) {
    const nameInput = document.getElementById('name');
    const orgInput = document.getElementById('organizationNumber');
    const countrySelect = document.getElementById('countryCode');

    // Hide the dropdown first
    document.getElementById('name-dropdown').innerHTML = '';

    // Temporarily disable HTMX trigger to prevent auto-search while setting values
    const originalTrigger = nameInput.getAttribute('hx-trigger');
    nameInput.removeAttribute('hx-trigger');

    // For Web Awesome components, use a timeout to ensure proper state management
    setTimeout(() => {
        // Set values using both property and attribute for web components
        nameInput.value = name;
        nameInput.setAttribute('value', name);

        if (orgNumber) {
            orgInput.value = orgNumber;
            orgInput.setAttribute('value', orgNumber);
        }

        // Set country code to Norway
        countrySelect.value = 'NO';

        // Trigger only change events (not input) to update component internal state without triggering HTMX
        nameInput.dispatchEvent(new CustomEvent('change', { bubbles: true }));

        if (orgNumber) {
            orgInput.dispatchEvent(new CustomEvent('input', { bubbles: true }));
            orgInput.dispatchEvent(new CustomEvent('change', { bubbles: true }));
        }

        countrySelect.dispatchEvent(new CustomEvent('change', { bubbles: true }));

        // Mark fields as selected for validation
        nameInput.setAttribute('data-selected', 'true');
        if (orgNumber) {
            orgInput.setAttribute('data-selected', 'true');
        }

        // Force validation update by briefly focusing and blurring
        setTimeout(() => {
            // Quick focus-blur cycle to ensure component state is updated
            nameInput.focus();
            setTimeout(() => nameInput.blur(), 10);

            if (orgNumber) {
                setTimeout(() => {
                    orgInput.focus();
                    setTimeout(() => orgInput.blur(), 10);
                }, 20);
            }

            // Re-enable HTMX trigger after all events are processed
            setTimeout(() => {
                if (originalTrigger) {
                    nameInput.setAttribute('hx-trigger', originalTrigger);
                }
            }, 300); // Wait longer to ensure no accidental triggers

        }, 50);

    }, 10); // Small initial delay to ensure dropdown hide is processed
}

// Function to clear form
function clearForm() {
    document.getElementById('createCompanyForm').reset();
    document.getElementById('name-dropdown').innerHTML = '';

    // Remove selection attributes
    document.getElementById('name').removeAttribute('data-selected');
    document.getElementById('organizationNumber').removeAttribute('data-selected');
}

// Show loading state on form submission
document.getElementById('createCompanyForm').addEventListener('htmx:beforeRequest', function(e) {
    // Only handle actual form submission, not search requests
    if (e.detail.elt.tagName === 'FORM') {
        // Double-check that all required fields have values before proceeding
        const nameInput = document.getElementById('name');
        const orgInput = document.getElementById('organizationNumber');
        const countrySelect = document.getElementById('countryCode');

        if (!nameInput.value.trim() || !orgInput.value.trim() || !countrySelect.value.trim()) {
            console.log('Form submission blocked - missing values:', {
                name: nameInput.value,
                org: orgInput.value,
                country: countrySelect.value
            });
            e.preventDefault();
            return false;
        }

        const submitButton = document.getElementById('submitButton');
        if (submitButton) {
            submitButton.disabled = true;
            submitButton.innerHTML = '<wa-spinner style="margin-right: 0.5rem;"></wa-spinner> Creating Company...';
        }
    }
});

// Reset submit button state after form response
document.getElementById('createCompanyForm').addEventListener('htmx:afterRequest', function(e) {
    // Small delay to ensure DOM updates are complete
    setTimeout(() => {
        const submitButton = document.getElementById('submitButton');
        if (submitButton && submitButton.disabled) {
            submitButton.disabled = false;
            submitButton.innerHTML = '<wa-icon name="plus" style="margin-right: 0.5rem;"></wa-icon>Create Company';
        }
    }, 100);
});

// Hide dropdown when clicking outside or on search results
document.addEventListener('click', function(e) {
    const nameInput = document.getElementById('name');
    const nameDropdown = document.getElementById('name-dropdown');

    // Check if click is on a search result item
    const isSearchItem = e.target.closest('.search-item');

    if (isSearchItem) {
        // If clicking on search item, let the selectCompany function handle it
        // and hide dropdown after a short delay to ensure selection completes
        setTimeout(() => {
            nameDropdown.innerHTML = '';
        }, 100);
    } else if (!nameInput.contains(e.target) && !nameDropdown.contains(e.target)) {
        // If clicking outside, hide dropdown immediately
        nameDropdown.innerHTML = '';
    }
});