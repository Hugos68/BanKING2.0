
const header =document.querySelector("header");

// Mechanism to toggle navigation menu top of page is reached
document.addEventListener("scroll", () => {
    header.classList.toggle("header-pop-out", scrollY < 50);
}, {passive: true});