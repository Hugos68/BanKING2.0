const changePasswordButton = document.querySelector(".change-password-button");
const changePasswordForm = document.querySelector(".change-password-form");

import {greenHex, redHex, getCookie, parseJwt, promptFeedback} from "./util.js";


async function changePassword() {
    const formData = new FormData(changePasswordForm);

    // Convert form into object
    const jsonObj = ["old-password", "new-password"].reduce((res, key) => {
        res[key] = formData.get(key);
        return res;
    },{});

    const accessToken = getCookie("access_token");
    const jsonJwt = parseJwt(accessToken);
    const email = jsonJwt.sub;

    const changePasswordResponse = await fetch("http://localhost:8080/app-users/"+email, {
        method: 'put',
        headers: new Headers({
        'content-type': 'application/json',
        'Authorization': 'Bearer '+ accessToken
    })
    }
}



changePasswordButton.addEventListener('click', async () => {
    await changePassword();
});