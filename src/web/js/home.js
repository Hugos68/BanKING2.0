const signInButton = document.querySelector(".sign-in-button");
const signUpButton = document.querySelector(".sign-up-button");
const signInForm = document.querySelector(".sign-in-form");
const signUpForm = document.querySelector(".sign-up-form");
const signInLabel = document.querySelector(".sign-in-feedback");
const signUpLabel = document.querySelector(".sign-up-feedback");

import {greenHex, redHex, getCookie, promptFeedback} from "./util.js";

const refreshToken = getCookie("refresh_token");

// Manipulate the DOM based on if the user is logged in or not
function setPageLoggedIn(loggedIn) {

    if (loggedIn) {
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

async function attemptAutoLogin()  {
    try {
        // Send refresh token to server to validate it
        const refreshResponse = await fetch("http://localhost:8080/api/accesstoken", {
            method: 'get',
            headers: {
                'Authorization': 'Bearer '+ refreshToken
            }
        });
        if (!refreshResponse.ok) {

            // Delete leftover access_token
            document.cookie = "refresh_token=; Max-Age=-99999999;";
            document.cookie = "access_token=; Max-Age=-99999999;";
            setPageLoggedIn(false);
        }
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
function setTokenPairCookies(tokenPair) {

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
    try {
        const signInResponse = await fetch("http://localhost:8080/api/appuser/authentication",  {
            method: 'post',
            headers: new Headers({
                'content-type': 'application/json'
            }),
            body: JSON.stringify(jsonObj)
        });
        if (!signInResponse.ok) {

            // Prompt server response formatted to be user friendly
            promptFeedback(signInLabel, (await signInResponse.json())["message"], redHex);
            return;
        }
        // Reset form
        signInForm.reset();

        // Prompt a success message
        promptFeedback(signInLabel, "Success, signing in...", greenHex);

        // Get token pair from response
        const tokenPair = await signInResponse.json();
        setTokenPairCookies(tokenPair);

        // Redirect to account page
        setTimeout(async () => {
            location.replace("account.html")
        }, 750);
    } catch (e) {
        console.error(e);
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
    try {
        const signUpResponse = await fetch("http://localhost:8080/api/appuser",  {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify(jsonObj)
        });
        if (!signUpResponse.ok) {

            // Prompt server response formatted to be user friendly
            promptFeedback(signUpLabel, (await signUpResponse.json())["message"], redHex);
            return;
        }

        // Reset form
        signUpForm.reset();

        // Prompt a success message
        promptFeedback(signUpLabel, "Registration success!", greenHex);

        // Scroll to sing in section after 0.75 second so feedback message is readable
        setTimeout(() => {
            document.querySelector("#sign-in").scrollIntoView({
                behavior: 'smooth'
            });
        }, 500);

    } catch (e) {
        console.error(e);
    }

}


// Page load event
location.href="#home";
await attemptAutoLogin();

// Sign In Event
signInButton.addEventListener('click', async () => {
    await signIn();
});

// Sign Up Event
signUpButton.addEventListener('click', async () => {
    await signUp();
});

