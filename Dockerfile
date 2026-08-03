FROM maven:3.9.6-eclipse-temurin-17

# Install Node.js 20 & Chromium driver for headless Selenium in Linux cloud environments
RUN apt-get update && apt-get install -y \
    curl \
    gnupg \
    wget \
    chromium \
    chromium-driver \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
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
