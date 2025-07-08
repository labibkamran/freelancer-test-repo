
let rowCounter = 0;

// Initialize form
document.addEventListener('DOMContentLoaded', function () {
    addPostingLine();

    if (clearForm) {
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

        // Update balance display
        updateBalance();
    }
});

function addPostingLine() {
    const tbody = document.getElementById('postingLines');
    const row = document.createElement('tr');
    row.className = 'posting-line-row';
    row.id = 'posting-line-row-' + rowCounter;
    const initialDate = new Date().toISOString().split('T')[0];

    const currencyOptions = supportedCurrencies.map(currency =>
        `<wa-option value="${currency}">${currency}</wa-option>`
    ).join('');

    row.innerHTML = `
                <td>
                    <wa-input type="date"
                           size="small"
                           value="${initialDate}"
                           onchange="updateHiddenField(${rowCounter}, 'postingDate', this.value)"></wa-input>
                    <input type="hidden" name="postingLines[${rowCounter}].postingDate" id="hidden-postingDate-${rowCounter}" value="${initialDate}">
                </td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 2px;">
                        <r-combobox
                            id="debit-account-${rowCounter}"
                            placeholder="Search debit account..."
                            style="font-size: 0.8rem;">
                        </r-combobox>
                        <input type="hidden" name="postingLines[${rowCounter}].debitAccount" id="hidden-debitAccount-${rowCounter}">
                        
                        <r-combobox
                            id="debit-vat-${rowCounter}"
                            placeholder="VAT code required"
                            style="font-size: 0.75rem;">
                        </r-combobox>
                        <input type="hidden" name="postingLines[${rowCounter}].debitVatCode" id="hidden-debitVatCode-${rowCounter}">
                    </div>
                </td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 2px;">
                        <r-combobox
                            id="credit-account-${rowCounter}"
                            placeholder="Search credit account..."
                            style="font-size: 0.8rem;">
                        </r-combobox>
                        <input type="hidden" name="postingLines[${rowCounter}].creditAccount" id="hidden-creditAccount-${rowCounter}">
                        
                        <r-combobox
                            id="credit-vat-${rowCounter}"
                            placeholder="VAT code required"
                            style="font-size: 0.75rem;">
                        </r-combobox>
                        <input type="hidden" name="postingLines[${rowCounter}].creditVatCode" id="hidden-creditVatCode-${rowCounter}">
                    </div>
                </td>
                <td>
                    <wa-input type="number"
                           size="small"
                           step="0.01"
                           placeholder="0.00"
                           onchange="updateHiddenField(${rowCounter}, 'amount', this.value); handleAmountChange(${rowCounter});"
                           onblur="validatePostingLineFields(${rowCounter})"></wa-input>
                    <input type="hidden" name="postingLines[${rowCounter}].amount" id="hidden-amount-${rowCounter}">
                    <div id="converted-amount-${rowCounter}" class="converted-amount" style="font-size: 0.7rem; color: #6b7280; margin-top: 2px;"></div>
                </td>
                <td>
                    <wa-select
                            size="small"
                            class="currency-select"
                            value="${companyCurrency}"
                            onchange="updateHiddenField(${rowCounter}, 'currency', this.value); handleCurrencyChange(${rowCounter});">
                        ${currencyOptions}
                    </wa-select>
                    <input type="hidden" name="postingLines[${rowCounter}].currency" id="hidden-currency-${rowCounter}" value="${companyCurrency}">
                </td>
                <td>
                    <wa-input type="text"
                           size="small"
                           placeholder="Description"
                           onchange="updateHiddenField(${rowCounter}, 'description', this.value)"></wa-input>
                    <input type="hidden" name="postingLines[${rowCounter}].description" id="hidden-description-${rowCounter}">
                </td>
                <td>
                    <wa-button type="button" size="small" variant="danger" onclick="removePostingLine(${rowCounter})">
                        <wa-icon name="trash"></wa-icon>
                    </wa-button>
                </td>
            `;

    tbody.appendChild(row);

    // Initialize r-combobox components after adding the row
    const newRowId = rowCounter;
    requestAnimationFrame(() => {
        initializeComboboxes(newRowId);
        updateBalance();
    });

    rowCounter++;
}

function updateHiddenField(rowId, fieldName, value) {
    const hiddenInput = document.getElementById(`hidden-${fieldName}-${rowId}`);
    if (hiddenInput) {
        hiddenInput.value = value;
    }
}

