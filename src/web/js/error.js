// Redirect user back to home page after 2.5 seconds so that they can read the error message
async function redirectToHome() {
    setTimeout(function() {
        window.location.replace("../html/index.html");
    }, 2500);
}

await redirectToHome();