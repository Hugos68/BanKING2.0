const changePasswordButton = document.querySelector(".change-password-button");
const changePasswordForm = document.querySelector(".change-password-form");
const changePasswordFeedback = document.querySelector(".password-feedback");

import {greenHex, redHex, promptFeedback} from "./util.js";


async function changePassword() {
    const formData = new FormData(changePasswordForm);

    // Convert form into object
    const jsonObj = ["old-password", "new-password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    // Fetch password put request
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

changePasswordButton.addEventListener('click', async (e) => {
    e.preventDefault();
    await changePassword();
});