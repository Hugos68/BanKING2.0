
const header =document.querySelector("header");

document.addEventListener("scroll", e => {
    header.classList.toggle("header-pop-out", scrollY < 50);
}, {passive: true});
// This setting is particularly important for scroll based listeners - https://web.dev/uses-passive-event-listeners/

