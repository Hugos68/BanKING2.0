const greenHex = '#228B22';
const redHex = '#F47174';

// Get cookie from name, returns null if cookie was not found
function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for (let i = 0; i <ca.length; i++) {
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

let fading = false;
function promptFeedback(element, text, color) {
    if (!fading) {
        fading = true;
        element.innerText = text;
        element.style.color = color;
        element.classList.add("feedback-label-fade");
        setTimeout(async () => {
            element.classList.remove("feedback-label-fade"); fading=false;
        }, 2000);
    }
}

export {
    greenHex,
    redHex,
    getCookie,
    promptFeedback
}