#!/bin/bash
# EC2 user-data bootstrap for apiAutomation_Shree (Amazon Linux 2023, x86_64)
# Runs as root at first boot. Logs to /var/log/deploy-apiautomation.log
set -x
exec > /var/log/deploy-apiautomation.log 2>&1
echo "=== deploy start: $(date) ==="

# 1) Java 21 (Amazon Corretto) + Git
dnf install -y java-21-amazon-corretto-devel git

# 2) Maven (not in AL2023 repos -> install the Apache binary)
MAVEN_VERSION=3.9.9
curl -fsSL "https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o /opt/maven.tar.gz
tar -xzf /opt/maven.tar.gz -C /opt
ln -sf "/opt/apache-maven-${MAVEN_VERSION}/bin/mvn" /usr/local/bin/mvn

# 3) Google Chrome (Selenium Manager auto-fetches the matching chromedriver)
dnf install -y https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm

# 4) Clone the project (public repo, no credentials needed) and run the smoke suite
export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
cd /home/ec2-user
git clone https://github.com/shree2710/apiAutomation_Shree.git
cd apiAutomation_Shree
git checkout master
mvn -B test -Dsurefire.suiteXmlFiles=testng-smoke.xml | tee /home/ec2-user/test-output.log

# 5) Make results readable by the ec2-user login
chown -R ec2-user:ec2-user /home/ec2-user/apiAutomation_Shree /home/ec2-user/test-output.log
echo "=== deploy done: $(date) ==="
