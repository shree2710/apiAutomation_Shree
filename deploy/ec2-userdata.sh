#!/bin/bash
# Provision an Amazon Linux 2023 (x86_64) EC2 instance and run the test suite.
#
# This script is the single deployment entry point. It installs the toolchain,
# then runs the smoke suite against the copy of the repo it lives in (it does
# NOT clone — the caller clones the master branch first, see deploy/README.md).
#
# Usage on the instance (repo already cloned to e.g. /home/ec2-user/app):
#     bash /home/ec2-user/app/deploy/ec2-userdata.sh
#
# Config: SUITE env var picks the TestNG suite (default: smoke).
set -u

SUITE="${SUITE:-smoke}"
LOG=/var/log/deploy-apiautomation.log
# Resolve the repo root as the parent of this script's directory (no re-clone).
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Log everything (works whether run by cloud-init as root or by a user via sudo).
exec > >(sudo tee -a "$LOG" 2>/dev/null || tee -a "$LOG") 2>&1
echo "=== deploy start: $(date) | suite=$SUITE | repo=$REPO_ROOT ==="

# 1) Java 21 (Amazon Corretto) + Git
sudo dnf install -y java-21-amazon-corretto-devel git

# 2) Maven — not in AL2023 repos. Use the Apache ARCHIVE mirror (the primary
#    CDN keeps only the newest releases and 404s on pinned versions).
MAVEN_VERSION=3.9.9
if [ ! -x /usr/local/bin/mvn ]; then
  sudo curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o /opt/maven.tar.gz
  sudo tar -xzf /opt/maven.tar.gz -C /opt
  sudo ln -sf "/opt/apache-maven-${MAVEN_VERSION}/bin/mvn" /usr/local/bin/mvn
fi

# 3) Google Chrome (Selenium Manager auto-fetches the matching chromedriver)
sudo dnf install -y https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm

# 4) Run the suite. Fix git "dubious ownership" (repo may be root-owned after a
#    cloud-init clone), then execute Maven with an explicit JAVA_HOME.
export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
git config --global --add safe.directory "$REPO_ROOT"
cd "$REPO_ROOT"
echo "--- versions ---"; java -version; /usr/local/bin/mvn -version | head -1; google-chrome --version

set -o pipefail
/usr/local/bin/mvn -B test -Dsurefire.suiteXmlFiles="testng-${SUITE}.xml" -Dui.headless=true \
  | tee "$REPO_ROOT/test-output.log"
STATUS=${PIPESTATUS[0]}

# 5) Write a machine-readable marker so success is trivial to verify over SSH.
if [ "$STATUS" -eq 0 ]; then
  echo "SUCCESS $(date)" | sudo tee /home/ec2-user/DEPLOY_STATUS >/dev/null
else
  echo "FAILED  $(date) (mvn exit $STATUS)" | sudo tee /home/ec2-user/DEPLOY_STATUS >/dev/null
fi
sudo chown -R ec2-user:ec2-user "$REPO_ROOT" /home/ec2-user/DEPLOY_STATUS 2>/dev/null || true
echo "=== deploy done: $(date) | status=$STATUS ==="
exit "$STATUS"
