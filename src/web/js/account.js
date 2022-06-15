const signOutButton = document.querySelector(".sign-out-button");
const emailElement = document.querySelector(".email");
const balanceElement = document.querySelector(".balance");
const refreshToken = getCookie("refresh_token");
const accessToken = getCookie("access_token");

// TODO: Hide content until all fetches are successful

let refreshTried = false;
await getAccountInfo();

signOutButton.addEventListener('click', async () => {
    await logOut();
});

async function getAccountInfo() {
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
        console.log(refreshTried);
        if (refreshTried) {
            //await logOut();
            //location.replace("error.html");
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
        // Get access token from response
        const accessToken = (await refreshAccessResponse.json()).access_token;

        // Create expire date (1 year from now)
        const date = new Date();
        const expireDate = new Date(date.getMinutes()+15);

        // TODO Fix cookie not getting set correctly

        // Set access token cookie with expire date of session
        document.cookie = "access_token="+accessToken
            + "; SameSite=lax"
            + "; expires="+expireDate.toUTCString()+";";
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



