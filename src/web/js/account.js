const signOutButton = document.querySelector(".sign-out-button");
const emailElement = document.querySelector(".email");
const balanceElement = document.querySelector(".balance");
const accessToken = getCookie("access_token");

// TODO: Hide content until all fetches are successful

// TODO: Handle data and put it in the html

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const emailResponse = await fetch("http://localhost:8080/account", {
            method: 'get',
            headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+ accessToken
                })
        });
        if (!emailResponse.ok) throw new Error(emailResponse.status + ' ' + emailResponse.statusText);
        else {
            const account = emailResponse.json().toString();
        }


    } catch (e) {
        location.replace("error.html");
    }
});

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

signOutButton.addEventListener('click', async () => {

    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";

    // Replace screen back to home
    location.replace("home.html");
});



