const signOutButton = document.querySelector(".sign-out-button");


document.addEventListener('DOMContentLoaded0', async () => {
    try {
        // TODO: Fetch email, balance and iban with access_token in cookies

    } catch (e) {
        // TODO: Redirect home (something went wrong authenticating)
    }
});

signOutButton.addEventListener('click', async () => {

    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";

    // Replace screen back to home
    location.replace("home.html");
});

