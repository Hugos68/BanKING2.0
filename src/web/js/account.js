const signOutButton = document.querySelector(".sign-out-button");
const emailElement = document.querySelector(".email");
const accessToken = getCookie("access_token");


document.addEventListener('DOMContentLoaded', async () => {
    try {
        const emailResponse = fetch("http://localhost:8080/account/email", {
            method: 'get',
            headers: {
                'Authorization': 'Bearer '+ accessToken
            }
        })
        if (!(await emailResponse).ok);
        else {
            emailElement.innerHTML = (await emailResponse).json().toString();
        }



        const balanceResponse = fetch("http://localhost:8080/account/balance", {

        })

    } catch (e) {
        // TODO: Redirect home (something went wrong authenticating)
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



