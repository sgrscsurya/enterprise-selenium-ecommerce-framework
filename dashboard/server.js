const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
const { spawn } = require('child_process');

const app = express();
const PORT = process.env.PORT || 3000;
const ROOT_DIR = path.resolve(__dirname, '..');
const HISTORY_FILE = path.join(__dirname, 'data', 'history.json');
const REPORTS_DIR = path.join(ROOT_DIR, 'reports');
const SCREENSHOTS_DIR = path.join(REPORTS_DIR, 'screenshots');
const LOGS_DIR = path.join(ROOT_DIR, 'logs');

app.use(cors());
app.use(express.json());

// Serve static frontend dashboard assets
app.use(express.static(path.join(__dirname, 'public')));

// Serve reports and screenshots directly
app.use('/reports-static', express.static(REPORTS_DIR));
app.use('/screenshots-static', express.static(SCREENSHOTS_DIR));
app.use('/logs-static', express.static(LOGS_DIR));

// Explicit page routes
app.get('/', (req, res) => res.sendFile(path.join(__dirname, 'public', 'index.html')));
app.get('/dashboard', (req, res) => res.sendFile(path.join(__dirname, 'public', 'dashboard.html')));

// Ensure data folder and history file exist
if (!fs.existsSync(path.dirname(HISTORY_FILE))) {
  fs.mkdirSync(path.dirname(HISTORY_FILE), { recursive: true });
}
if (!fs.existsSync(HISTORY_FILE)) {
  fs.writeFileSync(HISTORY_FILE, JSON.stringify([], null, 2));
}

// Active Execution State & SSE clients
let activeProcess = null;
let sseClients = [];
let executionLogs = [];
let activeRunInfo = null;

function broadcastSSE(event, data) {
  const payload = `event: ${event}\ndata: ${JSON.stringify(data)}\n\n`;
  sseClients.forEach(res => res.write(payload));
}

function appendLog(text) {
  executionLogs.push(text);
  broadcastSSE('log', { text, timestamp: new Date().toISOString() });
}

function readHistory() {
  try {
    const raw = fs.readFileSync(HISTORY_FILE, 'utf-8');
    return JSON.parse(raw);
  } catch (err) {
    return [];
  }
}

function saveHistoryRecord(record) {
  const history = readHistory();
  history.unshift(record); // put newest first
  fs.writeFileSync(HISTORY_FILE, JSON.stringify(history, null, 2));
}

// API Routes

// 1. GET /api/status - Get current runner status
app.get('/api/status', (req, res) => {
  res.json({
    isRunning: !!activeProcess,
    activeRunInfo: activeRunInfo,
    logCount: executionLogs.length
  });
});

// 2. GET /api/stream - SSE endpoint for live terminal output
app.get('/api/stream', (req, res) => {
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');
  res.flushHeaders();

  // Send initial backlog of logs
  res.write(`event: init\ndata: ${JSON.stringify({ logs: executionLogs, isRunning: !!activeProcess, activeRunInfo })}\n\n`);

  sseClients.push(res);

  req.on('close', () => {
    sseClients = sseClients.filter(client => client !== res);
  });
});

