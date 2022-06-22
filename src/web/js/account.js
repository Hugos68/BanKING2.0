const contentBlocks = document.querySelectorAll(".sections > *");
const signOutButton = document.querySelector(".sign-out-button");
const deleteAccountButton = document.querySelector(".delete-account-button");
const sortByTypeButton = document.querySelector(".sort-type-button");
const sortByTimestampButton = document.querySelector(".sort-date-button");
const sortByAmountButton = document.querySelector(".sort-amount-button");
const clearTransactionsButton = document.querySelector(".clear-transactions-button");
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
const transactionTableHeader = transactionTable.children[0];
let iban;

import {greenHex, redHex, getCookie, parseJwt, promptFeedback} from "./util.js";

const refreshToken = getCookie("refresh_token");

async function refreshAccessToken() {

    // Send refresh token to server to validate it
    const refreshAccessTokenResponse = await fetch("http://localhost:8080/api/access-token", {
        method: 'get',
        headers: {
            'Authorization': 'Bearer '+ refreshToken
        }
    });
    if (!refreshAccessTokenResponse.ok) {

        // Delete leftover access_token
        document.cookie = "refresh_token=; Max-Age=-99999999;";
        document.cookie = "access_token=; Max-Age=-99999999;";
        logOut(true);
        console.error(await (refreshAccessTokenResponse["message"]));
        return;
    }

    // Get token pair from response
    const tokenPair = await refreshAccessTokenResponse.json();

    // Create expire dates for tokens
    const date = new Date();
    const accessExpire = new Date(date.getTime() + (15 * 60 * 1000));

    // Set access token cookie with expire date of session
    document.cookie = "access_token="+tokenPair.access_token
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

    const accessToken = getCookie("access_token");
    const jsonJwt = parseJwt(accessToken);
    const email = jsonJwt.sub;

    // Fetch account info with access token
    const accountInfoResponse = await fetch("http://localhost:8080/api/app-users/"+email, {
        method: 'get',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ accessToken
        })
    });
    if (!accountInfoResponse.ok) {
        await syncTokens();
        await getAccountInfo();
        console.error((await accountInfoResponse)["message"]);
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

async function getAccountTransactions(limit, sortBy) {
    if (limit===undefined) {
        limit = 0;
    }

    if (sortBy===undefined) {
        sortBy = "id";
    }



    // Fetch account info with access token
    const transactionsResponse = await fetch("http://localhost:8080/api/bank-accounts/"+iban+"/transactions?limit="+limit+"&sortBy="+sortBy,{
        method: 'get',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        })
    });
    if (!transactionsResponse.ok) {
        await syncTokens();
        await getAccountTransactions();
        console.error((await transactionsResponse)["message"]);
        return;
    }

    const transactionObj = (await transactionsResponse.json()).transactions;

    const transactionList = Object.values(transactionObj);

    // Replace all children with header to reset table
    transactionTable.replaceChildren(transactionTableHeader);

    // Create table rows for transactions
    if (transactionList.length!==0) {
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
    else {
        const tr = document.createElement("tr");
        tr.innerText = "No transactions available"
        transactionTable.appendChild(tr);
    }
}

