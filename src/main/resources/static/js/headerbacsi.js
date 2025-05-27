document.addEventListener('DOMContentLoaded', () => {
    const hamburger = document.querySelector('.doctor-header .hamburger');
    const nav = document.querySelector('.doctor-header .header-nav');

    if (hamburger && nav) {
        hamburger.addEventListener('click', () => {
            nav.classList.toggle('active');
            hamburger.innerHTML = nav.classList.contains('active') ? '×' : '☰';
        });
    }
});