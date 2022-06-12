const signOutButton = document.querySelector(".sign-out-button");

signOutButton.addEventListener('click', async () => {

    // TODO: Remove jwt cookies

    // Set refresh token cookie with expire date of 1 year
    document.cookie = "refresh_token=undefined";
    document.cookie = "access_token=undefined";

    // Replace screen back to home
    location.replace("home.html");
});

