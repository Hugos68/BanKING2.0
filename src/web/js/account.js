const signOutButton = document.querySelector(".sign-out-button");
const emailElement = document.querySelector(".email");
const balanceElement = document.querySelector(".balance");
const refreshToken = getCookie("refresh_token");

// TODO: Hide content until all fetches are successful

let refreshTried = false;
await getAccountInfo();

signOutButton.addEventListener('click', async () => {
    await logOut();
});

async function getAccountInfo() {

    // Get access token from cookie
    const accessToken = getCookie("access_token");

    // Fetch account info with access token
    try {
        const accountInfoResponse = await fetch("http://localhost:8080/api/account", {
            method: 'get',
            headers: new Headers({
                'content-type': 'application/json',
                'Authorization': 'Bearer '+ accessToken
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

    } catch (e) {

        // Prevent fetches from looping by catching a second fetch fail
        if (refreshTried) {
            await logOut();
            location.replace("error.html");
        }
        else {
            refreshTried=true;
            await refreshAccessToken();
            await getAccountInfo();
        }
    }
}

async function logOut() {

    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";

    // Replace screen back to home
    location.replace("home.html");
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



