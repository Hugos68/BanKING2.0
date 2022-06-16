const contentBlocks = document.querySelectorAll(".section-block");
const signOutButton = document.querySelector(".sign-out-button");
const depositButton = document.querySelector(".deposit-button");
const depositForm = document.querySelector(".deposit-form");
const transferButton = document.querySelector(".transfer-button");
const transferForm = document.querySelector(".transfer-form");
const withdrawButton = document.querySelector(".withdraw-button");
const withdrawForm = document.querySelector(".withdraw-form");
const emailElement = document.querySelector(".email");
const balanceElement = document.querySelector(".balance");
const refreshToken = getCookie("refresh_token");


// Hide content until account info is retrieved
contentBlocks.forEach((element) => {
    element.classList.add("display-none")
});

let refreshAttempted = false;
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
    await logOut();
});



async function getAccountInfo() {

    await validateTokens();

    // Fetch account info with access token
    const accountInfoResponse = await fetch("http://localhost:8080/api/account", {
        method: 'get',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ getCookie("access_token")
        })
    });
    if (!accountInfoResponse.ok) throw new Error(accountInfoResponse.status + ' ' + accountInfoResponse.statusText);
    else {
        const accountInfo = await accountInfoResponse.json();

        const formatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 2
        });

        emailElement.textContent = "Email: "+ accountInfo.email;
        balanceElement.textContent = "Balance: "+ formatter.format(accountInfo.bank_account.balance);
    }
}

async function validateTokens() {

    if (getCookie("refresh_token")==="") {
        logOut();
        return;
    }

    if (getCookie("access_token")==="" && !refreshAttempted) {
        refreshAttempted=true;
        await refreshAccessToken();
        await validateTokens();
    }
    else if (getCookie("access_token")==="") {
        logOut();
    }

}

async function refreshAccessToken() {

    // Send refresh token to server to get access token
    const refreshAccessResponse = await fetch("http://localhost:8080/api/accesstoken", {
        method: 'get',
        headers: new Headers({
            'content-type': 'application/json',
            'Authorization': 'Bearer '+ refreshToken
        })
    });
    if (!refreshAccessResponse.ok);
    else {
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
}



async function deposit() {
    const formData = new FormData(depositForm);

    // Convert form into object
    const jsonObj = ["amount"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    try {
        const depositResponse = await fetch("http://localhost:8080/api/account/deposit", {
            method: 'post',
            headers: new Headers({
                'content-type': 'application/json',
                'Authorization': 'Bearer '+ getCookie("access_token")
            })
        })

    } catch(e) {

    }
}

async function transfer() {
    const formData = new FormData(transferForm);

    // Convert form into object
    const jsonObj = ["amount"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    try {
        const transferResponse = await fetch("http://localhost:8080/api/account/transfer")
    } catch(e) {

    }

}

async function withdraw() {
    const formData = new FormData(withdrawForm);

    // Convert form into object
    const jsonObj = ["amount"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    try {
        const withdrawResponse = fetch("http://localhost:8080/api/account/withdraw")
    } catch(e) {

    }

}




function logOut() {

    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";

    // Replace screen back to home
    location.replace("error.html");
}

// Get cookie from name, returns null if cookie was not found
function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for (let i = 0; i <ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}