function initializeComboboxes(rowId) {
    // Initialize account comboboxes with accounts data
    const debitAccountCombo = document.getElementById(`debit-account-${rowId}`);
    const creditAccountCombo = document.getElementById(`credit-account-${rowId}`);
    
    if (debitAccountCombo && accounts) {
        debitAccountCombo.items = accounts.map(account => ({
            value: account.noAccountNumber,
            title: account.noAccountNumber,
            description: account.accountName,
            meta: account.accountDescription || ''
        }));
        
        debitAccountCombo.addEventListener('change', (e) => {
            updateHiddenField(rowId, 'debitAccount', e.target.value);
            toggleAccountSelection(rowId, 'debit');
            updateBalance();
        });
    }

    if (creditAccountCombo && accounts) {
        creditAccountCombo.items = accounts.map(account => ({
            value: account.noAccountNumber,
            title: account.noAccountNumber,
            description: account.accountName,
            meta: account.accountDescription || ''
        }));
        
        creditAccountCombo.addEventListener('change', (e) => {
            updateHiddenField(rowId, 'creditAccount', e.target.value);
            toggleAccountSelection(rowId, 'credit');
            updateBalance();
        });
    }

    // Initialize VAT comboboxes with VAT codes data
    const debitVatCombo = document.getElementById(`debit-vat-${rowId}`);
    const creditVatCombo = document.getElementById(`credit-vat-${rowId}`);
    
    if (debitVatCombo && vatCodes) {
        debitVatCombo.items = vatCodes.map(vatCode => ({
            value: vatCode.code,
            title: vatCode.code,
            subtitle: `(${vatCode.rate}%)`,
            description: vatCode.description,
            meta: `${vatCode.vatType} - ${vatCode.vatCategory}`,
            displayText: `${vatCode.code} (${vatCode.rate}%)`
        }));
        
        // Set default VAT code (first one)
        if (vatCodes.length > 0) {
            debitVatCombo.value = vatCodes[0].code;
            updateHiddenField(rowId, 'debitVatCode', vatCodes[0].code);
        }
        
        debitVatCombo.addEventListener('change', (e) => {
            updateHiddenField(rowId, 'debitVatCode', e.target.value);
            validatePostingLineFields(rowId);
            updateBalance();
        });
    }

    if (creditVatCombo && vatCodes) {
        creditVatCombo.items = vatCodes.map(vatCode => ({
            value: vatCode.code,
            title: vatCode.code,
            subtitle: `(${vatCode.rate}%)`,
            description: vatCode.description,
            meta: `${vatCode.vatType} - ${vatCode.vatCategory}`,
            displayText: `${vatCode.code} (${vatCode.rate}%)`
        }));
        
        // Set default VAT code (first one)
        if (vatCodes.length > 0) {
            creditVatCombo.value = vatCodes[0].code;
            updateHiddenField(rowId, 'creditVatCode', vatCodes[0].code);
        }
        
        creditVatCombo.addEventListener('change', (e) => {
            updateHiddenField(rowId, 'creditVatCode', e.target.value);
            validatePostingLineFields(rowId);
            updateBalance();
        });
    }

    // Validate the line after setting defaults
    validatePostingLineFields(rowId);
}

function removePostingLine(id) {
    const rows = document.querySelectorAll('.posting-line-row');
    if (rows.length > 1) {
        document.getElementById('posting-line-row-' + id).remove();
        updateBalance();
    }
}

