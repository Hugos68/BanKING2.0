const signUpButton = document.querySelector(".sign-up-button");
const signInButton = document.querySelector(".sign-in-button");
const signUpForm = document.querySelector(".sign-up-form");
const signInForm = document.querySelector(".sign-in-form");

// Sign Up Event
signUpButton.addEventListener('click', async () => {
    const formData = new FormData(signUpForm);

    // Convert form into object
    const jsonObj = ["email", "password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // TODO: Validate register data (Better user experience), note: pass in formData.get("confirm-password");

    try {
        const registrationResponse = await fetch("http://localhost:8080/api/register",  {
            method:'POST',
            headers: new Headers({
                'content-type': 'application/json'
            }),
            body: JSON.stringify(jsonObj)
        });
        if (!registrationResponse.ok) {
            console.error(registrationResponse.status+' '+registrationResponse.statusText);
        }
        else {
            // User is registered here, maybe confetti?
        }
    } catch (e) {
        throw new Error("Server timed out... "+e.error());
    }
});

// Sign In Event
signInButton.addEventListener('click', async () => {
    const formData = new FormData(signInForm);

    // Convert form into object
    const jsonObj = ["email", "password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // TODO: Validate login data (Better user experience)

    try {
        const loginResponse = await fetch("http://localhost:8080/api/authenticate",  {
            method:'POST',
            headers: new Headers({
                'content-type': 'application/json'

            }),
            body: JSON.stringify(jsonObj)
        });
        if (!loginResponse.ok) new Error(loginResponse.status+' '+loginResponse.statusText);

        // Get JWTs and store in browser local storage
        const JWTs = await loginResponse.json();
        // Access token -> get access to resources
        localStorage.setItem("access_token", JWTs["access_token"]);
        // Refresh token -> get new access token
        localStorage.setItem("refresh_token", JWTs["refresh_token"]);

    } catch (e) {
        throw new Error(e.message);
    }
});

const logo = document.querySelector(".logo");

logo.addEventListener('click', async () => {
    console.log("click")
    logo.classList.toggle(".logo-animation");
}, {passive: true});