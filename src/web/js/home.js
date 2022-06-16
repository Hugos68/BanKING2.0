const signInButton = document.querySelector(".sign-in-button");
const signUpButton = document.querySelector(".sign-up-button");
const signInForm = document.querySelector(".sign-in-form");
const signUpForm = document.querySelector(".sign-up-form");
const signInLabel = document.querySelector(".sign-in-feedback");
const signUpLabel = document.querySelector(".sign-up-feedback");
const refreshToken = getCookie("refresh_token");
const greenHex = '#228B22';
const redHex = '#F47174';

// Page load event
if (refreshToken!=="") {
    await attemptAutoLogin();
}

// Sign In Event
signInButton.addEventListener('click', async () => {
    await signIn();
});

// Sign Up Event
signUpButton.addEventListener('click', async () => {
    await signUp();
});

async function attemptAutoLogin()  {
    try {
        // Send refresh token to server to validate it
        const refreshResponse = await fetch("http://localhost:8080/api/accesstoken", {
            method: 'get',
            headers: {
                'Authorization': 'Bearer '+ refreshToken
            }
        });
        if (!refreshResponse.ok) setPageLoggedIn(false);
        else {

            // Get token pair from response
            const tokenPair = await refreshResponse.json();

            // Create expire dates for tokens
            const date = new Date();
            const accessExpire = new Date(date.getTime() + (15 * 60 * 1000));

            // Set access token cookie with expire date of session
            document.cookie = "access_token="+tokenPair.access_token
                + "; SameSite=lax"
                + "; expires="+accessExpire.toUTCString()+";";

            // Set login page to true
            setPageLoggedIn(true)
        }
    }
    catch (e) {
        setPageLoggedIn(false);
    }
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

async function signIn() {
    const formData = new FormData(signInForm);

    // Convert form into object
    const jsonObj = ["email", "password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    const validationResponse = validateSignIn(jsonObj);

    if (validationResponse!=="OK") {
        promptFeedback(signInLabel, validationResponse, redHex);
        return;
    }

    const loginResponse = await fetch("http://localhost:8080/api/authentication",  {
        method: 'post',
        headers: new Headers({
            'content-type': 'application/json'
        }),
        body: JSON.stringify(jsonObj)
    });
    if (!loginResponse.ok) {

        // Prompt server response formatted to be user friendly
        promptFeedback(signInLabel, (await loginResponse.json())["message"], redHex);
    }
    else {
        signInForm.reset();
        promptFeedback(signInLabel, "Success, signing in...", greenHex);

        // Get token pair from response
        const tokenPair = await loginResponse.json();

        // Create expire dates for tokens
        const date = new Date();
        const refreshExpire = new Date(date.getTime() + (7 * 24 * 60 * 60 * 1000));
        const accessExpire = new Date(date.getTime() + (15 * 60 * 1000));

        // Set refresh token cookie with expire date of 1 year
        document.cookie = "refresh_token="+tokenPair.refresh_token
            + "; SameSite=lax"
            + "; expires="+refreshExpire.toUTCString()+";";

        // Set access token cookie with expire date of session
        document.cookie = "access_token="+tokenPair.access_token
            + "; SameSite=lax"
            + "; expires="+accessExpire.toUTCString()+";";

        // Redirect to account page
        setTimeout(async () => {
            location.replace("account.html")
        }, 750);
    }
}

function validateSignIn(jsonObj) {

    const email = jsonObj.email;

    if (email === null || email === "") {
        return "Missing Email"
    }

    const password = jsonObj.password;

    if (password === null || password === "") {
        return "Missing Password"
    }

    return "OK";
}

async function signUp() {
    const formData = new FormData(signUpForm);

    // Convert form into object
    const jsonObj = ["email", "password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    const validationResponse = validateSignUp(jsonObj, formData.get("confirm-password"));

    if (validationResponse!=="OK") {
        promptFeedback(signUpLabel, validationResponse, redHex)
        return;
    }

    const registrationResponse = await fetch("http://localhost:8080/api/registration",  {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(jsonObj)
    });
    if (!registrationResponse.ok) {

        // Prompt server response formatted to be user friendly
        promptFeedback(signUpLabel, (await registrationResponse.json())["message"], redHex);
    }
    else {
        signUpForm.reset();
        // User is registered here, maybe confetti?
        promptFeedback(signUpLabel, "Registration success!", greenHex);
    }
}

function validateSignUp(jsonObj, confirmPassword) {

    const email = jsonObj.email

    if (email === null || email === "") {
        return "Missing Email"
    }

    if (!email.match(new RegExp("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\\])"))) {
        return "Invalid Email";
    }

    const password = jsonObj.password

    if (password === null || password === "") {
        return "Missing Password"
    }

    if (password.length < 8) {
        return "Password is too short";
    }

    if (confirmPassword === null || confirmPassword === "") {
        return "Missing Confirm password";
    }

    if (password !== confirmPassword) {
        return "Password mismatch";
    }

    return "OK";
}

let fading = false;
function promptFeedback(element, text, color) {
    if (!fading) {
        fading = true;
        element.innerText = text;
        element.style.color = color;
        element.classList.add("feedback-label-fade");
        setTimeout(async () => {
            element.classList.remove("feedback-label-fade"); fading=false;
        }, 2000);
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

