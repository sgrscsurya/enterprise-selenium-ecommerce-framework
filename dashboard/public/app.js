document.addEventListener('DOMContentLoaded', () => {
  // State
  let selectedSuite = 'testng.xml';
  let eventSource = null;
  let autoScroll = true;

  // DOM elements
  const navTabs = document.querySelectorAll('.nav-tab');
  const tabContents = document.querySelectorAll('.tab-content');
  const suiteCards = document.querySelectorAll('.suite-card');
  const headlessToggle = document.getElementById('headlessToggle');
  const browserSelect = document.getElementById('browserSelect');
  const envSelect = document.getElementById('envSelect');

  const runTestBtn = document.getElementById('runTestBtn');
  const stopTestBtn = document.getElementById('stopTestBtn');
  const clearConsoleBtn = document.getElementById('clearConsoleBtn');
  const copyConsoleBtn = document.getElementById('copyConsoleBtn');
  const autoScrollBtn = document.getElementById('autoScrollBtn');

  const terminalBody = document.getElementById('terminalBody');
  const runnerStatusBadge = document.getElementById('runnerStatusBadge');
  const runnerStateText = document.getElementById('runnerStateText');
  const runnerSpinner = document.getElementById('runnerSpinner');

  const refreshReportBtn = document.getElementById('refreshReportBtn');
  const reportIframe = document.getElementById('reportIframe');
  const refreshScreenshotsBtn = document.getElementById('refreshScreenshotsBtn');
  const screenshotsGrid = document.getElementById('screenshotsGrid');
  const refreshHistoryBtn = document.getElementById('refreshHistoryBtn');
  const clearHistoryBtn = document.getElementById('clearHistoryBtn');
  const historyTableBody = document.getElementById('historyTableBody');
  const refreshLogFileBtn = document.getElementById('refreshLogFileBtn');
  const logFileContent = document.getElementById('logFileContent');

  const imageModal = document.getElementById('imageModal');
  const modalImage = document.getElementById('modalImage');
  const modalCaption = document.getElementById('modalCaption');
  const modalCloseBtn = document.getElementById('modalCloseBtn');
  const modalOverlay = document.getElementById('modalOverlay');

  const mobileMenuBtn = document.getElementById('mobileMenuBtn');
  const navTabsEl = document.getElementById('navTabs');

  // ── Mobile menu toggle
  if (mobileMenuBtn && navTabsEl) {
    mobileMenuBtn.addEventListener('click', () => {
      navTabsEl.classList.toggle('open');
    });
  }

  // ── Custom Dropdowns System (Replaces native OS blue popups)
  document.querySelectorAll('.custom-dropdown').forEach(dropdown => {
    const trigger = dropdown.querySelector('.dropdown-trigger');
    const menu = dropdown.querySelector('.dropdown-menu');
    const items = dropdown.querySelectorAll('.dropdown-item');
    const hiddenSelect = dropdown.querySelector('.hidden-select');
    const triggerIcon = dropdown.querySelector('.dropdown-trigger-icon');
    const selectedText = dropdown.querySelector('.dropdown-selected-text');

    if (!trigger || !menu) return;

    trigger.addEventListener('click', (e) => {
      e.stopPropagation();
      document.querySelectorAll('.custom-dropdown').forEach(d => {
        if (d !== dropdown) d.classList.remove('open');
      });
      dropdown.classList.toggle('open');
    });

    items.forEach(item => {
      item.addEventListener('click', (e) => {
        e.stopPropagation();
        const value = item.getAttribute('data-value');
        const iconClass = item.getAttribute('data-icon');
        const textContent = item.textContent.trim();

        items.forEach(i => i.classList.remove('selected'));
        item.classList.add('selected');

        dropdown.setAttribute('data-value', value);
        if (selectedText) selectedText.textContent = textContent;
        if (triggerIcon && iconClass) triggerIcon.className = `${iconClass} dropdown-trigger-icon`;
        if (hiddenSelect) {
          hiddenSelect.value = value;
          hiddenSelect.dispatchEvent(new Event('change'));
        }

        dropdown.classList.remove('open');
      });
    });
  });

  document.addEventListener('click', () => {
    document.querySelectorAll('.custom-dropdown').forEach(d => d.classList.remove('open'));
  });

  // ── Tab navigation
  navTabs.forEach(tab => {
    tab.addEventListener('click', () => {
      const targetTab = tab.getAttribute('data-tab');

      navTabs.forEach(t => t.classList.remove('active'));
      tabContents.forEach(c => c.classList.remove('active'));
      tab.classList.add('active');

      const target = document.getElementById(`tab-${targetTab}`);
      if (target) target.classList.add('active');

      // Close mobile nav on tab select
      if (navTabsEl) navTabsEl.classList.remove('open');

      // Lazy-load tab content
      if (targetTab === 'screenshots') loadScreenshots();
      if (targetTab === 'history') loadHistory();
      if (targetTab === 'logs') loadLogFile();
      if (targetTab === 'reports') refreshReport();
    });
  });

  // ── Suite card selection
  suiteCards.forEach(card => {
    card.addEventListener('click', () => {
      suiteCards.forEach(c => c.classList.remove('active'));
      card.classList.add('active');
      selectedSuite = card.getAttribute('data-suite');
    });
  });

  // ── SSE connection for live console
  function connectSSE() {
    if (eventSource) eventSource.close();
    eventSource = new EventSource('/api/stream');

    eventSource.addEventListener('init', e => {
      const data = JSON.parse(e.data);
      updateRunnerUI(data.isRunning, data.activeRunInfo);
      if (data.logs && data.logs.length > 0) {
        terminalBody.innerHTML = '';
        data.logs.forEach(line => appendTerminalLine(line));
      }
    });

    eventSource.addEventListener('log', e => {
      const data = JSON.parse(e.data);
      appendTerminalLine(data.text);
    });

    eventSource.addEventListener('status', e => {
      const data = JSON.parse(e.data);
      updateRunnerUI(data.isRunning, data.activeRunInfo);
    });

    eventSource.addEventListener('finished', e => {
      const data = JSON.parse(e.data);
      updateRunnerUI(false, data.record);
      refreshReport();
      loadScreenshots();
      loadHistory();
    });

    eventSource.onerror = () => {};
  }

  function appendTerminalLine(text) {
    const div = document.createElement('div');

    if (text.includes('BUILD FAILURE') || text.includes('[ERROR]') || text.includes('FAILED')) {
      div.className = 'log-err';
    } else if (text.includes('[WARN]') || text.includes('WARNING')) {
      div.className = 'log-warn';
    } else if (text.startsWith('===') || text.startsWith('[INFO] Starting')) {
      div.className = 'log-sys';
    }

    div.textContent = text;
    terminalBody.appendChild(div);

    if (autoScroll) {
      terminalBody.scrollTop = terminalBody.scrollHeight;
    }
  }

  function updateRunnerUI(isRunning, runInfo) {
    if (isRunning) {
      runnerStatusBadge.className = 'runner-badge running';
      runnerStateText.textContent = runInfo ? `RUNNING` : 'RUNNING';
      if (runnerSpinner) runnerSpinner.classList.remove('hidden');
      if (runTestBtn) runTestBtn.disabled = true;
      if (stopTestBtn) stopTestBtn.disabled = false;
    } else {
      runnerStatusBadge.className = 'runner-badge idle';
      runnerStateText.textContent = 'IDLE';
      if (runnerSpinner) runnerSpinner.classList.add('hidden');
      if (runTestBtn) runTestBtn.disabled = false;
      if (stopTestBtn) stopTestBtn.disabled = true;
    }
  }

  // ── Custom Centered Dialog Helpers (Replaces native browser popups)
  const dialogModal = document.getElementById('dialogModal');
  const dialogTitle = document.getElementById('dialogTitle');
  const dialogMessage = document.getElementById('dialogMessage');
  const dialogIcon = document.getElementById('dialogIcon');
  const dialogCancelBtn = document.getElementById('dialogCancelBtn');
  const dialogConfirmBtn = document.getElementById('dialogConfirmBtn');
  const dialogOverlay = document.getElementById('dialogOverlay');

  function showConfirm(title, message, iconClass = 'fa-triangle-exclamation') {
    return new Promise((resolve) => {
      if (!dialogModal) { resolve(confirm(message)); return; }
      dialogTitle.textContent = title;
      dialogMessage.textContent = message;
      dialogIcon.innerHTML = `<i class="fa-solid ${iconClass}"></i>`;
      dialogCancelBtn.style.display = 'inline-flex';
      dialogConfirmBtn.textContent = 'Confirm';
      dialogModal.classList.remove('hidden');

      const cleanup = () => {
        dialogModal.classList.add('hidden');
        dialogConfirmBtn.removeEventListener('click', onConfirm);
        dialogCancelBtn.removeEventListener('click', onCancel);
        dialogOverlay.removeEventListener('click', onCancel);
      };

      const onConfirm = () => { cleanup(); resolve(true); };
      const onCancel = () => { cleanup(); resolve(false); };

      dialogConfirmBtn.addEventListener('click', onConfirm);
      dialogCancelBtn.addEventListener('click', onCancel);
      dialogOverlay.addEventListener('click', onCancel);
    });
  }

  function showAlert(title, message, iconClass = 'fa-circle-info') {
    return new Promise((resolve) => {
      if (!dialogModal) { alert(message); resolve(true); return; }
      dialogTitle.textContent = title;
      dialogMessage.textContent = message;
      dialogIcon.innerHTML = `<i class="fa-solid ${iconClass}"></i>`;
      dialogCancelBtn.style.display = 'none';
      dialogConfirmBtn.textContent = 'OK';
      dialogModal.classList.remove('hidden');

      const cleanup = () => {
        dialogModal.classList.add('hidden');
        dialogConfirmBtn.removeEventListener('click', onConfirm);
        dialogOverlay.removeEventListener('click', onConfirm);
      };

      const onConfirm = () => { cleanup(); resolve(true); };

      dialogConfirmBtn.addEventListener('click', onConfirm);
      dialogOverlay.addEventListener('click', onConfirm);
    });
  }

  // ── Run tests
  if (runTestBtn) {
    runTestBtn.addEventListener('click', async () => {
      const dropdownBrowserEl = document.getElementById('dropdownBrowser');
      const dropdownEnvEl = document.getElementById('dropdownEnv');
      const payload = {
        suiteFile: selectedSuite,
        headless: headlessToggle ? headlessToggle.checked : true,
        browser: dropdownBrowserEl ? dropdownBrowserEl.getAttribute('data-value') : (browserSelect ? browserSelect.value : 'chrome'),
        env: dropdownEnvEl ? dropdownEnvEl.getAttribute('data-value') : (envSelect ? envSelect.value : 'local')
      };

      try {
        const res = await fetch('/api/run', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (!res.ok) await showAlert('Launch Failed', data.error || 'Failed to start test execution.', 'fa-circle-exclamation');
      } catch (err) {
        await showAlert('Server Error', 'Could not communicate with backend: ' + err.message, 'fa-triangle-exclamation');
      }
    });
  }

  // ── Abort
  if (stopTestBtn) {
    stopTestBtn.addEventListener('click', async () => {
      const confirmed = await showConfirm('Abort Execution', 'Are you sure you want to stop the running test suite?', 'fa-hand');
      if (!confirmed) return;
      try { await fetch('/api/stop', { method: 'POST' }); } catch (e) {}
    });
  }

  // ── Console controls
  if (clearConsoleBtn) {
    clearConsoleBtn.addEventListener('click', () => {
      terminalBody.innerHTML = '<div class="log-sys">=== Console cleared ===</div>';
    });
  }

  if (copyConsoleBtn) {
    copyConsoleBtn.addEventListener('click', () => {
      navigator.clipboard.writeText(terminalBody.textContent);
    });
  }

  if (autoScrollBtn) {
    autoScrollBtn.addEventListener('click', () => {
      autoScroll = !autoScroll;
      autoScrollBtn.classList.toggle('active', autoScroll);
    });
  }

  // ── Reports
  function refreshReport() {
    if (reportIframe) {
      reportIframe.src = '/reports-static/ExtentReport.html?t=' + Date.now();
    }
  }

  if (refreshReportBtn) refreshReportBtn.addEventListener('click', refreshReport);

  // ── Screenshots
  async function loadScreenshots() {
    if (!screenshotsGrid) return;
    try {
      const res = await fetch('/api/screenshots');
      const files = await res.json();
      screenshotsGrid.innerHTML = '';

      if (!files || files.length === 0) {
        screenshotsGrid.innerHTML = '<p style="color:var(--gray-500);font-size:13px;padding:20px;">No screenshots captured yet.</p>';
        return;
      }

      files.forEach(file => {
        const card = document.createElement('div');
        card.className = 'screenshot-card';
        card.innerHTML = `
          <div class="screenshot-img">
            <img src="${file.url}" alt="${file.filename}" loading="lazy">
          </div>
          <div class="screenshot-info">
            <h4>${file.filename}</h4>
            <p>${new Date(file.modifiedTime).toLocaleString()}</p>
          </div>
        `;
        card.querySelector('.screenshot-img').addEventListener('click', () => {
          modalImage.src = file.url;
          modalCaption.textContent = `${file.filename} — ${new Date(file.modifiedTime).toLocaleString()}`;
          imageModal.classList.remove('hidden');
        });
        screenshotsGrid.appendChild(card);
      });
    } catch (err) {
      screenshotsGrid.innerHTML = '<p style="color:#f87171;font-size:13px;padding:20px;">Failed to load screenshots.</p>';
    }
  }

  if (refreshScreenshotsBtn) refreshScreenshotsBtn.addEventListener('click', loadScreenshots);

  // Modal close
  if (modalCloseBtn) modalCloseBtn.addEventListener('click', () => imageModal.classList.add('hidden'));
  if (modalOverlay) modalOverlay.addEventListener('click', () => imageModal.classList.add('hidden'));
  document.addEventListener('keydown', e => {
    if (e.key === 'Escape') imageModal.classList.add('hidden');
  });

  // ── History
  async function loadHistory() {
    if (!historyTableBody) return;
    try {
      const res = await fetch('/api/history');
      const records = await res.json();
      historyTableBody.innerHTML = '';

      if (!records || records.length === 0) {
        historyTableBody.innerHTML = '<tr><td colspan="9" style="text-align:center;color:var(--gray-500);padding:24px;">No execution records yet.</td></tr>';
        return;
      }

      records.forEach(rec => {
        const tr = document.createElement('tr');
        const res = rec.results || {};
        const isPass = rec.status === 'PASSED';
        tr.innerHTML = `
          <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--gray-400)">${rec.id}</code></td>
          <td><strong>${rec.suiteFile}</strong></td>
          <td><span class="pill ${rec.headless ? 'pill-pass' : ''}">${rec.headless ? 'HEADLESS' : 'HEADED'}</span></td>
          <td>${rec.browser}</td>
          <td>${rec.env}</td>
          <td><span class="pill ${isPass ? 'pill-pass' : 'pill-fail'}">${rec.status}</span></td>
          <td style="font-family:var(--font-mono);">${res.total || 0} / ${res.passed || 0} / ${res.failed || 0}</td>
          <td>${rec.durationSeconds ? rec.durationSeconds + 's' : '—'}</td>
          <td style="color:var(--gray-500)">${new Date(rec.startTime).toLocaleString()}</td>
        `;
        historyTableBody.appendChild(tr);
      });
    } catch (err) {
      historyTableBody.innerHTML = '<tr><td colspan="9" style="color:#f87171;padding:20px;">Error loading history.</td></tr>';
    }
  }

  if (refreshHistoryBtn) refreshHistoryBtn.addEventListener('click', loadHistory);

  if (clearHistoryBtn) {
    clearHistoryBtn.addEventListener('click', async () => {
      const confirmed = await showConfirm('Clear Execution Records', 'Are you sure you want to delete all historical execution records? This action cannot be undone.', 'fa-trash-can');
      if (!confirmed) return;
      await fetch('/api/history', { method: 'DELETE' });
      loadHistory();
    });
  }

  // ── Log file
  async function loadLogFile() {
    if (!logFileContent) return;
    try {
      const res = await fetch('/api/log-file');
      const data = await res.json();
      logFileContent.textContent = data.content;
    } catch (err) {
      logFileContent.textContent = 'Error loading log file.';
    }
  }

  if (refreshLogFileBtn) refreshLogFileBtn.addEventListener('click', loadLogFile);

  // ── Init SSE
  connectSSE();
});
