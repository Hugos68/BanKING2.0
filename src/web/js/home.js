const signInButton = document.querySelector(".sign-in-button");
const signUpButton = document.querySelector(".sign-up-button");
const signInForm = document.querySelector(".sign-in-form");
const signUpForm = document.querySelector(".sign-up-form");


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
        // Get tokenPair and refresh in cookies
        // Access token -> get access to resources
        // Refresh token -> get new access token
        const tokenPair = await loginResponse.json();
        let expireDate = new Date;
        expireDate.setFullYear(expireDate.getFullYear());
        document.cookie = "refresh_token="+tokenPair["refresh_token"]+"; expires="+expireDate.toUTCString()+";";

        location.replace("account.html");
    } catch (e) {
        throw new Error(e.message);
    }
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

    try {
        const registrationResponse = await fetch("http://localhost:8080/api/registration",  {
            method:'POST',
            headers: new Headers({
                'Content-Type': 'application/json'
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