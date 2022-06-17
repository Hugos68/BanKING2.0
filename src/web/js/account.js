const contentBlocks = document.querySelectorAll(".section-block");
const signOutButton = document.querySelector(".sign-out-button");
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
const balanceElement = document.querySelector(".balance");

import {greenHex, redHex, getCookie, promptFeedback} from "./util.js";

const refreshToken = getCookie("refresh_token");

// Refresh the access token using the refresh token in the cookies
async function refreshAccessToken() {

    try {
        // Send refresh token to server to get access token
        const refreshAccessResponse = await fetch("http://localhost:8080/api/accesstoken", {
            method: 'get',
            headers: new Headers({
                'content-type': 'application/json',
                'Authorization': 'Bearer '+ refreshToken
            })
        });
        if (!refreshAccessResponse.ok) throw new Error(refreshAccessResponse.status + ' ' + refreshAccessResponse.statusText);

        // Get and set access token from response
        const accessTokenFetched = (await refreshAccessResponse.json()).access_token;

        // Create expire dates for tokens
        const date = new Date();
        const accessExpire = new Date(date.getTime() + (15 * 60 * 1000));

        // Set access token cookie with expire date of session
        document.cookie = "access_token="+accessTokenFetched
            + "; SameSite=lax"
            + "; expires="+accessExpire.toUTCString()+";";
    } catch (e) {

    }

}

// Validate tokens and act accordingly
async function validateTokens(refreshAttempted) {
    // If refresh token is absent -> log user out
    if (getCookie("refresh_token")==="") {
        logOut(true);
        return;
    }

    // If access token is absent -> refresh the access token and try again
    if (getCookie("access_token")==="" && !refreshAttempted) {
        await refreshAccessToken();

        // Set refresh attempted to true to avoid infinite refresh access token loop
        refreshAttempted=true;

        // Once access token was refreshed attempt it again
        await validateTokens(refreshAttempted);
    }
    // If refresh was attempted -> log user out
    else {
        logOut(true);
    }
}

// Sync tokens will validate tokens and give a false refresh attempt
async function syncTokens() {
    const refreshAttempted = false;
    await validateTokens(refreshAttempted);
}

async function getAccountInfo() {

    await syncTokens();
    try {
        // Fetch account info with access token
        const accountInfoResponse = await fetch("http://localhost:8080/api/account", {
            method: 'get',
            headers: new Headers({
                'content-type': 'application/json',
                'Authorization': 'Bearer '+ getCookie("access_token")
            })
        });
        if (!accountInfoResponse.ok) throw new Error(accountInfoResponse.status + ' ' + accountInfoResponse.statusText);

        const accountInfo = await accountInfoResponse.json();
        const formatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 2
        });

        emailElement.textContent = "Email: "+ accountInfo.email;
        balanceElement.textContent = "Balance: "+ formatter.format(accountInfo.bank_account.balance);
    } catch (e) {
        await validateTokens();
    }

}

function validateAmount(amount) {

    // Check if amount is a valid positive integer
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

    try {
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

        promptFeedback(depositFeedback, "Amount Deposited!", greenHex);

        location.reload();
    } catch (e) {
        console.error(e);
    }
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

    try {
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

        promptFeedback(withdrawFeedback, "Amount Withdrawn!", greenHex);

        location.reload();
    } catch (e) {
        console.error(e);
    }

}

function logOut(errorCausedLogout) {

    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";

    if (errorCausedLogout) {
        // Replace screen to error if an error occurred
        location.replace("error.html");
    }
    else {
        // Replace screen back to home
        location.replace("home.html");
    }
}

// Hide content until account info is retrieved
contentBlocks.forEach((element) => {
    element.classList.add("display-none")
});

// Page load event
location.href="#overview";
await getAccountInfo();

contentBlocks.forEach((element) => {
    element.classList.remove("display-none")
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