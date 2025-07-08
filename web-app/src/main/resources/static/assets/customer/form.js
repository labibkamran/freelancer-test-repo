function selectCompany(name, orgNumber) {
    const nameInput = document.getElementById('companyName');
    const orgInput = document.getElementById('organizationNumber');
    nameInput.value = name;
    if (orgNumber)
        orgInput.value = orgNumber;
    document.getElementById('name-dropdown').innerHTML = '';
    nameInput.blur();
    orgInput.blur();
    nameInput.setAttribute('data-selected', 'true');
    orgInput.setAttribute('data-selected', 'true');
}

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
