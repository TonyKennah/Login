const loginBtn = document.getElementById("loginBtn");
const registerBtn = document.getElementById("registerBtn");
const forgottenBtn = document.getElementById("forgottenBtn");
const resultDisplay = document.getElementById("result-display");
const passwordInput = document.getElementById("password");
const togglePassword = document.getElementById("togglePassword");

loginBtn.addEventListener("click", login);
registerBtn.addEventListener("click", registerUser);
forgottenBtn.addEventListener("click", forgottenPassword);

let config = {};
document.addEventListener('DOMContentLoaded', fetchConfig);

togglePassword.addEventListener('click', function (e) {
    // toggle the type attribute
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordInput.setAttribute('type', type);
    // toggle the eye slash icon
    this.textContent = type === 'password' ? '👁️' : '🙈';
});

async function fetchConfig() {
    try {
    const response = await fetch("http://localhost:8080/auth/config");
    if (response.ok) {
        config = await response.json();
        // Update the logo if a URL is provided
        if (config.logoUrl && config.logoUrl.length > 0 && config.logoUrl !== 'Login') {
        const logoContainer = document.querySelector('h1');
        logoContainer.innerHTML = ''; // Clear the 'Login' text
        const img = document.createElement('img');
        img.src = config.logoUrl;
        img.alt = 'Application Logo';
        img.style.maxHeight = '60px'; // Example styling
        logoContainer.appendChild(img);
        }
    } else {
        console.error("Failed to fetch config from server.");
        resultDisplay.textContent = "Could not load app configuration.";
    }
    } catch (error) {
    console.error("Error fetching config:", error);
    }
}

async function login() {
    resultDisplay.textContent = "Logging in...";
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    if (response.ok) {
        const data = await response.json();
        localStorage.setItem("jwt", data.token);
        resultDisplay.textContent = "Logged in successfully! Tokens stored.";
        if (config.appUrl && config.appUrl.length > 0) {
        const url = config.appUrl.startsWith('http') ? config.appUrl : `/${config.appUrl}`;
        window.open(url, '_self'); // Open in the same window for better UX
        }
    } else {
        const errorText = await response.text();
        resultDisplay.textContent = `Login failed: ${response.status} ${errorText}`;
    }
    } catch (error) {
    resultDisplay.textContent = `An error occurred: ${error.message}`;
    }
}

function registerUser() {
    if (config.registerUrl && config.registerUrl.length > 0) {
    // Ensure it's treated as a relative path from the root if it doesn't contain 'http'
    const url = config.registerUrl.startsWith('http') ? config.registerUrl : `/${config.registerUrl}`;
    window.open(url, '_blank');
    resultDisplay.textContent = `Opening registration page...`;
    } else {
    resultDisplay.textContent = "Registration URL is not configured on the server.";
    }
}

function forgottenPassword() {
    if (config.forgottenPasswordUrl && config.forgottenPasswordUrl.length > 0) {
    // Ensure it's treated as a relative path from the root if it doesn't contain 'http'
    const url = config.forgottenPasswordUrl.startsWith('http') ? config.forgottenPasswordUrl : `/${config.forgottenPasswordUrl}`;
    window.open(url, '_blank');
    resultDisplay.textContent = `Opening forgotten password page...`;
    } else {
    resultDisplay.textContent = "Forgotten password URL is not configured on the server.";
    }
}