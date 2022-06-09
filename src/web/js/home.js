const header =document.querySelector("header");
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
        if (!registrationResponse.ok) new Error(registrationResponse.status+' '+registrationResponse.statusText);

        // User is registered here, maybe confetti?
    } catch (e) {
        new Error("Server request failed...");
    }
});

// Sign In Event
signInButton.addEventListener('click', async () => {

});












// Mechanism to toggle navigation menu top of page is reached
document.addEventListener("scroll", () => {
    header.classList.toggle("header-pop-out", scrollY < 50);
}, {passive: true});