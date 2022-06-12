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
        if (!loginResponse.ok) throw new Error(loginResponse.status+' '+loginResponse.statusText);
        // Get token pair from response
        const tokenPair = await loginResponse.json();

        // Create expire date (1 year from now)
        const date = new Date();
        const expireDate = new Date(date.getFullYear() + 1, date.getMonth(), date.getDate());

        // Set refresh token cookie with expire date of 1 year
        document.cookie = "refresh_token="+tokenPair["refresh_token"]
            + "; SameSite=lax"
            + "; expires="+expireDate.toUTCString()+";";

        // Redirect to account page
        location.replace("account.html");
        
    } catch (e) {
        console.error(e.message);
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
        if (!registrationResponse.ok) throw new Error(registrationResponse.status+' '+registrationResponse.statusText);

        // User is registered here, maybe confetti?

    } catch (e) {
        console.error(e.message)
    }
});