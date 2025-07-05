function selectCompany(name, orgNumber) {
    const nameInput = document.getElementById('companyName');
    const orgInput = document.getElementById('organizationNumber');
    nameInput.value = name;
    if (orgNumber) {
        orgInput.value = orgNumber;
    }
    document.getElementById('name-dropdown').innerHTML = '';
    nameInput.blur();
    orgInput.blur();
    nameInput.setAttribute('data-selected', 'true');
    orgInput.setAttribute('data-selected', 'true');
}

document.getElementById('createCustomerForm').addEventListener('htmx:beforeRequest', function (e) {
    if (e.detail.elt.tagName === 'FORM') {
        const submitButton = document.getElementById('submitButton');
        submitButton.disabled = true;
        submitButton.innerHTML = '<wa-spinner style="margin-right: 0.5rem;"></wa-spinner> Creating Customer...';
    }
});

document.getElementById('createCustomerForm').addEventListener('htmx:afterRequest', function (e) {
    if (e.detail.elt.tagName === 'FORM') {
        const submitButton = document.getElementById('submitButton');
        submitButton.disabled = false;
        submitButton.innerHTML = '<wa-icon name="plus" style="margin-right: 0.5rem;"></wa-icon> Create Customer';
    }
});

document.getElementById('createCustomerForm').addEventListener('htmx:afterSwap', function (e) {
    const submitButton = document.getElementById('submitButton');
    if (submitButton.disabled && submitButton.innerHTML.includes('Creating Customer')) {
        submitButton.disabled = false;
        submitButton.innerHTML = '<wa-icon name="plus" style="margin-right: 0.5rem;"></wa-icon> Create Customer';
    }
});

document.addEventListener('click', function (e) {
    const nameInput = document.getElementById('companyName');
    const nameDropdown = document.getElementById('name-dropdown');

    const isSearchItem = e.target.closest('.search-item');

    if (isSearchItem) {
        setTimeout(() => {
            nameDropdown.innerHTML = '';
        }, 100);
    } else if (!nameInput.contains(e.target) && !nameDropdown.contains(e.target)) {
        nameDropdown.innerHTML = '';
    }
});
