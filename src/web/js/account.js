const signOutButton = document.querySelector(".sign-out-button");

signOutButton.addEventListener('click', async () => {

    // Create date 1 second from now
    const date = new Date();
    const expires = new Date(date.getSeconds()+1);

    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=undefined; expires="+expires.toUTCString()+";";
    document.cookie = "access_token=undefined; expires="+expires.toUTCString()+";";

    // Replace screen back to home
    location.replace("home.html");
});

