
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

    const currencyOptions = supportedCurrencies.map(currency =>
        `<option value="${currency}" ${currency === companyCurrency ? 'selected' : ''}>${currency}</option>`
    ).join('');

    row.innerHTML = `
                <td>
                    <input type="date" 
                           name="postingLines[${rowCounter}].postingDate" 
                           value="${new Date().toISOString().split('T')[0]}"
                           onchange="validatePostingLineFields(${rowCounter})"
                           required>
                </td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 2px;">
                        <r-combobox
                            id="debit-account-${rowCounter}"
                            name="postingLines[${rowCounter}].debitAccount"
                            placeholder="Search debit account..."
                            style="font-size: 0.8rem;">
                        </r-combobox>
                        <r-combobox
                            id="debit-vat-${rowCounter}"
                            name="postingLines[${rowCounter}].debitVatCode"
                            placeholder="VAT code required"
                            style="font-size: 0.75rem;">
                        </r-combobox>
                    </div>
                </td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 2px;">
                        <r-combobox
                            id="credit-account-${rowCounter}"
                            name="postingLines[${rowCounter}].creditAccount"
                            placeholder="Search credit account..."
                            style="font-size: 0.8rem;">
                        </r-combobox>
                        <r-combobox
                            id="credit-vat-${rowCounter}"
                            name="postingLines[${rowCounter}].creditVatCode"
                            placeholder="VAT code required"
                            style="font-size: 0.75rem;">
                        </r-combobox>
                    </div>
                </td>
                <td>
                    <input type="number" 
                           step="0.01" 
                           name="postingLines[${rowCounter}].amount" 
                           class="amount-input"
                           placeholder="0.00"
                           onchange="handleAmountChange(${rowCounter})"
                           onblur="validatePostingLineFields(${rowCounter})"
                           required>
                    <div id="converted-amount-${rowCounter}" class="converted-amount" style="font-size: 0.7rem; color: #6b7280; margin-top: 2px;"></div>
                </td>
                <td>
                    <select name="postingLines[${rowCounter}].currency" 
                            class="currency-select"
                            onchange="handleCurrencyChange(${rowCounter})">
                        ${currencyOptions}
                    </select>
                </td>
                <td>
                    <input type="text" 
                           name="postingLines[${rowCounter}].description" 
                           placeholder="Description">
                </td>
                <td>
                    <wa-button type="button" size="small" variant="danger" onclick="removePostingLine(${rowCounter})">
                        <wa-icon name="trash"></wa-icon>
                    </wa-button>
                </td>
            `;

    tbody.appendChild(row);

    // Initialize r-combobox components after adding the row
    initializeComboboxes(rowCounter);

    rowCounter++;
    updateBalance();
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
            toggleAccountSelection(rowId, 'debit');
            validatePostingLineFields(rowId);
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
            toggleAccountSelection(rowId, 'credit');
            validatePostingLineFields(rowId);
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
        }
        
        debitVatCombo.addEventListener('change', (e) => {
            validatePostingLineFields(rowId);
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
        }
        
        creditVatCombo.addEventListener('change', (e) => {
            validatePostingLineFields(rowId);
        });
    }
}

function removePostingLine(id) {
    const rows = document.querySelectorAll('.posting-line-row');
    if (rows.length > 1) {
        document.getElementById('posting-line-row-' + id).remove();
        updateBalance();
    }
}

function validatePostingLineFields(rowId) {
    const dateInput = document.querySelector(`input[name="postingLines[${rowId}].postingDate"]`);
    const debitAccountCombo = document.getElementById(`debit-account-${rowId}`);
    const creditAccountCombo = document.getElementById(`credit-account-${rowId}`);
    const amountInput = document.querySelector(`input[name="postingLines[${rowId}].amount"]`);
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
        const amountInput = row.querySelector('input[name*=".amount"]');
        const debitInput = row.querySelector('input[name*=".debitAccount"]');
        const creditInput = row.querySelector('input[name*=".creditAccount"]');
        const dateInput = row.querySelector('input[name*=".postingDate"]');
        const currencySelect = row.querySelector('select[name*=".currency"]');

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
    const amountInput = document.querySelector(`input[name="postingLines[${rowId}].amount"]`);
    const currencySelect = document.querySelector(`select[name="postingLines[${rowId}].currency"]`);
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
    updateBalance();
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
        const dateInput = row.querySelector('input[name*=".postingDate"]');
        const debitAccountCombo = document.getElementById(`debit-account-${rowId}`);
        const creditAccountCombo = document.getElementById(`credit-account-${rowId}`);
        const amountInput = row.querySelector('input[name*=".amount"]');
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

    // Then prepare form data
    const isValid = await prepareFormData(event);
    if (!isValid) {
        event.preventDefault();
        return false;
    }

    return true;
}

