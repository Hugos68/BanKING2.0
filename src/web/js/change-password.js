const changePasswordButton = document.querySelector(".change-password-button");
const changePasswordForm = document.querySelector(".change-password-form");
const changePasswordFeedback = document.querySelector(".password-feedback");

import {greenHex, redHex, getCookie, parseJwt, promptFeedback} from "./util.js";

function logOut(errorCausedLogout) {
    // Set tokens to undefine to counter auto-login, set expires to now plus 1 second to expire them
    document.cookie = "refresh_token=; Max-Age=-99999999;";
    document.cookie = "access_token=; Max-Age=-99999999;";
    // Replace screen to error if an error occurred
    if (errorCausedLogout) {
        location.replace("error.html");
    }
    else {
        location.replace("home.html");
    }
}

async function changePassword() {
    const formData = new FormData(changePasswordForm);

    // Convert form into object
    const jsonObj = ["old-password", "new-password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    const changePasswordResponse = await fetch("http://localhost:8080/api/app-users/"+formData.get("email"), {
        method: 'put',
        headers: new Headers({
            'content-type': 'application/json',
        }),
        body: JSON.stringify(jsonObj)
    });
    if (!changePasswordResponse.ok) {
        promptFeedback(changePasswordFeedback, (await changePasswordResponse.json())["message"], redHex)
        return;
    }
    promptFeedback(changePasswordFeedback, "Password changed", greenHex);
    setTimeout(() => {
        location.replace("../html/account.html");
    }, 250);
}

changePasswordButton.addEventListener('click', async () => {
    await changePassword();
});