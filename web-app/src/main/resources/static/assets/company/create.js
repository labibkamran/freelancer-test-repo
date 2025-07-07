// Function to select company from search results
function selectCompany(name, orgNumber) {
    const nameInput = document.getElementById('name');
    const orgInput = document.getElementById('organizationNumber');
    const countrySelect = document.getElementById('countryCode');

    // Set values directly without triggering events that cause new searches
    nameInput.value = name;
    if (orgNumber) {
        orgInput.value = orgNumber;
    }

    // Set country code to Norway
    countrySelect.value = 'NO';

    // Hide the dropdown immediately
    document.getElementById('name-dropdown').innerHTML = '';

    // Remove focus to prevent any focus-related issues
    nameInput.blur();
    orgInput.blur();

    // Mark fields as touched/valid for any form validation
    nameInput.setAttribute('data-selected', 'true');
    orgInput.setAttribute('data-selected', 'true');
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
        const submitButton = document.getElementById('submitButton');
        submitButton.disabled = true;
        submitButton.innerHTML = '<wa-spinner style="margin-right: 0.5rem;"></wa-spinner> Creating Company...';
    }
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