document.addEventListener('click', function (e) {
    const icon = e.target.closest('.toggle-password');
    if (!icon) return;

    const input = document.getElementById(icon.dataset.target);
    if (!input) return;

    const isPassword = input.type === 'password';
    input.type = isPassword ? 'text' : 'password';
    icon.classList.toggle('bi-eye');
    icon.classList.toggle('bi-eye-slash');
});