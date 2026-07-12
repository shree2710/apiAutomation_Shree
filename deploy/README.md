# Deploying the test runner to EC2

`master` is the canonical branch. The instance clones **`master`** and runs the
[`ec2-userdata.sh`](ec2-userdata.sh) bootstrap, which installs Java 21, Maven,
and Chrome, then runs the smoke suite. No AWS CLI keys are needed — you launch
from the console and the box pulls the public repo itself.

## 1. Launch the instance (AWS console)

EC2 → **Launch instances**:

| Field | Value |
|-------|-------|
| AMI | **Amazon Linux 2023** |
| Architecture | **64-bit (x86)** — Chrome has no Arm Linux build |
| Instance type | **t3.micro** (free-tier eligible) |
| Key pair | create/download a `.pem` (for SSH) |
| Security group | allow **SSH (22)** from **My IP** |
| Advanced details → **User data** | paste the snippet below |

```bash
#!/bin/bash
dnf install -y git
git clone -b master https://github.com/shree2710/apiAutomation_Shree.git /home/ec2-user/app
bash /home/ec2-user/app/deploy/ec2-userdata.sh
```

The `-b master` is required: the repository's default branch does not carry the
framework, so a plain clone would check out the wrong code.

## 2. Verify it ran successfully

Wait ~3–5 minutes for first-boot provisioning, then SSH in:

```bash
chmod 400 /path/to/key.pem
ssh -i /path/to/key.pem ec2-user@<PUBLIC_IP>
```

Check the one-line status marker, then the details:

```bash
cat ~/DEPLOY_STATUS                       # -> "SUCCESS <timestamp>"
cat ~/app/test-output.log                 # full Maven/TestNG output
grep total= ~/app/target/surefire-reports/testng-results.xml
#   -> total="3" passed="3" failed="0" skipped="0"
tail -n 40 /var/log/deploy-apiautomation.log   # full provisioning log
```

**Success = `DEPLOY_STATUS` says `SUCCESS` and `passed="3" failed="0"`.**

## 3. Re-run on demand

```bash
cd ~/app && git pull origin master
SUITE=smoke bash deploy/ec2-userdata.sh              # suite: smoke/regression/api/ui/ci
ENV=prod SUITE=smoke bash deploy/ec2-userdata.sh     # target a profile (config-<ENV>.properties)
```

`SUITE` selects `testng-<SUITE>.xml` (default `smoke`). `ENV` maps to
`-Denv=<ENV>`, overlaying `config-<ENV>.properties` (default: base config only).

## 4. Tear down

Terminate the instance when finished so it stops billing:
EC2 → Instances → select → **Instance state → Terminate**.
