const VAT_CODES_KEY = 'r-vat-codes';
const VAT_CODES_TS_KEY = 'r-vat-codes-ts';
const WEEK_MS = 7 * 24 * 60 * 60 * 1000;

window.getVatCodes = async function() {
    const cached = localStorage.getItem(VAT_CODES_KEY);
    const ts = parseInt(localStorage.getItem(VAT_CODES_TS_KEY) || '0', 10);
    if (cached && Date.now() - ts < WEEK_MS) {
        try {
            return JSON.parse(cached);
        } catch (_) {
            localStorage.removeItem(VAT_CODES_KEY);
        }
    }
    const response = await fetch('/api/vat-codes');
    const data = await response.json();
    localStorage.setItem(VAT_CODES_KEY, JSON.stringify(data));
    localStorage.setItem(VAT_CODES_TS_KEY, Date.now().toString());
    return data;
};
