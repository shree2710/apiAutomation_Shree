// Declarative Jenkins pipeline for the API + UI automation framework.
//
// Intended to run as a MULTIBRANCH PIPELINE so branch/PR jobs trigger automatically
// on code commit / PR merge (via the GitHub webhook). The pollSCM trigger below is a
// fallback for setups without webhooks.
//
// Prerequisites on the Jenkins controller:
//   - Global Tool Configuration entries named 'jdk21' (JDK 21) and 'maven3' (Maven 3.9+)
//   - An agent with Google Chrome installed (the UI smoke test runs headless)
//   - The "HTML Publisher" plugin (for the publishHTML step)
//
// This file is a reviewable deliverable; it executes only on a Jenkins controller.

pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    parameters {
        choice(name: 'SUITE',
               choices: ['smoke', 'regression', 'api', 'ui', 'ci'],
               description: 'TestNG suite to run (maps to testng-<SUITE>.xml)')
        choice(name: 'ENVIRONMENT',
               choices: ['qa', 'staging', 'prod'],
               description: 'Target environment (passed as -Denv, overlays config-<env>.properties)')
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        // Compile gate - fail fast before spending time on tests.
        stage('Build') {
            steps {
                sh 'mvn -B clean test-compile'
            }
        }

        // Quality gate: a non-zero Maven exit (test failure) fails the build.
        stage('Test') {
            steps {
                sh """
                    mvn -B test \
                      -Dsurefire.suiteXmlFiles=testng-${params.SUITE}.xml \
                      -Denv=${params.ENVIRONMENT} \
                      -Dui.headless=true
                """
            }
        }
    }

    post {
        always {
            // Publish execution results and reports (even when the build fails).
            junit testResults: '**/surefire-reports/TEST-*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/extent-report/**, target/failure-analysis.md',
                             allowEmptyArchive: true
            publishHTML(target: [
                reportDir: 'target/extent-report',
                reportFiles: 'index.html',
                reportName: 'Extent Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])
        }
        failure {
            echo 'Build FAILED (compile or test failure). See the Extent Report and target/failure-analysis.md for classified failures.'
        }
    }
}