// 3. POST /api/run - Trigger test suite execution
app.post('/api/run', (req, res) => {
  if (activeProcess) {
    return res.status(400).json({ error: 'A test suite execution is already running.' });
  }

  const {
    suiteFile = 'testng.xml',
    headless = false,
    browser = 'chrome',
    env = 'local',
    grid = false
  } = req.body;

  const runId = 'RUN-' + Date.now();
  const startTime = new Date();

  executionLogs = [];
  activeRunInfo = {
    id: runId,
    suiteFile,
    headless: Boolean(headless),
    browser,
    env,
    grid: Boolean(grid),
    startTime: startTime.toISOString(),
    status: 'RUNNING'
  };

  appendLog(`=================================================================`);
  appendLog(`  Starting Execution: ${suiteFile}`);
  appendLog(`  Browser: ${browser} | Headless: ${headless} | Env: ${env} | Grid: ${grid}`);
  appendLog(`  Timestamp: ${startTime.toLocaleString()}`);
  appendLog(`=================================================================\n`);

  broadcastSSE('status', { isRunning: true, activeRunInfo });

  // Build Maven arguments
  const mvnArgs = [
    'test',
    `-DsuiteFile=${suiteFile}`,
    `-Dheadless=${headless}`,
    `-Dbrowser=${browser}`,
    `-Denv=${env}`
  ];

  if (grid) {
    mvnArgs.push('-Dgrid=true');
  }

  // Spawn child process for maven
  // On Windows, maven command is mvn.cmd
  const isWin = process.platform === 'win32';
  const command = isWin ? 'mvn.cmd' : 'mvn';

  try {
    activeProcess = spawn(command, mvnArgs, {
      cwd: ROOT_DIR,
      shell: true,
      env: { ...process.env, FORCE_COLOR: 'true' }
    });
  } catch (err) {
    appendLog(`[ERROR] Failed to launch Maven process: ${err.message}`);
    activeRunInfo.status = 'ERROR';
    broadcastSSE('status', { isRunning: false, activeRunInfo });
    activeProcess = null;
    return res.status(500).json({ error: 'Failed to spawn Maven process: ' + err.message });
  }

  let totalTests = 0, failures = 0, errors = 0, skipped = 0, passes = 0;

  activeProcess.stdout.on('data', (data) => {
    const text = data.toString();
    appendLog(text);

    // Parse standard Surefire test results line
    // e.g. "Tests run: 5, Failures: 0, Errors: 0, Skipped: 0"
    const match = text.match(/Tests run:\s*(\d+),\s*Failures:\s*(\d+),\s*Errors:\s*(\d+),\s*Skipped:\s*(\d+)/i);
    if (match) {
      totalTests = parseInt(match[1], 10);
      failures = parseInt(match[2], 10);
      errors = parseInt(match[3], 10);
      skipped = parseInt(match[4], 10);
      passes = Math.max(0, totalTests - (failures + errors + skipped));
    }
  });

  activeProcess.stderr.on('data', (data) => {
    appendLog(`[STDERR] ${data.toString()}`);
  });

  activeProcess.on('close', (code) => {
    const endTime = new Date();
    const durationSec = Math.round((endTime - startTime) / 1000);
    const finalStatus = (code === 0 && failures === 0 && errors === 0) ? 'PASSED' : 'FAILED';

    appendLog(`\n=================================================================`);
    appendLog(`  Execution Completed with Exit Code ${code}`);
    appendLog(`  Status: ${finalStatus} | Duration: ${durationSec}s`);
    appendLog(`  Total: ${totalTests} | Passed: ${passes} | Failed: ${failures + errors} | Skipped: ${skipped}`);
    appendLog(`=================================================================\n`);

    const record = {
      ...activeRunInfo,
      endTime: endTime.toISOString(),
      durationSeconds: durationSec,
      exitCode: code,
      status: finalStatus,
      results: { total: totalTests, passed: passes, failed: failures + errors, skipped }
    };

    saveHistoryRecord(record);

    broadcastSSE('finished', { record });
    broadcastSSE('status', { isRunning: false, activeRunInfo: record });

    activeProcess = null;
  });

  res.json({ message: 'Test execution launched successfully.', runId });
});

// 4. POST /api/stop - Abort active execution
app.post('/api/stop', (req, res) => {
  if (!activeProcess) {
    return res.status(400).json({ error: 'No test suite execution is currently active.' });
  }

  appendLog('\n[WARN] Abort signal received. Terminating test suite execution...');
  
  if (process.platform === 'win32') {
    spawn('taskkill', ['/pid', activeProcess.pid, '/f', '/t']);
  } else {
    activeProcess.kill('SIGKILL');
  }

  res.json({ message: 'Execution termination requested.' });
});

// 5. GET /api/history - Return execution history
app.get('/api/history', (req, res) => {
  res.json(readHistory());
});

// 6. DELETE /api/history - Clear execution history
app.delete('/api/history', (req, res) => {
  fs.writeFileSync(HISTORY_FILE, JSON.stringify([], null, 2));
  res.json({ message: 'History cleared.' });
});

// 7. GET /api/screenshots - List all screenshots with details
app.get('/api/screenshots', (req, res) => {
  if (!fs.existsSync(SCREENSHOTS_DIR)) {
    return res.json([]);
  }

  try {
    const files = fs.readdirSync(SCREENSHOTS_DIR);
    const screenshots = files
      .filter(f => f.match(/\.(png|jpg|jpeg)$/i))
      .map(f => {
        const filePath = path.join(SCREENSHOTS_DIR, f);
        const stats = fs.statSync(filePath);
        return {
          filename: f,
          url: `/screenshots-static/${f}`,
          sizeBytes: stats.size,
          modifiedTime: stats.mtime.toISOString(),
          isFailure: f.toLowerCase().includes('fail') || f.toLowerCase().includes('error')
        };
      })
      .sort((a, b) => new Date(b.modifiedTime) - new Date(a.modifiedTime));

    res.json(screenshots);
  } catch (err) {
    res.status(500).json({ error: 'Failed to read screenshots: ' + err.message });
  }
});

// 8. GET /api/log-file - Get contents of automation.log
app.get('/api/log-file', (req, res) => {
  const logFile = path.join(LOGS_DIR, 'automation.log');
  if (!fs.existsSync(logFile)) {
    return res.json({ content: 'No automation.log file found yet.' });
  }
  try {
    const content = fs.readFileSync(logFile, 'utf-8');
    res.json({ content });
  } catch (err) {
    res.status(500).json({ error: 'Failed to read log file: ' + err.message });
  }
});

const server = app.listen(PORT, () => {
  console.log(`=======================================================`);
  console.log(`  Enterprise Selenium Dashboard Server Running`);
  console.log(`  Dashboard URL: http://localhost:${PORT}`);
  console.log(`  Root Project Directory: ${ROOT_DIR}`);
  console.log(`=======================================================`);
});

server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(`\n[ERROR] Port ${PORT} is already in use by another process.`);
    console.error(`Please stop any existing dashboard server or free port ${PORT}.\n`);
    process.exit(1);
  } else {
    console.error('[ERROR] Server error:', err);
  }
});

