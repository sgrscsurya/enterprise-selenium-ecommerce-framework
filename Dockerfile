# ============================================================
# Dockerfile – Enterprise Selenium Framework
# Phase 7: CI/CD Integration & Containerization
# ============================================================
#
# Build image:
#   docker build -t saucedemo-automation .
#
# Run smoke tests (headless Chrome):
#   docker run --rm -v $(pwd)/reports:/app/reports saucedemo-automation
#
# Run regression with env override:
#   docker run --rm \
#     -e ENV=staging \
#     -e SUITE=testng-regression.xml \
#     -v $(pwd)/reports:/app/reports \
#     saucedemo-automation
# ============================================================

# ── Stage 1: Maven Build ────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy dependency manifests first to leverage Docker layer caching.
# Dependencies will only be re-downloaded when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress

# Copy entire project source
COPY src ./src
COPY testng.xml .
COPY testng-smoke.xml .
COPY testng-regression.xml .
COPY testng-crossbrowser.xml .

# Compile both main and test sources (do NOT run tests here)
RUN mvn test-compile -B --no-transfer-progress -DskipTests

# ── Stage 2: Runtime with Headless Chrome ──────────────────
FROM eclipse-temurin:17-jdk-jammy

LABEL maintainer="saucedemo-automation@example.com"
LABEL version="1.0"
LABEL description="Enterprise Selenium Automation Framework for SauceDemo"

# Install Google Chrome (stable) + required dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    gnupg \
    ca-certificates \
    fonts-liberation \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libdbus-1-3 \
    libgdk-pixbuf2.0-0 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    xdg-utils \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y --no-install-recommends \
    google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Set up working directory
WORKDIR /app

# Copy compiled artifacts from builder stage
COPY --from=builder /build /app

# Copy Maven local repository cache from builder stage to avoid re-downloads
COPY --from=builder /root/.m2 /root/.m2

# Create directories for reports and screenshots
RUN mkdir -p reports/screenshots

# ── Environment Variables ───────────────────────────────────
# These can all be overridden with docker run -e KEY=VALUE
ENV ENV=local
ENV SUITE=testng-smoke.xml
ENV BROWSER=chrome
ENV HEADLESS=true

# ── Entry Point ─────────────────────────────────────────────
# Runs the configured suite. Override SUITE env var to change which suite runs.
CMD ["sh", "-c", "mvn test \
    -DsuiteFile=${SUITE} \
    -Denv=${ENV} \
    -Dbrowser=${BROWSER} \
    -Dheadless=${HEADLESS} \
    -B --no-transfer-progress"]