function validatePostingLineFields(rowId) {
    const dateInput = document.querySelector(`#posting-line-row-${rowId} wa-input[type="date"]`);
    const debitAccountCombo = document.getElementById(`debit-account-${rowId}`);
    const creditAccountCombo = document.getElementById(`credit-account-${rowId}`);
    const amountInput = document.querySelector(`#posting-line-row-${rowId} wa-input[type="number"]`);
    const debitVatCombo = document.getElementById(`debit-vat-${rowId}`);
    const creditVatCombo = document.getElementById(`credit-vat-${rowId}`);

    // Clear previous error states
    [dateInput, amountInput].forEach(input => {
        if (input) input.classList.remove('field-error');
    });
    
    // Clear error styles from comboboxes by removing any error classes
    [debitAccountCombo, creditAccountCombo, debitVatCombo, creditVatCombo].forEach(combo => {
        if (combo) {
            const input = combo.shadowRoot?.querySelector('.combobox-input');
            if (input) input.classList.remove('has-error');
        }
    });

    const hasDate = dateInput && dateInput.value.trim() !== '';
    const hasDebitAccount = debitAccountCombo && debitAccountCombo.value.trim() !== '';
    const hasCreditAccount = creditAccountCombo && creditAccountCombo.value.trim() !== '';
    const hasAmount = amountInput && amountInput.value && parseFloat(amountInput.value) > 0;
    const hasDebitVat = debitVatCombo && debitVatCombo.value.trim() !== '';
    const hasCreditVat = creditVatCombo && creditVatCombo.value.trim() !== '';

    // If any field has data, validate the entire row
    if (hasDate || hasDebitAccount || hasCreditAccount || hasAmount) {
        if (!hasDate) {
            dateInput.classList.add('field-error');
        }

        // Must have at least one account (debit or credit, or both)
        if (!hasDebitAccount && !hasCreditAccount) {
            if (debitAccountCombo) {
                const input = debitAccountCombo.shadowRoot?.querySelector('.combobox-input');
                if (input) input.classList.add('has-error');
            }
            if (creditAccountCombo) {
                const input = creditAccountCombo.shadowRoot?.querySelector('.combobox-input');
                if (input) input.classList.add('has-error');
            }
            // Also highlight the amount field as it's meaningless without an account
            if (amountInput) {
                amountInput.classList.add('field-error');
            }
        }

        if (!hasAmount) {
            amountInput.classList.add('field-error');
        }

        // VAT validation: if account is filled, VAT must be filled
        if (hasDebitAccount && !hasDebitVat) {
            if (debitVatCombo) {
                const input = debitVatCombo.shadowRoot?.querySelector('.combobox-input');
                if (input) input.classList.add('has-error');
            }
        }
        if (hasCreditAccount && !hasCreditVat) {
            if (creditVatCombo) {
                const input = creditVatCombo.shadowRoot?.querySelector('.combobox-input');
                if (input) input.classList.add('has-error');
            }
        }
    }
}

function updateBalance() {
    let totalDebit = 0;
    let totalCredit = 0;
    let hasValidEntries = false;

    // Calculate totals from individual posting lines using converted amounts
    document.querySelectorAll('.posting-line-row').forEach((row, index) => {
        const rowId = row.id.replace('posting-line-row-', '');
        const amountInput = row.querySelector('wa-input[type="number"]');
        const debitInput = document.getElementById(`debit-account-${rowId}`);
        const creditInput = document.getElementById(`credit-account-${rowId}`);
        const dateInput = row.querySelector('wa-input[type="date"]');
        const currencySelect = row.querySelector('wa-select');

        if (amountInput && amountInput.value && dateInput && dateInput.value) {
            const originalAmount = parseFloat(amountInput.value);
            const currency = currencySelect ? currencySelect.value : companyCurrency;

            // Use converted amount if available, otherwise use original amount
            const convertedAmountStr = amountInput.getAttribute('data-converted-amount');
            const convertedAmount = convertedAmountStr ? parseFloat(convertedAmountStr) : originalAmount;

            if (!isNaN(convertedAmount) && convertedAmount > 0) {
                const hasDebit = debitInput && debitInput.value.trim() !== '';
                const hasCredit = creditInput && creditInput.value.trim() !== '';

                if (hasDebit && hasCredit) {
                    // Both debit and credit filled - this line is self-balanced
                    totalDebit += convertedAmount;
                    totalCredit += convertedAmount;
                    hasValidEntries = true;
                } else if (hasDebit) {
                    // Only debit filled
                    totalDebit += convertedAmount;
                    hasValidEntries = true;
                } else if (hasCredit) {
                    // Only credit filled
                    totalCredit += convertedAmount;
                    hasValidEntries = true;
                }
            }
        }
    });

    const balance = totalDebit - totalCredit;
    const isBalanced = Math.abs(balance) < 0.01;

    // Update display (show amounts in company currency)
    document.getElementById('totalDebit').textContent = totalDebit.toFixed(2) + ' ' + companyCurrency;
    document.getElementById('totalCredit').textContent = totalCredit.toFixed(2) + ' ' + companyCurrency;
    document.getElementById('balanceAmount').textContent = balance.toFixed(2) + ' ' + companyCurrency;

    const saveButton = document.getElementById('saveButton');
    const totalDebitElement = document.getElementById('totalDebit');
    const totalCreditElement = document.getElementById('totalCredit');
    const balanceAmountElement = document.getElementById('balanceAmount');

    if (isBalanced && hasValidEntries && totalDebit > 0) {
        balanceAmountElement.className = 'balance-success';
        totalDebitElement.className = 'balance-success';
        totalCreditElement.className = 'balance-success';
        saveButton.disabled = false;
    } else {
        balanceAmountElement.className = 'balance-error';
        totalDebitElement.className = 'balance-success';
        totalCreditElement.className = 'balance-success';
        saveButton.disabled = true;
    }
}

