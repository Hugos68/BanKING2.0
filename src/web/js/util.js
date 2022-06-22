const greenHex = '#228B22';
const redHex = '#F47174';

// Get cookie from name, returns null if cookie was not found
function getCookie(cname) {
    const name = cname + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function parseJwt(token) {
    if (!token.includes('.')) {
        return null;
    }
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

let fading = false;

function promptFeedback(element, text, color) {
    if (!fading) {
        fading = true;
        element.innerText = text;
        element.style.color = color;
        element.classList.add("feedback-label-fade");
        setTimeout(async() => {
            element.classList.remove("feedback-label-fade");
            fading = false;
            element.innerText = "";
        }, 2000);
    }
}

export {
    greenHex,
    redHex,
    getCookie,
    parseJwt,
    promptFeedback
}