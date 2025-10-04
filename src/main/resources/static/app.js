const loginBtn = document.getElementById("loginBtn");
const registerBtn = document.getElementById("registerBtn");
const forgottenBtn = document.getElementById("forgottenBtn");
const resultDisplay = document.getElementById("result-display");
const passwordInput = document.getElementById("password");
const togglePassword = document.getElementById("togglePassword");

loginBtn.addEventListener("click", login);
registerBtn.addEventListener("click", registerUser);
forgottenBtn.addEventListener("click", forgottenPassword);

const MAX_LOGIN_ATTEMPTS = 3;
const LOCKOUT_DURATION_SECONDS = 30;
let loginAttempts;

let config = {};
document.addEventListener('DOMContentLoaded', () => {
    initializeLoginState();
    fetchConfig();
});

function initializeLoginState() {
    loginAttempts = parseInt(getCookie("loginAttempts")) || 0;
    const lockoutUntil = getCookie("lockoutUntil");

    if (lockoutUntil && new Date().getTime() < parseInt(lockoutUntil)) {
        const remainingTime = Math.ceil((parseInt(lockoutUntil) - new Date().getTime()) / 1000);
        lockUserOut(remainingTime);
    } else {
        // Clear any stale cookies if not locked out
        if (lockoutUntil) deleteCookie("lockoutUntil");
        if (loginAttempts > 0) {
            // If there were attempts but no lockout, we can clear them on refresh
            deleteCookie("loginAttempts");
        }
    }
}

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
    if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
        resultDisplay.textContent = "Maximum login attempts exceeded. Please try again later.";
        loginBtn.disabled = true;
        return;
    }

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
        resetLoginAttempts();
        const data = await response.json();
        localStorage.setItem("jwt", data.token);
        resultDisplay.textContent = "Logged in successfully! Tokens stored.";
        if (config.appUrl && config.appUrl.length > 0) {
        const url = config.appUrl.startsWith('http') ? config.appUrl : `/${config.appUrl}`;
        window.open(url, '_self'); // Open in the same window for better UX
        }
    } else {
        loginAttempts++;
        setCookie("loginAttempts", loginAttempts, 1); // Persist for 1 day
        const remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts;
        const errorText = await response.text();
        let message = `Login failed: ${response.status} ${errorText}.`;

        if (remainingAttempts > 0) {
            message += ` You have ${remainingAttempts} attempt(s) remaining.`;
            resultDisplay.textContent = message;
        } else {
            lockUserOut(LOCKOUT_DURATION_SECONDS);
        }
    }
    } catch (error) {
    resultDisplay.textContent = `An error occurred: ${error.message}`;
    }
}

function lockUserOut(durationInSeconds) {
    const lockoutUntil = new Date().getTime() + durationInSeconds * 1000;
    setCookie("lockoutUntil", lockoutUntil, 1);

    loginBtn.disabled = true;
    resultDisplay.textContent = `You have no attempts remaining. Please wait ${durationInSeconds} seconds to try again.`;

    setTimeout(() => {
        resetLoginAttempts();
        resultDisplay.textContent = "You can now try to log in again.";
    }, durationInSeconds * 1000);
}

function resetLoginAttempts() {
    loginAttempts = 0;
    deleteCookie("loginAttempts");
    deleteCookie("lockoutUntil");
    loginBtn.disabled = false;
}

// --- Cookie Helper Functions ---

function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/; SameSite=Lax";
}

function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function deleteCookie(name) {
    document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
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