// Handle amount changes with proper async/await
async function handleAmountChange(rowId) {
    await updateConvertedAmount(rowId);
    updateBalance();
}

// Handle currency changes with proper async/await
async function handleCurrencyChange(rowId) {
    await updateConvertedAmount(rowId);
    updateBalance();
}

// Update converted amount display
async function updateConvertedAmount(rowId) {
    const amountInput = document.querySelector(`#posting-line-row-${rowId} wa-input[type="number"]`);
    const currencySelect = document.querySelector(`#posting-line-row-${rowId} wa-select`);
    const convertedAmountDiv = document.getElementById(`converted-amount-${rowId}`);

    if (amountInput && currencySelect && convertedAmountDiv) {
        const amount = parseFloat(amountInput.value);
        const currency = currencySelect.value;

        if (!isNaN(amount) && amount > 0 && currency !== companyCurrency) {
            try {
                const response = await fetch(`/api/currency/convert?amount=${amount}&fromCurrency=${currency}&toCurrency=${companyCurrency}`);
                if (response.ok) {
                    const data = await response.json();
                    const convertedAmount = parseFloat(data.convertedAmount);
                    convertedAmountDiv.textContent = `≈ ${convertedAmount.toFixed(2)} ${companyCurrency}`;

                    // Store the converted amount for balance calculation
                    amountInput.setAttribute('data-converted-amount', convertedAmount.toString());
                } else {
                    console.error('Failed to convert currency, using original amount');
                    convertedAmountDiv.textContent = `≈ ${amount.toFixed(2)} ${companyCurrency}`;
                    amountInput.setAttribute('data-converted-amount', amount.toString());
                }
            } catch (error) {
                console.error('Error converting amount:', error);
                convertedAmountDiv.textContent = `≈ ${amount.toFixed(2)} ${companyCurrency}`;
                amountInput.setAttribute('data-converted-amount', amount.toString());
            }
        } else if (!isNaN(amount) && amount > 0) {
            // Same currency, no conversion needed
            convertedAmountDiv.textContent = '';
            amountInput.setAttribute('data-converted-amount', amount.toString());
        } else {
            // Invalid amount
            convertedAmountDiv.textContent = '';
            amountInput.removeAttribute('data-converted-amount');
        }
    }
}

function toggleAccountSelection(rowId, type) {
    // With r-combobox, no need to disable fields anymore
    // Both debit and credit sides can be filled
    
    validatePostingLineFields(rowId);
}

// Old search and dropdown functions removed since we're using r-combobox


// Frontend validation functions
function validateField(fieldId, value, errorMessage) {
    const field = document.getElementById(fieldId);
    const errorSpan = document.getElementById(fieldId + '-error');

    if (!value || value.trim() === '') {
        field.classList.add('field-error');
        errorSpan.textContent = errorMessage;
        errorSpan.style.display = 'block';
        return false;
    } else {
        field.classList.remove('field-error');
        errorSpan.style.display = 'none';
        return true;
    }
}

