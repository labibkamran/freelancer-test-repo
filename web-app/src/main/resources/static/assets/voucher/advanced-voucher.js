// Simplified Advanced Voucher JavaScript - Basic functionality
let rowCounter = 0;

// Initialize form on page load
document.addEventListener('DOMContentLoaded', function () {
    console.log('DOM loaded, initializing voucher form...');
    addPostingLine();

    // Clear form if requested
    if (typeof clearForm !== 'undefined' && clearForm) {
        console.log('Clearing form as requested');
        clearVoucherForm();
    }
});

// Add new posting line
function addPostingLine() {
    console.log('Adding posting line, rowCounter:', rowCounter);
    const tbody = document.getElementById('postingLines');
    if (!tbody) {
        console.error('Could not find postingLines tbody element');
        return;
    }

    // Get tenant ID from URL
    const tenantId = new URLSearchParams(window.location.search).get('tenantId');
    const url = tenantId ? 
        `/htmx/voucher/add-posting-line?rowCounter=${rowCounter}&tenantId=${tenantId}` :
        `/htmx/voucher/add-posting-line?rowCounter=${rowCounter}`;
    
    console.log('Requesting URL:', url);

    // Use HTMX to fetch the new posting line
    htmx.ajax('GET', url, {
        target: '#postingLines',
        swap: 'beforeend',
        headers: {
            'HX-Request': 'true'
        }
    }).then(() => {
        console.log('Successfully added posting line');
        rowCounter++;
        updateBalance();
    }).catch(error => {
        console.error('Error adding posting line:', error);
    });
}

// Remove posting line
function removePostingLine(button) {
    const row = button.closest('tr');
    const rows = document.querySelectorAll('.posting-line-row');
    
    // Don't remove the last row
    if (rows.length > 1) {
        row.remove();
        updateBalance();
    }
}

// Clear the voucher form
function clearVoucherForm() {
        // Clear voucher information fields
        document.querySelectorAll('#voucherForm input[type="date"], #voucherForm input[type="text"]:not([name*="postingLines"])').forEach(input => {
            if (input.type === 'date') {
                input.value = new Date().toISOString().split('T')[0];
            } else {
                input.value = '';
            }
            input.classList.remove('field-error');
        });

        // Clear all posting lines and add a fresh one
        document.getElementById('postingLines').innerHTML = '';
        rowCounter = 0;
        addPostingLine();

    // Reset balance display
    resetBalanceDisplay();
}

// Reset balance display to initial state
function resetBalanceDisplay() {
    const companyCurrency = 'NOK'; // Default currency
    document.getElementById('totalDebit').textContent = `0.00 ${companyCurrency}`;
    document.getElementById('totalCredit').textContent = `0.00 ${companyCurrency}`;
    document.getElementById('balanceAmount').textContent = `0.00 ${companyCurrency}`;
    
    // Reset button state
    const saveButton = document.getElementById('saveButton');
    if (saveButton) {
        saveButton.disabled = true;
    }
}

// Update balance by sending form data to server via HTMX
function updateBalance() {
    const form = document.getElementById('voucherForm');
    if (!form) return;

    // Use HTMX to submit the form data to the balance endpoint
    htmx.ajax('POST', '/htmx/voucher/update-balance', {
        source: form,
        target: '#balanceFooter',
        swap: 'innerHTML',
        headers: {
            'HX-Request': 'true'
        }
    }).then(() => {
        updateSaveButtonState();
    }).catch(error => {
        console.error('Error updating balance:', error);
    });
}

// Update save button state based on balance
function updateSaveButtonState() {
    const saveButton = document.getElementById('saveButton');
    if (!saveButton) return;

    const balanceAmount = document.getElementById('balanceAmount');
    const totalDebit = document.getElementById('totalDebit');
    
    if (balanceAmount && totalDebit) {
        const balanceText = balanceAmount.textContent || '';
        const debitText = totalDebit.textContent || '';
        
        // Check if balanced and has valid entries
        const isBalanced = balanceText.includes('0.00');
        const hasEntries = !debitText.includes('0.00');
        
        saveButton.disabled = !(isBalanced && hasEntries);
    }
}

// Handle form validation before submission
function validateAndPrepareForm(event) {
    const postingLines = document.querySelectorAll('.posting-line-row');
    let hasValidData = false;

    postingLines.forEach(row => {
        const inputs = row.querySelectorAll('input, select, wa-input, wa-select');
        const hasData = Array.from(inputs).some(input => (input.value || '').trim() !== '');
        if (hasData) hasValidData = true;
        
        // Remove empty rows
        if (!hasData) row.remove();
    });

    if (!hasValidData) {
        showValidationError('Please add at least one posting line with data.');
        event.preventDefault();
        return false;
    }
    return true;
}

// Show validation error message
function showValidationError(message) {
    const messagesDiv = document.getElementById('form-messages');
    if (messagesDiv) {
        messagesDiv.innerHTML = `<div style="margin-bottom: var(--wa-space-m);">
            <wa-callout variant="danger" open>
                <wa-icon slot="icon" name="circle-exclamation" variant="regular"></wa-icon>
                <span>${message}</span>
            </wa-callout></div>`;
    }
}

// Handle HTMX events and errors
document.addEventListener('htmx:responseError', (e) => {
    console.error('HTMX Error:', e.detail);
    showValidationError('An error occurred. Please try again.');
});

document.addEventListener('htmx:sendError', (e) => {
    console.error('HTMX Network Error:', e.detail);
    showValidationError('Network error. Please check your connection.');
});

// Auto-trigger balance update
document.addEventListener('change', (e) => {
    if (e.target.matches('input[type="number"], select, wa-input, wa-select')) {
        setTimeout(updateBalance, 100);
    }
});

// Configure HTMX requests with tenant ID
document.addEventListener('htmx:configRequest', (e) => {
    const tenantId = new URLSearchParams(window.location.search).get('tenantId');
    if (tenantId && e.detail.path.includes('/htmx/')) {
        e.detail.parameters.tenantId = tenantId;
    }
});

// Handle HTMX success event to clear form
document.addEventListener('htmx:afterSettle', function(event) {
    // Clear form on successful voucher creation
    if (event.detail.target.id === 'form-messages' && 
        event.detail.target.querySelector('wa-callout[variant="success"]')) {
        setTimeout(() => clearVoucherForm(), 100);
    }
});