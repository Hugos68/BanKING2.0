const signUpButton = document.querySelector(".sign-up-button");
const signInButton = document.querySelector(".sign-in-button");
const signUpForm = document.querySelector(".sign-up-form");
const signInForm = document.querySelector(".sign-in-form");

// Sign Up Event
signUpButton.addEventListener('click', async () => {
    const formData = new FormData(signUpForm);

    // Convert form into object
    let jsonObj = {
        "name": formData.get("name"),
        "email": formData.get("email"),
        "password": formData.get("password"),
        "confirm_password": formData.get("confirm_password")
    }

    // TODO: Validate register data (Better user experience)

    // Confirm password is redundant after validation
    delete jsonObj["confirm_password"];

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
    let jsonObj = {
        "email": formData.get("email"),
        "password": formData.get("password")
    }

    // TODO: Validate login data (Better user experience)

    try {
        const loginResponse = await fetch("http://localhost:8080/api/login",  {
            method:'POST',
            headers: new Headers({
                'content-type': 'application/json'

            }),
            body: JSON.stringify(jsonObj)
        });
        if (!loginResponse.ok) {
            console.error(loginResponse.status+' '+loginResponse.statusText);
        }
        else {
            // TODO: Retrieve JWT and store it in window.localStorage
        }
    } catch (e) {
        throw new Error("Server timed out... "+e.error());
    }
});


const header = document.querySelector("header");

// Mechanism to toggle navigation menu top of page is reached
document.addEventListener("scroll", () => {
    header.classList.toggle("header-pop-out", scrollY < 50);
}, {passive: true});