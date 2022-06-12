const signInButton = document.querySelector(".sign-in-button");
const signUpButton = document.querySelector(".sign-up-button");
const signInForm = document.querySelector(".sign-in-form");
const signUpForm = document.querySelector(".sign-up-form");
const refreshToken = getCookie("refresh_token");

document.addEventListener("DOMContentLoaded", async () => {

    // Send refresh token to server to validate it
    try {
        const refreshResponse = await fetch("http://localhost:8080/api/token/refresh", {
            method: 'get',
            headers: {
                'Authorization': 'Bearer '+refreshToken
            }
        });
        if (!refreshResponse.ok) {
            setPageLoggedIn(false)
        }
        else {
            // Get token pair from response
            const tokenPair = refreshResponse.json();

            // Create expire date (1 year from now)
            const date = new Date();
            const expireDate = new Date(date.getMinutes()+15);

            // Set access token cookie in session
            document.cookie = "access_token="+tokenPair["access_token"]
                + "; SameSite=lax"
                + "; expires="+expireDate.toUTCString()+";";

            // Set login page to true
            setPageLoggedIn(true)
        }
    }
    catch (e) {
        setPageLoggedIn(false);
    }
})


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

// Manipulate the DOM based on if the user is logged in or not
function setPageLoggedIn(boolean) {

    if (boolean) {
        document.querySelector(".sign-in").classList.add("display-none");
        document.querySelector(".sign-up").classList.add("display-none");
        document.querySelector(".nav-sign-in").classList.add("display-none");
        document.querySelector(".nav-sign-up").classList.add("display-none");
        document.querySelector(".nav-account").classList.remove("display-none");
    }
    else {
        document.querySelector(".nav-account").classList.add("display-none");
        document.querySelector(".sign-in").classList.remove("display-none");
        document.querySelector(".sign-up").classList.remove("display-none");
        document.querySelector(".nav-sign-in").classList.remove("display-none");
        document.querySelector(".nav-sign-up").classList.remove("display-none");
    }
}


// Sign In Event
signInButton.addEventListener('click', async () => {
    const formData = new FormData(signInForm);

    // Convert form into object
    const jsonObj = ["email", "password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // TODO: Validate login data (Better user experience)

    const loginResponse = await fetch("http://localhost:8080/api/authenticate",  {
        method:'POST',
        headers: new Headers({
            'content-type': 'application/json'
        }),
        body: JSON.stringify(jsonObj)
    });
    if (!loginResponse.ok) throw new Error(loginResponse.status+' '+loginResponse.statusText);

    // Get token pair from response
    const tokenPair = await loginResponse.json();

    // Create expire dates for tokens
    const date = new Date();
    const refreshExpire = new Date(date.getTime() + (7 * 24 * 60 * 60 * 1000));
    const accessExpire = new Date(date.getTime() + (15 * 60 * 1000));

    // Set refresh token cookie with expire date of 1 year
    document.cookie = "refresh_token="+tokenPair["refresh_token"]
        + "; SameSite=lax"
        + "; expires="+refreshExpire.toUTCString()+";";

    // Set access token cookie with expire date of session
    document.cookie = "access_token="+tokenPair["access_token"]
        + "; SameSite=lax"
        + "; expires="+accessExpire.toUTCString()+";";

    // Redirect to account page
    location.replace("account.html");
});

// Sign Up Event
signUpButton.addEventListener('click', async () => {
    const formData = new FormData(signUpForm);

    // Convert form into object
    const jsonObj = ["email", "password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // TODO: Validate register data (Better user experience), note: pass in formData.get("confirm-password");

    const registrationResponse = await fetch("http://localhost:8080/api/registration",  {
        method:'POST',
        headers: new Headers({
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(jsonObj)
    });
    if (!registrationResponse.ok) throw new Error(registrationResponse.status+' '+registrationResponse.statusText);

    // User is registered here, maybe confetti?
});