function validateFormFields() {
    let isValid = true;

    // Validate posting lines
    const rows = document.querySelectorAll('.posting-line-row');
    let hasValidEntries = false;

    rows.forEach((row, index) => {
        const rowId = row.id.replace('posting-line-row-', '');
        const dateInput = row.querySelector('wa-input[name*=".postingDate"]');
        const debitAccountCombo = document.getElementById(`debit-account-${rowId}`);
        const creditAccountCombo = document.getElementById(`credit-account-${rowId}`);
        const amountInput = row.querySelector('wa-input[name*=".amount"]');
        const debitVatCombo = document.getElementById(`debit-vat-${rowId}`);
        const creditVatCombo = document.getElementById(`credit-vat-${rowId}`);

        // Clear previous errors
        [dateInput, amountInput].forEach(input => {
            if (input) input.classList.remove('field-error');
        });
        
        // Clear error styles from comboboxes
        [debitAccountCombo, creditAccountCombo, debitVatCombo, creditVatCombo].forEach(combo => {
            if (combo) {
                const input = combo.shadowRoot?.querySelector('.combobox-input');
                if (input) input.classList.remove('has-error');
            }
        });

        const hasDate = dateInput && dateInput.value.trim() !== '';
        const hasDebitAccount = debitAccountCombo && debitAccountCombo.value.trim() !== '';
        const hasCreditAccount = creditAccountCombo && creditAccountCombo.value.trim() !== '';
        const hasAmount = amountInput && amountInput.value && parseFloat(amountInput.value) > 0;
        const hasDebitVat = debitVatCombo && debitVatCombo.value.trim() !== '';
        const hasCreditVat = creditVatCombo && creditVatCombo.value.trim() !== '';

        if (hasDate || hasDebitAccount || hasCreditAccount || hasAmount) {
            // This row has some data, validate it completely
            if (!hasDate) {
                dateInput.classList.add('field-error');
                isValid = false;
            }

            // Must have at least one account (debit or credit, or both)
            if (!hasDebitAccount && !hasCreditAccount) {
                if (debitAccountCombo) {
                    const input = debitAccountCombo.shadowRoot?.querySelector('.combobox-input');
                    if (input) input.classList.add('has-error');
                }
                if (creditAccountCombo) {
                    const input = creditAccountCombo.shadowRoot?.querySelector('.combobox-input');
                    if (input) input.classList.add('has-error');
                }
                isValid = false;
            }

            if (!hasAmount) {
                amountInput.classList.add('field-error');
                isValid = false;
            }

            // VAT validation: if account is filled, VAT must be filled
            if (hasDebitAccount && !hasDebitVat) {
                if (debitVatCombo) {
                    const input = debitVatCombo.shadowRoot?.querySelector('.combobox-input');
                    if (input) input.classList.add('has-error');
                }
                isValid = false;
            }
            if (hasCreditAccount && !hasCreditVat) {
                if (creditVatCombo) {
                    const input = creditVatCombo.shadowRoot?.querySelector('.combobox-input');
                    if (input) input.classList.add('has-error');
                }
                isValid = false;
            }

            if (hasDate && (hasDebitAccount || hasCreditAccount) && hasAmount &&
                (!hasDebitAccount || hasDebitVat) && (!hasCreditAccount || hasCreditVat)) {
                hasValidEntries = true;
            }
        }
    });

    if (!hasValidEntries) {
        showMessage('Please add at least one complete posting line.', 'error');
        isValid = false;
    }

    return isValid;
}

function showMessage(message, type) {
    const messagesDiv = document.getElementById('form-messages');
    const alertClass = type === 'error' ? 'alert-danger' : 'alert-success';
    const iconName = type === 'error' ? 'exclamation-triangle' : 'check-circle';

    messagesDiv.innerHTML = `
                <div class="alert ${alertClass}">
                    <wa-icon name="${iconName}" style="margin-right: 0.5rem;"></wa-icon>
                    ${message}
                </div>
            `;
}

// Combined validation and form preparation
async function validateAndPrepareForm(event) {
    // First validate all fields
    if (!validateFormFields()) {
        event.preventDefault();
        return false;
    }

    // Submission is allowed, just remove empty rows before submitting
    const allRows = Array.from(document.querySelectorAll('.posting-line-row'));
    allRows.forEach(row => {
        const rowId = row.id.replace('posting-line-row-', '');
        const debitAccountCombo = document.getElementById(`debit-account-${rowId}`);
        const creditAccountCombo = document.getElementById(`credit-account-${rowId}`);
        const amountInput = document.querySelector(`#posting-line-row-${rowId} wa-input[type="number"]`);

        const hasDebitAccount = debitAccountCombo && debitAccountCombo.value.trim() !== '';
        const hasCreditAccount = creditAccountCombo && creditAccountCombo.value.trim() !== '';
        const hasAmount = amountInput && amountInput.value && parseFloat(amountInput.value) > 0;

        // If a row has no accounts and no amount, it's considered empty and should be removed.
        if (!hasDebitAccount && !hasCreditAccount && !hasAmount) {
            row.remove();
        }
    });


    // Final balance check before allowing submission
    const saveButton = document.getElementById('saveButton');
    if (saveButton.disabled) {
        event.preventDefault();
        return false;
    }

    return true;
}

// Prepare form data before submission - This function is now much simpler.
async function prepareFormData(event) {
    // This function is now removed, its logic is merged into validateAndPrepareForm
    // All data is now handled by hidden inputs, updated in real-time.
    return true;
}

// getActualVatCode function removed since we're using r-combobox