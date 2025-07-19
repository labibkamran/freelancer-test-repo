const ACCOUNTS_KEY = 'r-accounts';
const ACCOUNTS_TS_KEY = 'r-accounts-ts';
const WEEK_MS = 7 * 24 * 60 * 60 * 1000;

window.getAccounts = async function() {
    const cached = localStorage.getItem(ACCOUNTS_KEY);
    const ts = parseInt(localStorage.getItem(ACCOUNTS_TS_KEY) || '0', 10);
    if (cached && Date.now() - ts < WEEK_MS) {
        try {
            return JSON.parse(cached);
        } catch (_) {
            localStorage.removeItem(ACCOUNTS_KEY);
        }
    }
    const response = await fetch('/api/accounts');
    const data = await response.json();
    localStorage.setItem(ACCOUNTS_KEY, JSON.stringify(data));
    localStorage.setItem(ACCOUNTS_TS_KEY, Date.now().toString());
    return data;
};
