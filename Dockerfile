FROM maven:3.9.6-eclipse-temurin-17

# Install Node.js 20 & Chromium for headless Selenium in Linux cloud environments
# Note: on recent Debian/Ubuntu bases, chromedriver ships inside the "chromium"
# package itself, so a separate "chromium-driver" package no longer exists there
# and would fail the install with an unmet-dependency error.
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    gnupg \
    wget \
    chromium \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/*

# Set Chrome / Chromedriver environment variables for Linux container
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver

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
