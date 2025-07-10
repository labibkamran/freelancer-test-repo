// Function to select company from search results
function selectCompanyFromSearch(element) {
    const name = element.getAttribute('data-name');
    const orgNumber = element.getAttribute('data-org-number');
    const countryCode = element.getAttribute('data-country-code');

    // Update form fields
    const nameInput = document.getElementById('name');
    const orgInput = document.getElementById('organizationNumber');
    const countrySelect = document.getElementById('countryCode');

    nameInput.value = name;
    if (orgNumber && orgNumber !== 'null') {
        orgInput.value = orgNumber;
    }
    countrySelect.value = countryCode;

    // Trigger change events to update component state
    nameInput.dispatchEvent(new Event('change', { bubbles: true }));
    orgInput.dispatchEvent(new Event('change', { bubbles: true }));
    countrySelect.dispatchEvent(new Event('change', { bubbles: true }));

    // Hide the dropdown
    document.getElementById('name-dropdown').innerHTML = '';
}

// Function to clear form
function clearForm() {
    document.getElementById('createCompanyForm').reset();
    document.getElementById('name-dropdown').innerHTML = '';
    document.getElementById('form-messages').innerHTML = '';
}

// Show loading state on form submission
document.getElementById('createCompanyForm').addEventListener('htmx:beforeRequest', function(e) {
    // Only handle actual form submission, not search requests
    if (e.detail.elt.tagName === 'FORM') {
        const submitButton = document.getElementById('submitButton');
        if (submitButton) {
            submitButton.disabled = true;
            submitButton.innerHTML = '<wa-spinner style="margin-right: 0.5rem;"></wa-spinner> Creating Company...';
        }
    }
});

// Reset submit button state after form response
document.getElementById('createCompanyForm').addEventListener('htmx:afterRequest', function(e) {
    // Only handle form responses, not search responses
    if (e.detail.elt.tagName === 'FORM') {
        setTimeout(() => {
            const submitButton = document.getElementById('submitButton');
            if (submitButton && submitButton.disabled) {
                submitButton.disabled = false;
                submitButton.innerHTML = '<wa-icon name="plus" style="margin-right: 0.5rem;"></wa-icon>Create Company';
            }
        }, 100);
    }
});

// Hide dropdown when clicking outside
document.addEventListener('click', function(e) {
    const nameInput = document.getElementById('name');
    const nameDropdown = document.getElementById('name-dropdown');

    // If clicking outside input and dropdown, hide dropdown
    if (nameInput && nameDropdown && 
        !nameInput.contains(e.target) && !nameDropdown.contains(e.target)) {
        nameDropdown.innerHTML = '';
    }
});