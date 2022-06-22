// Redirect user back to home page after 2.5 seconds so that they can read the error message
await redirectToHome();

async function redirectToHome() {
    setTimeout(function() {
        window.location.replace("../html/home.html");
    }, 2500);
}