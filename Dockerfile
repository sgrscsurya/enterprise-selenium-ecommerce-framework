FROM maven:3.9.6-eclipse-temurin-17

# Install Node.js 20 & Google Chrome for headless Selenium in Linux cloud environments
# Note: the distro "chromium" apt package is unavailable on recent Ubuntu-based
# images (Ubuntu moved Chromium to snap only), so we install Google Chrome
# directly from Google's own apt repo, which works on both Debian and Ubuntu.
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    gnupg \
    wget \
    && wget -q -O /usr/share/keyrings/google-chrome.gpg.key https://dl.google.com/linux/linux_signing_key.pub \
    && gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg /usr/share/keyrings/google-chrome.gpg.key \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y --no-install-recommends google-chrome-stable \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/*

# Set Chrome environment variable for Linux container.
# Chromedriver is resolved automatically at runtime by Selenium Manager
# (bundled with Selenium 4.6+), so no CHROMEDRIVER_PATH is needed.
ENV CHROME_BIN=/usr/bin/google-chrome-stable

WORKDIR /app

# Copy Maven framework files
COPY pom.xml .
COPY testng*.xml ./
COPY src ./src

# Pre-fetch Maven dependencies
RUN mvn dependency:go-offline -B || true

# Copy Dashboard app
COPY dashboard ./dashboard

WORKDIR /app/dashboard
RUN npm install

EXPOSE 3000
ENV PORT=3000

CMD ["npm", "start"]
