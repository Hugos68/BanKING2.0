const contentBlocks = document.querySelectorAll(".section-block");
const signOutButton = document.querySelector(".sign-out-button");
const deleteAccountButton = document.querySelector(".delete-account-button");
const depositButton = document.querySelector(".deposit-button");
const depositForm = document.querySelector(".deposit-form");
const depositFeedback = document.querySelector(".deposit-feedback");
const transferButton = document.querySelector(".transfer-button");
const transferForm = document.querySelector(".transfer-form");
const transferFeedback = document.querySelector(".transfer-feedback");
const withdrawButton = document.querySelector(".withdraw-button");
const withdrawForm = document.querySelector(".withdraw-form");
const withdrawFeedback = document.querySelector(".withdraw-feedback");
const emailElement = document.querySelector(".email");
const ibanElement = document.querySelector(".iban");
const balanceElement = document.querySelector(".balance");
const transactionTable = document.querySelector(".transaction-table");
let iban;

import {greenHex, redHex, getCookie, promptFeedback} from "./util.js";

const refreshToken = getCookie("refresh_token");

async function refreshAccessToken() {

    // Send refresh token to server to get access token
    const refreshAccessResponse = await fetch("http://localhost:8080/api/accesstoken", {
        method: 'get',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ refreshToken
        })
    });

    // If access token refresh fails, log user out
    if (!refreshAccessResponse.ok) {
        logOut(true);
        throw new Error(refreshAccessResponse.status + " " + refreshAccessResponse.statusText);
    }

    // Get and set access token from response
    const accessTokenFetched = (await refreshAccessResponse.json()).access_token;

    // Create expire dates for tokens
    const date = new Date();
    const accessExpire = new Date(date.getTime() + (15 * 60 * 1000));

    // Set access token cookie with expire date of session
    document.cookie = "access_token="+accessTokenFetched
        + "; SameSite=lax"
        + "; expires="+accessExpire.toUTCString()+";";
}


async function validateTokens(refreshAttempted) {

    // Refresh if possible otherwise logout
    if (!refreshAttempted) {
        await refreshAccessToken();
        refreshAttempted=true;
        await validateTokens(refreshAttempted);
    }
    else if(getCookie("access_token")==="") {
        logOut(true);
    }
}

async function syncTokens() {
    await validateTokens(false);
}

async function getAccountInfo() {

    // Fetch account info with access token
    const accountInfoResponse = await fetch("http://localhost:8080/api/account", {
        method: 'get',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        })
    });
    if (!accountInfoResponse.ok) {
        await syncTokens();
        await getAccountInfo();
        return;
    }

    // Get account info from fetch response
    const accountInfo = await accountInfoResponse.json();

    // Convert amount to EURO currency
    const formatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 2
    });
    iban = accountInfo.bank_account.iban;

    // Set account info elements to the fetched variables
    emailElement.textContent = "Email: "+ accountInfo.email;
    ibanElement.textContent = "IBAN: "+ iban;
    balanceElement.textContent = "Balance: "+ formatter.format(accountInfo.bank_account.balance);
}

async function getTransactions() {

    // Fetch account info with access token
    const transactionsResponse = await fetch("http://localhost:8080/api/account/transactions",{
        method: 'get',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        })
    });
    if (!transactionsResponse.ok) {
        await syncTokens();
        await getTransactions();
        return;
    }
    const transactionObj = (await transactionsResponse.json()).transactions;


    const transactionList = Object.values(transactionObj);

    // Create table rows for transactions
    transactionList.forEach(item => {
        const tr = document.createElement("tr");
        for (let value in item) {
            const td = document.createElement("td");
            td.innerText = item[value];
            tr.appendChild(td);
        }
        transactionTable.appendChild(tr);
    });
}

async function loadAccountContent() {
    await getAccountInfo();
    await getTransactions();
}

function validateAmount(amount) {
    if(amount < 0.01) {
        return "Invalid amount"
    }
    if (amount > 1000) {
        return "Max is 1.000";
    }
    return "OK";
}

async function deposit() {
    const formData = new FormData(depositForm);

    // Convert form into object
    const jsonObj = ["amount"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // Validate user input (For user experience)
    const validationResponse = validateAmount(jsonObj.amount);
    if (validationResponse!=="OK") {
        promptFeedback(depositFeedback, validationResponse, redHex);
        return;
    }

    // Post deposit request
    const depositResponse = await fetch("http://localhost:8080/api/account/deposit", {
        method: 'post',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        }),
        body: JSON.stringify(jsonObj)
    })
    if (!depositResponse.ok) {

        // Get error message
        const message = (await depositResponse.json())["message"]

        // If access token expired -> request for a new one
        if (message==="Access token is invalid") {
            await syncTokens();
            await deposit();
            return;
        }

        // Prompt server response formatted to be user friendly
        promptFeedback(depositFeedback, (await depositResponse.json())["message"], redHex);
        throw new Error(depositResponse.status + ' ' + depositResponse.statusText);
    }
    depositForm.reset();
    promptFeedback(depositFeedback, "Amount Deposited!", greenHex);
    location.reload();
}

async function transfer() {

}

async function withdraw() {
    const formData = new FormData(withdrawForm);

    // Convert form into object
    const jsonObj = ["amount"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // Validate user input (For user experience)
    const validationResponse = validateAmount(jsonObj.amount);
    if (validationResponse!=="OK") {
        promptFeedback(withdrawFeedback, validationResponse, redHex);
        return;
    }

    // Post withdraw request
    const withdrawResponse = await fetch("http://localhost:8080/api/account/withdraw", {
        method: 'post',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        }),
        body: JSON.stringify(jsonObj)
    })
    if (!withdrawResponse.ok) {

        // Get error message
        const message = (await withdrawResponse.json())["message"]

        // If access token expired -> request for a new one
        if (message==="Access token is invalid") {
            await syncTokens();
            await withdraw();
            return;
        }

        // Prompt server response formatted to be user friendly
        promptFeedback(withdrawFeedback, message, redHex);
        throw new Error(withdrawResponse.status + ' ' + withdrawResponse.statusText);
    }
    withdrawForm.reset();
    promptFeedback(withdrawFeedback, "Amount Withdrawn!", greenHex);
    location.reload();
}

function logOut(errorCausedLogout) {
    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";
    // Replace screen to error if an error occurred
    if (errorCausedLogout) {
        location.replace("error.html");
    }
    else {
        location.replace("home.html");
    }
}

async function deleteAccount() {
    const deleteAccountResponse = await fetch("http://localhost:8080/api/account", {
        method: 'delete',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        }),
    });
    if (!deleteAccountResponse.ok) {

        // Get error message
        const message = (await deleteAccountResponse.json())["message"]

        // If access token expired -> request for a new one
        if (message==="Access token is invalid") {
            await syncTokens();
            await deleteAccount();
            return;
        }
    }
    logOut(false);
}



// Hide content until account info is retrieved
contentBlocks.forEach((element) => {
    element.classList.add("display-none")
});

// Page load event
location.href="#overview";
await loadAccountContent();

// Un-hide content when account content was loaded
contentBlocks.forEach((element) => {
    element.classList.remove("display-none")
});

ibanElement.addEventListener('click', () => {
    navigator.clipboard.writeText(iban.toString());
});

depositButton.addEventListener('click', async () => {
    await deposit();
});

transferButton.addEventListener('click', async () => {
    await transfer();
});

withdrawButton.addEventListener('click', async () => {
    await withdraw();
});

signOutButton.addEventListener('click', async () => {
    await logOut(false);
});

deleteAccountButton.addEventListener('click', async () => {
    await deleteAccount();
});