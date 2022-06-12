const refreshToken = getCookie("refresh_token");

document.addEventListener("DOMContentLoaded", async () => {

    // Send refresh token to server to validate it
    if (refreshToken!=="") {
        const refreshResponse = await fetch("http://localhost:8080/api/token/refresh", {
            method: 'get',
            headers: {
                'Authorization': 'Bearer '+refreshToken
            }
        });

        if (!refreshResponse.ok)  {
            setPageLoggedIn(false)
            throw new Error(refreshResponse.status+" "+refreshResponse.statusText);
        }

        // Get token pair from response
        const tokenPair = refreshResponse.json();

        // Create expire date (1 year from now)
        const date = new Date();
        const expireDate = new Date(date.getMinutes()+15);

        // Set access token cookie in session
        document.cookie = "access_token="+tokenPair["access_token"]
            + "; SameSite=lax"
            + "; expires="+expireDate.toUTCString()+";";

        // Set login page to true
        setPageLoggedIn(true)
    }
    else {
        setPageLoggedIn(false);
    }
})


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

// Manipulate the DOM based on if the user is logged in or not
function setPageLoggedIn(boolean) {
    if (boolean) {
        document.querySelector(".sign-in").classList.add("display-none");
        document.querySelector(".sign-up").classList.add("display-none");
        document.querySelector(".nav-sign-in").classList.add("display-none");
        document.querySelector(".nav-sign-up").classList.add("display-none");
    }
    else {
        document.querySelector(".nav-account").classList.add("display-none");
    }
}