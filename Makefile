# ============================================================
# Makefile – Enterprise Selenium Framework
# Phase 7: CI/CD Integration & Developer Shortcuts
# ============================================================
#
# Prerequisites: Java 17+, Maven 3.9+, Docker (optional)
#
# Usage:
#   make smoke                 → Run smoke tests locally (headed Chrome)
#   make regression            → Run full regression locally (headed Chrome)
#   make crossbrowser          → Run cross-browser suite locally
#   make ci-smoke              → Run smoke tests headless (staging config)
#   make ci-regression         → Run full regression headless (staging config)
#   make docker-smoke          → Run smoke tests in Docker container
#   make docker-regression     → Run regression in Docker container
#   make grid-start            → Start local Selenium Grid (Docker)
#   make grid-stop             → Stop local Selenium Grid
#   make grid-smoke            → Run smoke tests against local Grid
#   make clean                 → Clean Maven build and reports
#   make compile               → Compile all sources without running tests
#   make report                → Open latest Extent Report in browser
# ============================================================

.PHONY: all smoke regression crossbrowser ci-smoke ci-regression \
        docker-build docker-smoke docker-regression \
        grid-start grid-stop grid-smoke \
        clean compile report help

# ── Default Variables ──────────────────────────────────────
BROWSER    ?= chrome
ENV        ?= local
GRID_URL   ?= http://localhost:4444
MVN        := mvn
REPORT_DIR := reports

# ── Help Target ────────────────────────────────────────────
help:
	@echo ""
	@echo "  ┌─────────────────────────────────────────────────────────┐"
	@echo "  │        Enterprise Selenium Framework – Make Targets       │"
	@echo "  └─────────────────────────────────────────────────────────┘"
	@echo ""
	@echo "  Local Execution:"
	@echo "    make smoke              Run smoke tests (headed Chrome)"
	@echo "    make regression         Run full regression (headed Chrome)"
	@echo "    make crossbrowser       Run cross-browser suite"
	@echo ""
	@echo "  CI / Headless Execution:"
	@echo "    make ci-smoke           Headless smoke against staging"
	@echo "    make ci-regression      Headless regression against staging"
	@echo ""
	@echo "  Docker Execution:"
	@echo "    make docker-build       Build the automation Docker image"
	@echo "    make docker-smoke       Run smoke tests in Docker"
	@echo "    make docker-regression  Run regression in Docker"
	@echo ""
	@echo "  Selenium Grid:"
	@echo "    make grid-start         Start Docker Compose Grid"
	@echo "    make grid-stop          Stop Docker Compose Grid"
	@echo "    make grid-smoke         Run smoke tests via Grid"
	@echo ""
	@echo "  Utilities:"
	@echo "    make compile            Compile all sources"
	@echo "    make clean              Clean build + reports"
	@echo "    make report             Open latest ExtentReport"
	@echo ""

# ── Local Execution ────────────────────────────────────────
smoke:
	@echo "▶ Running SMOKE tests locally..."
	$(MVN) test -DsuiteFile=testng-smoke.xml -Denv=$(ENV) -Dbrowser=$(BROWSER) -Dheadless=false -B

regression:
	@echo "▶ Running REGRESSION tests locally..."
	$(MVN) test -DsuiteFile=testng-regression.xml -Denv=$(ENV) -Dbrowser=$(BROWSER) -Dheadless=false -B

crossbrowser:
	@echo "▶ Running CROSS-BROWSER suite locally..."
	$(MVN) test -DsuiteFile=testng-crossbrowser.xml -Denv=$(ENV) -Dheadless=false -B

# ── CI / Headless Execution ────────────────────────────────
ci-smoke:
	@echo "▶ Running SMOKE tests (headless, staging)..."
	$(MVN) test -Pci-staging -B --no-transfer-progress

ci-regression:
	@echo "▶ Running REGRESSION tests (headless, staging)..."
	$(MVN) test -Pci-regression -B --no-transfer-progress

# ── Docker Execution ───────────────────────────────────────
docker-build:
	@echo "▶ Building Docker image: saucedemo-automation..."
	docker build -t saucedemo-automation .

docker-smoke: docker-build
	@echo "▶ Running SMOKE tests in Docker..."
	docker run --rm \
		-e SUITE=testng-smoke.xml \
		-e ENV=staging \
		-e BROWSER=chrome \
		-e HEADLESS=true \
		-v $(CURDIR)/reports:/app/reports \
		saucedemo-automation

docker-regression: docker-build
	@echo "▶ Running REGRESSION tests in Docker..."
	docker run --rm \
		-e SUITE=testng-regression.xml \
		-e ENV=staging \
		-e BROWSER=chrome \
		-e HEADLESS=true \
		-v $(CURDIR)/reports:/app/reports \
		saucedemo-automation

# ── Selenium Grid ──────────────────────────────────────────
grid-start:
	@echo "▶ Starting Selenium Grid (Hub + Chrome + Firefox nodes)..."
	docker-compose --profile grid up -d
	@echo "✅ Grid UI available at: http://localhost:4444"

grid-stop:
	@echo "▶ Stopping Selenium Grid..."
	docker-compose --profile grid down

grid-smoke:
	@echo "▶ Running SMOKE tests via Selenium Grid..."
	$(MVN) test -DsuiteFile=testng-smoke.xml \
		-Dgrid=true \
		-DgridUrl=$(GRID_URL) \
		-Denv=$(ENV) \
		-Dbrowser=$(BROWSER) \
		-Dheadless=false \
		-B --no-transfer-progress

# ── Utilities ──────────────────────────────────────────────
compile:
	@echo "▶ Compiling project sources..."
	$(MVN) test-compile -B --no-transfer-progress

clean:
	@echo "▶ Cleaning Maven build and reports..."
	$(MVN) clean -B --no-transfer-progress
	rm -rf $(REPORT_DIR)
	@echo "✅ Clean complete."

report:
	@echo "▶ Opening Extent Report..."
	@if [ -f "$(REPORT_DIR)/ExtentReport.html" ]; then \
		start $(REPORT_DIR)/ExtentReport.html 2>/dev/null || \
		open $(REPORT_DIR)/ExtentReport.html 2>/dev/null || \
		xdg-open $(REPORT_DIR)/ExtentReport.html 2>/dev/null; \
	else \
		echo "❌ No report found at $(REPORT_DIR)/ExtentReport.html. Run tests first."; \
	fi
