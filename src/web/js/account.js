const signOutButton = document.querySelector(".sign-out-button");
const emailElement = document.querySelector(".email");
const balanceElement = document.querySelector(".balance");
const accessToken = getCookie("access_token");

// TODO: Hide content until all fetches are successful

await getAccountInfo();

signOutButton.addEventListener('click', async () => {
    await logOut();
});

async function getAccountInfo() {
    try {
        const emailResponse = await fetch("http://localhost:8080/api/account", {
            method: 'get',
            headers: new Headers({
                'content-type': 'application/json',
                'Authorization': 'Bearer '+ accessToken
            })
        });
        if (!emailResponse.ok) throw new Error(emailResponse.status + ' ' + emailResponse.statusText);
        else {
            const accountInfo = await emailResponse.json();

            const formatter = new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'EUR',
                minimumFractionDigits: 2
            });

            emailElement.textContent = "Email: "+ accountInfo.email;
            balanceElement.textContent = "Balance: "+ formatter.format(accountInfo.bank_account.balance);
        }

    } catch (e) {
        location.replace("error.html");
    }
}

async function logOut() {

    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";

    // Replace screen back to home
    location.replace("home.html");
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