async function loadAccountContent() {
    await getAccountInfo();
    await getAccountTransactions();
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
    const depositResponse = await fetch("http://localhost:8080/api/bank-accounts/"+iban+"/transactions/deposit", {
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
        promptFeedback(depositFeedback, message, redHex);
        console.error(message);
        return;
    }
    promptFeedback(depositFeedback, "Amount Deposited!", greenHex);

    // Refresh account data
    await loadAccountContent();

    // Scroll to overview after 0.5s
    setTimeout(() => {
        document.querySelector("#overview").scrollIntoView({
            behavior: 'smooth'
        });
        setTimeout(depositForm.reset(), 1500);
    }, 250);
}

async function transfer() {
    const formData = new FormData(transferForm);

    // Convert form into object
    const jsonObj = ["iban", "amount"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // Validate user input (For user experience)
    if (jsonObj.iban==="") {
        promptFeedback(transferFeedback, "Iban is missing", redHex);
    }

    const validationResponse = validateAmount(jsonObj.amount);
    if (validationResponse!=="OK") {
        promptFeedback(transferFeedback, validationResponse, redHex);
        return;
    }

    // Post withdraw request
    const transferResponse = await fetch("http://localhost:8080/api/bank-accounts/"+iban+"/transactions/transfer", {
        method: 'post',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        }),
        body: JSON.stringify(jsonObj)
    })
    if (!transferResponse.ok) {

        // Get error message
        const message = (await transferResponse.json())["message"]

        // If access token expired -> request for a new one
        if (message==="Access token is invalid") {
            await syncTokens();
            await withdraw();
            return;
        }

        // Prompt server response formatted to be user friendly
        promptFeedback(transferFeedback, message, redHex);
        console.error(message);
        return;
    }
    promptFeedback(transferFeedback, "Amount Transferred!", greenHex);

    // Refresh account data
    await loadAccountContent();

    // Scroll to overview after 0.5s
    setTimeout(() => {
        document.querySelector("#overview").scrollIntoView({
            behavior: 'smooth'
        });
        setTimeout(transferForm.reset(), 1500);
    }, 250);
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
    const withdrawResponse = await fetch("http://localhost:8080/api/bank-accounts/"+iban+"/transactions/withdraw", {
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
        console.error(message);
        return;
    }
    promptFeedback(withdrawFeedback, "Amount Withdrawn!", greenHex);

    // Refresh account data
    await loadAccountContent();

    // Scroll to overview after 0.5s
    setTimeout(() => {
        document.querySelector("#overview").scrollIntoView({
            behavior: 'smooth'
        });
        setTimeout(withdrawForm.reset(), 1500);
    }, 250);
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
        location.replace("index.html");
    }
}

async function clearTransactions() {

    const clearTransactionsResponse = await fetch("http://localhost:8080/api/bank-accounts/"+iban+"/transactions", {
        method: 'delete',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        }),
    });
    if (!clearTransactionsResponse.ok) {

        // Get error message
        const message = (await clearTransactionsResponse.json())["message"]

        // If access token expired -> request for a new one
        if (message==="Access token is invalid") {
            await syncTokens();
            await deleteAccount();
            return;
        }
        console.error(message);
        return;
    }
    // Scroll to overview after 0.5s
    setTimeout(() => {
        document.querySelector("#overview").scrollIntoView({
            behavior: 'smooth'
        });
        setTimeout(getAccountTransactions(), 1500);
    }, 250);

}

async function deleteAccount() {

    const accessToken = getCookie("access_token");
    const jsonJwt = parseJwt(accessToken);
    const email = jsonJwt.sub;

    const deleteAccountResponse = await fetch("http://localhost:8080/api/app-users/"+email, {
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
        console.error(message);
        return;
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

depositButton.addEventListener('click', async (e) => {
    e.preventDefault();
    await deposit();
});

transferButton.addEventListener('click', async (e) => {
    e.preventDefault();
    await transfer();
});

withdrawButton.addEventListener('click', async (e) => {
    e.preventDefault();
    await withdraw();
});

signOutButton.addEventListener('click', async () => {
    await logOut(false);
});

sortByTimestampButton.addEventListener('click', async () => {
    await getAccountTransactions(0, "timestamp");
});

sortByTypeButton.addEventListener('click', async () => {
    await getAccountTransactions(0, "type");
});

sortByAmountButton.addEventListener('click', async () => {
    await getAccountTransactions(0, "amount");
});

clearTransactionsButton.addEventListener('click', async () => {
    await clearTransactions();
});

deleteAccountButton.addEventListener('click', async () => {
    await deleteAccount();
});