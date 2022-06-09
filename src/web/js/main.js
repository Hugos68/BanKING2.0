document.onscroll = async () => {
    if (scrollY<50) {
        document.getElementById('header').classList.add("header-pop-out");
    }
    else {
        document.getElementById('header').classList.remove("header-pop-out");
    }
}

