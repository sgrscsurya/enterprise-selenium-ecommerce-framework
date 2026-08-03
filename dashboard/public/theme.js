/**
 * Shared Theme Manager for SA Selenium Automation Platform
 * Supports Dark (default) and Light mode with smooth CSS transitions
 * and cross-tab/iframe synchronization.
 */
(function() {
  const STORAGE_KEY = 'sa_platform_theme';

  function getSavedTheme() {
    return localStorage.getItem(STORAGE_KEY) || 'dark';
  }

  function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem(STORAGE_KEY, theme);

    // Sync icons and text if theme toggle button exists
    const toggleBtns = document.querySelectorAll('.theme-toggle-btn');
    toggleBtns.forEach(btn => {
      const icon = btn.querySelector('.theme-toggle-icon, i');
      const text = btn.querySelector('.theme-toggle-text');
      if (theme === 'light') {
        if (icon) icon.className = 'fa-solid fa-sun';
        if (text) text.textContent = 'Light';
        btn.setAttribute('title', 'Switch to Dark Mode');
      } else {
        if (icon) icon.className = 'fa-solid fa-moon';
        if (text) text.textContent = 'Dark';
        btn.setAttribute('title', 'Switch to Light Mode');
      }
    });

    // Notify iframe (ExtentReport) if present
    const iframe = document.getElementById('reportIframe');
    if (iframe && iframe.contentWindow) {
      try {
        iframe.contentWindow.postMessage({ type: 'SET_THEME', theme: theme }, '*');
      } catch (e) {}
    }
  }

  function toggleTheme() {
    const current = getSavedTheme();
    const next = current === 'dark' ? 'light' : 'dark';
    applyTheme(next);
  }

  // Initialize as early as possible
  const initialTheme = getSavedTheme();
  document.documentElement.setAttribute('data-theme', initialTheme);

  document.addEventListener('DOMContentLoaded', () => {
    applyTheme(getSavedTheme());

    // Bind all theme toggle buttons on page
    document.querySelectorAll('.theme-toggle-btn').forEach(btn => {
      btn.addEventListener('click', toggleTheme);
    });
  });

  // Listen for storage changes from other tabs
  window.addEventListener('storage', (e) => {
    if (e.key === STORAGE_KEY) {
      applyTheme(e.newValue || 'dark');
    }
  });

  // Expose global
  window.SATheme = {
    get: getSavedTheme,
    set: applyTheme,
    toggle: toggleTheme
  };
})();
