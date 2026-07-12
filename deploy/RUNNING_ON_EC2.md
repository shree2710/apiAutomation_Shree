# Running the automation on EC2 (like local)

Same repo, same Maven commands as your machine — you just SSH in first and view
the report over HTTP. Assumes the instance was provisioned per
[`deploy/README.md`](README.md) (Java 21, Maven, Chrome; repo at `~/app`).

## 1. Connect

```bash
chmod 400 /path/to/key.pem                 # once
ssh -i /path/to/key.pem ec2-user@<PUBLIC_IP>
cd ~/app
```

## 2. Make `java` / `mvn` bare-callable (one-time, on the instance)

The toolchain is installed but not on the login PATH by default. Add it once:

```bash
cat >> ~/.bashrc <<'EOF'
export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
export PATH=$JAVA_HOME/bin:/usr/local/bin:$PATH
alias smoke='mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Dui.headless=true'
EOF
source ~/.bashrc
```

Now `java -version` and `mvn -version` work exactly like local.

## 3. Run tests (identical commands to local)

```bash
# a suite: smoke / regression / api / ui / ci
mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Dui.headless=true

# with an environment profile (config-<env>.properties)
mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Denv=prod -Dui.headless=true

# a single class
mvn test -Dtest=apitests.StoreTest

# the alias from step 2
smoke
```

> **Always pass `-Dui.headless=true` on EC2** — there is no display, so a headed
> browser would fail. (It is already the config default; passing it is explicit.)

Or use the deploy wrapper (installs anything missing, writes `~/DEPLOY_STATUS`):

```bash
SUITE=smoke bash deploy/ec2-userdata.sh
ENV=prod SUITE=regression bash deploy/ec2-userdata.sh
```

## 4. Check results

```bash
cat ~/DEPLOY_STATUS                                         # SUCCESS / FAILED marker
grep -o 'passed="[0-9]*" failed="[0-9]*"' \
     target/surefire-reports/testng-results.xml            # pass/fail counts
less target/surefire-reports/TestSuite.txt                 # per-test detail
```

Or open the **Extent report** in a browser: `http://<PUBLIC_IP>` (served by the
`extent-report` systemd service; it auto-reflects the latest run).

## 5. Pull the latest code

```bash
cd ~/app && git pull origin master
```

## Local vs EC2 — the only differences

| | Local | EC2 |
|---|---|---|
| Get there | already there | `ssh ec2-user@<PUBLIC_IP>` |
| Java | any local JDK 21+ | Corretto 21 via `JAVA_HOME` |
| Browser | headed or headless | **must** be `-Dui.headless=true` |
| Run command | `mvn test -Dsurefire...` | **the same** |
| See report | open the HTML file | `http://<PUBLIC_IP>` |

## Report server (optional, already set up on the demo instance)

```bash
sudo systemctl status extent-report      # state of the HTTP server
sudo systemctl restart extent-report     # after moving/regenerating the report
```
Requires an inbound **HTTP (80)** rule in the security group (source: **My IP**).

## Remember

Terminate the instance when finished so it stops billing:
EC2 → Instances → select → **Instance state → Terminate**.
