const refreshToken = getCookie("refresh_token");

if (refreshToken!=="") {
    try {
        const refreshResponse = fetch("http://localhost:8080/api/token/refresh", {
            method: 'get',
            headers: {
                'Authorization': 'Bearer '+refreshToken
            }
        });
        if (!refreshResponse.ok) new Error(refreshResponse.status+" "+refreshResponse.statusText);
    } catch (e) {
        setPageLoggedIn(false);
        throw new Error(e.message);
    }
    setPageLoggedIn(true)
}
else {
    setPageLoggedIn(false)
}

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

// This function will show/hide certain things based on if the user is logged in or not
function setPageLoggedIn(boolean) {
    if (boolean) {
        document.querySelector(".sign-in").classList.add("display-none");
        document.querySelector(".sign-up").classList.add("display-none");
        document.querySelector(".nav-sign-in").classList.add("display-none");
        document.querySelector(".nav-sign-up").classList.add("display-none");
    }
    else {
        document.querySelector(".account-icon").classList.add("display-none");
    }
}