// Prepare form data before submission
async function prepareFormData(event) {

    // First, make sure all conversions are up to date
    const rows = Array.from(document.querySelectorAll('.posting-line-row'));

    // Update all conversions before validating
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const rowId = row.id.replace('posting-line-row-', '');
        const amountInput = row.querySelector('input[name*=".amount"]');
        const currencySelect = row.querySelector('select[name*=".currency"]');

        if (amountInput && amountInput.value && currencySelect && currencySelect.value) {
            await updateConvertedAmount(rowId);
        }
    }

    // Now validate the balance
    updateBalance();

    // Remove empty rows and reindex
    const tbody = document.getElementById('postingLines');
    const allRows = Array.from(tbody.querySelectorAll('.posting-line-row'));

    // Filter out empty rows and prepare posting lines
    const validPostingLines = [];

    allRows.forEach(row => {
        const rowId = row.id.replace('posting-line-row-', '');
        const dateInput = row.querySelector('input[name*=".postingDate"]');
        const debitAccountCombo = document.getElementById(`debit-account-${rowId}`);
        const creditAccountCombo = document.getElementById(`credit-account-${rowId}`);
        const amountInput = row.querySelector('input[name*=".amount"]');
        const currencySelect = row.querySelector('select[name*=".currency"]');
        const descriptionInput = row.querySelector('input[name*=".description"]');
        const debitVatCombo = document.getElementById(`debit-vat-${rowId}`);
        const creditVatCombo = document.getElementById(`credit-vat-${rowId}`);

        const hasDate = dateInput && dateInput.value.trim() !== '';
        const hasDebitAccount = debitAccountCombo && debitAccountCombo.value.trim() !== '';
        const hasCreditAccount = creditAccountCombo && creditAccountCombo.value.trim() !== '';
        const hasAmount = amountInput && amountInput.value && parseFloat(amountInput.value) > 0;

        if (hasDate && (hasDebitAccount || hasCreditAccount) && hasAmount) {
            const baseData = {
                postingDate: dateInput.value,
                amount: amountInput.value,
                currency: currencySelect.value,
                description: descriptionInput ? descriptionInput.value : ''
            };

            // Always create one posting line with both debit and credit accounts (backend will handle splitting)
            validPostingLines.push({
                ...baseData,
                debitAccount: hasDebitAccount ? debitAccountCombo.value : '',
                creditAccount: hasCreditAccount ? creditAccountCombo.value : '',
                debitVatCode: hasDebitAccount ? (debitVatCombo ? debitVatCombo.value : '') : '',
                creditVatCode: hasCreditAccount ? (creditVatCombo ? creditVatCombo.value : '') : ''
            });
        }
    });

    // Check balance before submission - use the same logic as backend
    let totalSignedAmount = 0;

    validPostingLines.forEach(line => {
        const amount = parseFloat(line.amount);
        const hasDebit = line.debitAccount.trim() !== '';
        const hasCredit = line.creditAccount.trim() !== '';

        if (hasDebit && hasCredit) {
            // Both sides filled - this line is balanced (backend will create +amount and -amount)
            // No contribution to total (0)
        } else if (hasDebit) {
            // Only debit - positive contribution
            totalSignedAmount += amount;
        } else if (hasCredit) {
            // Only credit - negative contribution
            totalSignedAmount -= amount;
        }
    });

    if (Math.abs(totalSignedAmount) >= 0.01) {
        alert(`Voucher is not balanced! Total signed amount: ${totalSignedAmount.toFixed(2)} ${companyCurrency}`);
        return false;
    }

    // Remove all rows
    allRows.forEach(row => row.remove());

    // Create new rows from validPostingLines
    validPostingLines.forEach((line, index) => {
        const newRow = document.createElement('tr');
        newRow.className = 'posting-line-row';
        newRow.innerHTML = `
                    <input type="hidden" name="postingLines[${index}].postingDate" value="${line.postingDate}">
                    <input type="hidden" name="postingLines[${index}].debitAccount" value="${line.debitAccount || ''}">
                    <input type="hidden" name="postingLines[${index}].creditAccount" value="${line.creditAccount || ''}">
                    <input type="hidden" name="postingLines[${index}].amount" value="${line.amount}">
                    <input type="hidden" name="postingLines[${index}].currency" value="${line.currency}">
                    <input type="hidden" name="postingLines[${index}].description" value="${line.description || ''}">
                    <input type="hidden" name="postingLines[${index}].debitVatCode" value="${line.debitVatCode || ''}">
                    <input type="hidden" name="postingLines[${index}].creditVatCode" value="${line.creditVatCode || ''}">
                `;
        tbody.appendChild(newRow);
    });

    // If no valid posting lines, prevent form submission
    if (validPostingLines.length === 0) {
        alert('Please add at least one complete posting line.');
        return false;
    }

    // Form is valid, allow submission
    return true;
}

// getActualVatCode function removed since we're using r-combobox