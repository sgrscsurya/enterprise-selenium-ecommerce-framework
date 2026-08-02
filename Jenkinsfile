// ============================================================
// Jenkinsfile – Enterprise Selenium Automation Framework
// Phase 7: CI/CD Integration
// ============================================================
//
// Pipeline Modes:
//   Default branch trigger → Smoke → Cross-Browser → Regression
//   Manual "Build with Parameters" → user chooses SUITE, BROWSER, ENV
//
// Jenkins Setup Requirements:
//   - JDK 17 configured in Global Tool Configuration as "JDK_17"
//   - Maven 3.9+ configured in Global Tool Configuration as "MAVEN_3_9"
//   - Agents/nodes with Chrome, Firefox, and Edge installed
//     (or use Docker agent – see commented block below)
//
// Usage:
//   Create a Pipeline job → point SCM to this repo → Jenkinsfile path: Jenkinsfile
// ============================================================

pipeline {

    agent any

    // ── Build Parameters (exposed in "Build with Parameters" UI) ─────
    parameters {
        choice(
            name: 'SUITE',
            choices: ['testng-smoke.xml', 'testng.xml', 'testng-regression.xml', 'testng-crossbrowser.xml'],
            description: 'TestNG suite file to execute'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser for single-browser suite runs'
        )
        choice(
            name: 'ENV',
            choices: ['staging', 'local', 'prod'],
            description: 'Target environment configuration'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode'
        )
        booleanParam(
            name: 'RUN_CROSSBROWSER',
            defaultValue: false,
            description: 'Run cross-browser suite (Chrome + Firefox + Edge) after smoke'
        )
    }

    // ── Tool Versions ────────────────────────────────────────────────
    tools {
        jdk    'JDK_17'
        maven  'MAVEN_3_9'
    }

    // ── Global Environment Variables ─────────────────────────────────
    environment {
        REPORT_DIR       = 'reports'
        SCREENSHOT_DIR   = 'reports/screenshots'
        TIMESTAMP        = sh(script: "date '+%Y%m%d_%H%M%S'", returnStdout: true).trim()
    }

    // ── Pipeline Options ─────────────────────────────────────────────
    options {
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
        timeout(time: 60, unit: 'MINUTES')
        timestamps()                         // Prepend timestamps to all console output
        ansiColor('xterm')                   // Enable ANSI colors in console (requires AnsiColor plugin)
        disableConcurrentBuilds()            // Prevent parallel runs on same branch
    }

    stages {

        // ── Stage 1: Checkout ───────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "\u001B[34m[\u2139] Checking out source code...\u001B[0m"
                checkout scm
                sh 'echo "Branch: $GIT_BRANCH | Commit: $GIT_COMMIT"'
            }
        }

        // ── Stage 2: Build & Compile ────────────────────────────────
        stage('Build & Compile') {
            steps {
                echo "\u001B[34m[\u2139] Compiling project sources...\u001B[0m"
                sh '''
                    mvn test-compile \
                        -B --no-transfer-progress \
                        -DskipTests
                '''
            }
        }

        // ── Stage 3: Smoke Tests ─────────────────────────────────────
        // Always runs – acts as a gate before heavier suites.
        stage('Smoke Tests') {
            steps {
                echo "\u001B[32m[\u25BA] Running Smoke Tests [Browser: ${params.BROWSER}, Env: ${params.ENV}]\u001B[0m"
                sh """
                    mvn test \\
                        -DsuiteFile=testng-smoke.xml \\
                        -Denv=${params.ENV} \\
                        -Dbrowser=${params.BROWSER} \\
                        -Dheadless=${params.HEADLESS} \\
                        -B --no-transfer-progress
                """
            }
            post {
                always {
                    publishExtentReport()
                }
            }
        }

        // ── Stage 4: Cross-Browser Tests ─────────────────────────────
        // Runs three browsers in parallel Jenkins sub-stages.
        stage('Cross-Browser Tests') {
            when {
                anyOf {
                    // Run automatically on main/develop pushes
                    branch 'main'
                    branch 'develop'
                    // Or when manually enabled via parameter
                    expression { return params.RUN_CROSSBROWSER == true }
                }
            }
            parallel {
                stage('Chrome') {
                    steps {
                        echo "\u001B[32m[\u25BA] Cross-Browser: Chrome\u001B[0m"
                        sh """
                            mvn test \\
                                -DsuiteFile=testng-smoke.xml \\
                                -Denv=${params.ENV} \\
                                -Dbrowser=chrome \\
                                -Dheadless=${params.HEADLESS} \\
                                -B --no-transfer-progress
                        """
                    }
                    post {
                        always { publishExtentReport() }
                    }
                }
                stage('Firefox') {
                    steps {
                        echo "\u001B[32m[\u25BA] Cross-Browser: Firefox\u001B[0m"
                        sh """
                            mvn test \\
                                -DsuiteFile=testng-smoke.xml \\
                                -Denv=${params.ENV} \\
                                -Dbrowser=firefox \\
                                -Dheadless=${params.HEADLESS} \\
                                -B --no-transfer-progress
                        """
                    }
                    post {
                        always { publishExtentReport() }
                    }
                }
                stage('Edge') {
                    steps {
                        echo "\u001B[32m[\u25BA] Cross-Browser: Edge\u001B[0m"
                        sh """
                            mvn test \\
                                -DsuiteFile=testng-smoke.xml \\
                                -Denv=${params.ENV} \\
                                -Dbrowser=edge \\
                                -Dheadless=${params.HEADLESS} \\
                                -B --no-transfer-progress
                        """
                    }
                    post {
                        always { publishExtentReport() }
                    }
                }
            }
        }

        // ── Stage 5: Full Regression ─────────────────────────────────
        // Runs only on main branch, after cross-browser passes.
        stage('Full Regression') {
            when {
                branch 'main'
            }
            steps {
                echo "\u001B[32m[\u25BA] Running Full Regression Suite...\u001B[0m"
                sh """
                    mvn test \\
                        -DsuiteFile=testng-regression.xml \\
                        -Denv=${params.ENV} \\
                        -Dbrowser=${params.BROWSER} \\
                        -Dheadless=${params.HEADLESS} \\
                        -B --no-transfer-progress
                """
            }
            post {
                always {
                    publishExtentReport()
                }
            }
        }

    } // end stages

    // ── Post-Pipeline Actions ────────────────────────────────────────
    post {
        always {
            echo "\u001B[34m[\u2139] Archiving test artifacts...\u001B[0m"

            // Archive ExtentReport HTML
            archiveArtifacts(
                artifacts: 'reports/**/*.html',
                allowEmptyArchive: true,
                fingerprint: true
            )

            // Archive all screenshots
            archiveArtifacts(
                artifacts: 'reports/screenshots/**/*.png',
                allowEmptyArchive: true
            )

            // Publish TestNG XML results for Jenkins test trend graph
            script {
                if (fileExists('target/surefire-reports')) {
                    testNG(reportFilenamePattern: 'target/surefire-reports/testng-results.xml')
                }
            }
        }

        success {
            echo "\u001B[32m\u2714 Pipeline completed SUCCESSFULLY.\u001B[0m"
            // Uncomment to enable Slack notifications:
            // slackSend(color: 'good', message: "✅ ${env.JOB_NAME} #${env.BUILD_NUMBER} passed – ${env.BUILD_URL}")
        }

        failure {
            echo "\u001B[31m\u2718 Pipeline FAILED. Check Extent Report for details.\u001B[0m"
            // Uncomment to enable Slack notifications:
            // slackSend(color: 'danger', message: "❌ ${env.JOB_NAME} #${env.BUILD_NUMBER} failed – ${env.BUILD_URL}")
        }

        unstable {
            echo "\u001B[33m\u26A0 Pipeline is UNSTABLE (some tests failed or retried).\u001B[0m"
        }

        cleanup {
            // Clean workspace after every run to avoid stale test-output interference
            cleanWs()
        }
    }

} // end pipeline

// ── Shared Step: Publish Extent Report ───────────────────────────────────
// Requires the "HTML Publisher" Jenkins plugin.
def publishExtentReport() {
    publishHTML(target: [
        allowMissing         : true,
        alwaysLinkToLastBuild: true,
        keepAll              : true,
        reportDir            : 'reports',
        reportFiles          : 'ExtentReport*.html',
        reportName           : "Extent Report – ${env.STAGE_NAME}",
        reportTitles         : 'Automation Test Results'
    ])
}
