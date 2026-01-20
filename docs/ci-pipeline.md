# CI/CD Pipeline Documentation

This document describes the complete Continuous Integration and Continuous Delivery (CI/CD) pipeline for the Elvah Charge Android SDK project.

## Table of Contents

- [Overview](#overview)
- [Local Quality Gates](#local-quality-gates)
- [GitHub Actions Workflows](#github-actions-workflows)
- [Quality Checks](#quality-checks)
- [Pipeline Architecture](#pipeline-architecture)
- [Usage Guide](#usage-guide)
- [Troubleshooting](#troubleshooting)

---

## Overview

The Elvah Charge project implements a comprehensive quality pipeline that catches issues at multiple stages:

1. **Pre-commit Hook** - Catches issues before code is committed
2. **CI Workflow** - Runs parallel checks on every push/PR
3. **Test Coverage** - Monitors code coverage for the SDK
4. **Dependency Review** - Scans for vulnerable dependencies in PRs

This multi-layered approach ensures code quality while providing fast feedback to developers.

---

## Local Quality Gates

### Pre-commit Hook

A git pre-commit hook runs automatically before each commit to catch issues early.

#### Installation

```bash
# Install git hooks
./gradlew installGitHooks

# Or full project setup
./gradlew setupProject
```

The hook is automatically checked when you sync the project. If not installed, you'll see a reminder.

#### What it Checks

The pre-commit hook runs:

1. **Detekt** - Static analysis on changed Kotlin files
   - Checks code complexity, naming conventions, style issues
   - Uses the baseline file to allow existing issues
   - Fast execution (only analyzes changed files)

2. **Android Lint** - Linting on affected modules
   - Always runs on `charge` module (main SDK)
   - Runs on `app` module if app files changed
   - Checks for Android-specific issues, resource problems, API usage

3. **Smart Skipping** - Automatically skips if:
   - No Kotlin or Gradle files changed
   - Only documentation files modified

#### Hook Output Examples

**Success:**
```
🔍 Running pre-commit checks...

📋 Running Detekt on changed files...
✅ Detekt passed

🔎 Running Android Lint on charge module...
✅ Lint passed for charge module

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ All pre-commit checks passed!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

**Failure:**
```
❌ Detekt found issues
❌ Lint found issues in charge module

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
❌ Pre-commit checks FAILED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💡 Tips:
  • Run './gradlew detekt' to see Detekt issues
  • Run './gradlew :charge:lintDebug' for lint issues
  • Check HTML reports in build/reports/ directories
  • Use 'git commit --no-verify' to bypass (emergencies only)
```

#### Bypassing the Hook

In emergencies, bypass with:
```bash
git commit --no-verify
```

**Warning:** Only bypass when absolutely necessary. The hook prevents quality issues from reaching CI.

---

## GitHub Actions Workflows

### 1. Android CI Workflow (`.github/workflows/ci.yml`)

Main CI workflow that runs on every push and pull request.

#### Triggers

**Push Events:**
- Branches: `main`, `develop`, `feature/**`, `bugfix/**`, `hotfix/**`
- Ignores: `**.md`, `docs/**`, `.gitignore`, `LICENSE`, `README.md`, `CLAUDE.md`

**Pull Request Events:**
- Target branches: `main`, `develop`
- Same path ignores as push events

#### Jobs Architecture

The workflow uses **parallel job execution** for faster feedback:

```
┌─────────────┐
│  validate   │  (Gradle wrapper validation)
└──────┬──────┘
       │
       ├─────────────────┬──────────────┬─────────────┐
       │                 │              │             │
       ▼                 ▼              ▼             ▼
   ┌───────┐       ┌──────────┐   ┌──────┐     ┌─────────┐
   │ build │       │   lint   │   │detekt│     │         │
   └───┬───┘       └──────────┘   └──────┘     │         │
       │                                       │         │
       ▼                                       │         │
   ┌───────┐                                   │         │
   │ test  │───────────────────────────────────┘         │
   └───────┘                                             │
                                                         │
                All jobs complete ───────────────────────┘
```

**Job Details:**

1. **validate** (runs first)
   - Validates Gradle wrapper integrity
   - Prevents supply chain attacks
   - Must pass before other jobs start

2. **build** (runs after validate)
   - Builds both debug and release variants
   - `./gradlew assembleDebug assembleRelease`
   - Verifies charge SDK release AAR exists
   - Uploads build artifacts (1-day retention)
   - Generates build summary in GitHub

3. **test** (runs after build completes)
   - Runs unit tests: `./gradlew test`
   - Uploads test results (14-day retention)
   - Generates test summary with counts
   - Depends on build to ensure code compiles

4. **lint** (runs in parallel with build)
   - Android Lint on both modules
   - `./gradlew :charge:lintDebug :app:lintDebug`
   - Continues on error (won't fail CI)
   - Uploads HTML and XML reports (14-day retention)

5. **detekt** (runs in parallel with build)
   - Static code analysis
   - `./gradlew detekt`
   - Continues on error (won't fail CI)
   - Uploads reports (14-day retention)

#### Build Cache Strategy

```yaml
cache-read-only: ${{ github.ref != 'refs/heads/main' && github.ref != 'refs/heads/develop' }}
```

- **main/develop branches**: Read AND write cache
- **Feature branches**: Read-only cache
- Benefits:
  - Prevents cache pollution from feature branches
  - Reduces storage costs
  - Maintains fast builds on all branches

#### Permissions

```yaml
permissions:
  contents: read    # Read repository contents
  checks: write     # Write check runs (for test results)
```

Follows **least-privilege principle** - only grants necessary permissions.

---

### 2. Test Coverage Workflow (`.github/workflows/test-coverage.yml`)

Dedicated workflow for code coverage analysis using Kover.

#### Triggers

- Push to `main` or `develop`
- Pull requests to `main` or `develop`

#### What it Does

1. **Runs Unit Tests**
   - `./gradlew :charge:test`
   - Tests only the charge SDK module

2. **Generates Coverage Reports**
   - XML report: `./gradlew :charge:koverXmlReport`
   - HTML report: `./gradlew :charge:koverHtmlReport`

3. **Uploads to Codecov**
   - Uses `codecov/codecov-action@v4`
   - Tracks coverage trends over time
   - Requires `CODECOV_TOKEN` secret

4. **PR Comments**
   - Posts coverage report on PRs
   - Shows coverage changes
   - Enforces minimum coverage:
     - Overall: 70%
     - Changed files: 80%

5. **Artifacts**
   - HTML coverage report (30-day retention)
   - Test results (14-day retention)

#### Coverage Exclusions

The Kover configuration excludes:
- Generated code: `BuildConfig`, Dagger factories
- Data binding classes
- Protocol Buffer generated code

---

### 3. Dependency Review Workflow (`.github/workflows/dependency-review.yml`)

Scans dependencies for security vulnerabilities and license compliance.

#### Triggers

- Pull requests only
- Target branches: `main`, `develop`

#### Security Checks

1. **Vulnerability Scanning**
   - Checks for known CVEs in dependencies
   - Fails on moderate or higher severity
   - Uses GitHub's dependency graph

2. **License Compliance**
   - **Allowed licenses:**
     - Apache-2.0
     - MIT
     - BSD-2-Clause
     - BSD-3-Clause
   - **Denied licenses:**
     - GPL-2.0
     - GPL-3.0

3. **PR Comments**
   - Posts detailed vulnerability information
   - Comments only on failure (`comment-summary-in-pr: on-failure`)
   - Provides remediation guidance

#### Why This Matters

- Prevents introducing vulnerable dependencies
- Ensures license compatibility with your SDK
- Catches issues before they reach production
- Free for public repositories

---

## Quality Checks

### Detekt (Static Analysis)

Detekt analyzes Kotlin code for quality issues.

#### Configuration

Location: `/codequality/detekt/detekt.yml`

**Key Rules:**

```yaml
complexity:
  LongMethod: 60 lines
  CyclomaticComplexMethod: 15
  TooManyFunctions: 15 per class/file

naming:
  FunctionNaming: camelCase
  ClassNaming: PascalCase
  VariableNaming: camelCase

style:
  MaxLineLength: 120 characters
  ReturnCount: max 3 returns
  MagicNumber: with sensible ignores
```

#### Baseline File

`detekt-baseline.xml` contains existing issues (128KB).

- Allows existing issues while preventing new ones
- Should be gradually reduced
- `maxIssues: 10` allows some flexibility

#### Running Locally

```bash
# Run Detekt
./gradlew detekt

# View reports
open build/reports/detekt/detekt.html
```

---

### Android Lint

Built-in Android static analysis tool.

#### What it Checks

- Hardcoded strings (i18n issues)
- Missing translations
- API usage violations
- Resource naming conventions
- Performance issues
- Security vulnerabilities
- Accessibility problems

#### Running Locally

```bash
# Charge module
./gradlew :charge:lintDebug

# App module
./gradlew :app:lintDebug

# View reports
open charge/build/reports/lint-results-debug.html
open app/build/reports/lint-results-debug.html
```

---

### Unit Tests

Tests use JUnit, MockK, and Turbine.

#### Running Tests

```bash
# All modules
./gradlew test

# Charge module only
./gradlew :charge:test

# With coverage
./gradlew :charge:test :charge:koverHtmlReport
open charge/build/reports/kover/html/index.html
```

---

## Pipeline Architecture

### Execution Flow

```
Developer writes code
        │
        ▼
┌───────────────────┐
│  Pre-commit Hook  │  ← Local validation (Detekt + Lint)
└────────┬──────────┘
         │ ✅ Passes
         ▼
    Git Commit
         │
         ▼
    Git Push
         │
         ▼
┌───────────────────────────────────────────┐
│         GitHub Actions Triggered          │
├───────────────────────────────────────────┤
│                                           │
│  ┌─────────────────────────────────┐      │
│  │  CI Workflow (ci.yml)           │      │
│  │  ├─ Validate                    │      │
│  │  ├─ Build (parallel)            │      │
│  │  ├─ Test                        │      │
│  │  ├─ Lint (parallel)             │      │
│  │  └─ Detekt (parallel)           │      │
│  └─────────────────────────────────┘      │
│                                           │
│  ┌─────────────────────────────────┐      │
│  │  Test Coverage (test-coverage)  │      │
│  │  ├─ Run tests                   │      │
│  │  ├─ Generate coverage           │      │
│  │  ├─ Upload to Codecov           │      │
│  │  └─ Comment on PR               │      │
│  └─────────────────────────────────┘      │
│                                           │
│  ┌─────────────────────────────────┐      │
│  │  Dependency Review (PRs only)   │      │
│  │  ├─ Scan vulnerabilities        │      │
│  │  ├─ Check licenses              │      │
│  │  └─ Comment on PR               │      │
│  └─────────────────────────────────┘      │
│                                           │
└───────────────────────────────────────────┘
         │
         ▼
    All checks pass ✅
         │
         ▼
    Ready to merge
```

### Performance Optimizations

1. **Parallel Jobs**
   - Build, lint, and detekt run simultaneously
   - Reduces total CI time by ~50-60%

2. **Smart Caching**
   - Gradle cache shared between jobs
   - Feature branches read-only (no pollution)
   - Main/develop write to cache

3. **Smart Triggers**
   - Path ignores skip CI for docs-only changes
   - Branch filtering reduces unnecessary runs

4. **Artifact Retention**
   - Build outputs: 1 day (large files)
   - Test/lint/detekt reports: 14 days
   - Coverage reports: 30 days

---

## Usage Guide

### For New Developers

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd Charge
   ```

2. **Install Git Hooks**
   ```bash
   ./gradlew installGitHooks
   ```

3. **Build and Test Locally**
   ```bash
   ./gradlew build test
   ```

4. **Make Changes**
   - Edit code
   - Run relevant checks: `./gradlew detekt :charge:lintDebug`
   - Commit (pre-commit hook will run automatically)

### Creating a Pull Request

1. **Ensure Local Checks Pass**
   ```bash
   ./gradlew detekt test :charge:lintDebug
   ```

2. **Push to Feature Branch**
   ```bash
   git push origin feature/my-feature
   ```

3. **Open Pull Request**
   - Target branch: `develop` or `main`
   - All workflows will run automatically
   - Check workflow results in the PR

4. **Review Feedback**
   - Check CI status in PR
   - Review dependency scan results
   - Review coverage changes
   - Download artifacts if needed

### Viewing CI Results

**In Pull Request:**
- View status checks at bottom of PR
- Click "Details" for any check to see logs
- Download artifacts from workflow runs

**Workflow Run Page:**
- Navigate to Actions tab
- Select workflow run
- View job logs
- Download artifacts

### Downloading Reports

**Test Results:**
1. Go to workflow run
2. Scroll to "Artifacts" section
3. Download `test-results` artifact
4. Extract and open HTML reports

**Coverage Reports:**
1. Download `coverage-report` artifact
2. Open `index.html` in browser
3. Or view on Codecov website

**Lint Reports:**
1. Download `lint-reports` artifact
2. Open `lint-results-debug.html` files

**Detekt Reports:**
1. Download `detekt-reports` artifact
2. Open `detekt.html` files

---

## Troubleshooting

### Pre-commit Hook Issues

**Hook not running:**
```bash
# Reinstall the hook
./gradlew installGitHooks

# Verify it exists and is executable
ls -la .git/hooks/pre-commit
```

**Hook fails but you need to commit:**
```bash
# Emergency bypass (use sparingly)
git commit --no-verify
```

**Hook runs but shouldn't (no Kotlin files):**
- Check if Gradle files changed (they trigger checks)
- Hook should skip automatically if no relevant files changed

### CI Failures

**Gradle wrapper validation fails:**
- Don't modify `gradle/wrapper/gradle-wrapper.jar`
- If legitimate update needed, regenerate with `./gradlew wrapper`

**Build fails:**
```bash
# Reproduce locally
./gradlew assembleDebug assembleRelease

# Check error messages
# Fix compilation issues
```

**Tests fail:**
```bash
# Run tests locally
./gradlew test

# Run specific test
./gradlew :charge:test --tests "de.elvah.charge.YourTest"

# View detailed reports
open charge/build/reports/tests/testDebugUnitTest/index.html
```

**Lint issues:**
```bash
# Run lint locally
./gradlew :charge:lintDebug

# View HTML report
open charge/build/reports/lint-results-debug.html

# Fix issues in code
```

**Detekt issues:**
```bash
# Run detekt locally
./gradlew detekt

# View HTML report
open build/reports/detekt/detekt.html

# Fix issues or update baseline if needed
./gradlew detektBaseline
```

**Dependency review fails:**
- Check PR comments for vulnerability details
- Update vulnerable dependencies to patched versions
- Or add suppression with justification (rare cases)

**Coverage drops below threshold:**
- Add tests for new/changed code
- Aim for 80%+ coverage on changed files
- Check coverage report for uncovered lines

### Cache Issues

**Builds slow despite cache:**
```bash
# Check cache hits in workflow logs
# Look for "Gradle Build Action: Restored from cache"

# On main branch: cache should be written
# On feature branch: cache should be read-only
```

**Cache corruption:**
- Rare, but can happen
- GitHub Actions will auto-recover
- Manual clear: delete and recreate cache keys

### Workflow Not Triggering

**Check triggers:**
- Push to correct branch?
- Path not ignored? (check paths-ignore list)
- Workflow file syntax correct? (YAML validation)

**Force trigger:**
```bash
# Close and reopen PR
# Or push empty commit
git commit --allow-empty -m "Trigger CI"
git push
```

### Getting Help

**Workflow logs:**
1. Go to Actions tab
2. Click failed workflow
3. Click failed job
4. Expand step to see logs
5. Copy relevant error messages

**Local reproduction:**
- Always try to reproduce failures locally first
- Run the same commands as CI
- Check Gradle logs: `./gradlew <task> --stacktrace`

**Common solutions:**
- Clean build: `./gradlew clean`
- Invalidate IDE caches: File → Invalidate Caches
- Update Gradle wrapper: `./gradlew wrapper --gradle-version=<latest>`
- Check Java version: `java -version` (should be JDK 21)

---

## Maintenance

### Updating Detekt Baseline

When fixing Detekt issues:

```bash
# Update baseline after fixing issues
./gradlew detektBaseline

# Commit the updated baseline
git add detekt-baseline.xml
git commit -m "Update Detekt baseline after fixes"
```

### Adjusting Thresholds

**Detekt maxIssues:**
- Currently set to 10 in `detekt.yml`
- Reduce as you fix issues
- Goal: Reach 0

**Coverage Thresholds:**
- Overall: 70% (in test-coverage.yml)
- Changed files: 80%
- Adjust in workflow file as coverage improves

### Adding New Checks

To add new quality checks:

1. **Local (pre-commit hook):**
   - Edit `scripts/pre-commit`
   - Add new check commands
   - Run `./gradlew installGitHooks`

2. **CI (workflows):**
   - Edit `.github/workflows/ci.yml`
   - Add new step or job
   - Test on feature branch first

---

## Summary

The Elvah Charge CI/CD pipeline provides:

✅ **Fast Feedback** - Pre-commit hook catches issues before push
✅ **Comprehensive Checks** - Build, test, lint, static analysis, security
✅ **Parallel Execution** - Multiple checks run simultaneously
✅ **Optimized Performance** - Smart caching and triggers
✅ **Clear Visibility** - Reports, summaries, and PR comments
✅ **Security First** - Wrapper validation, dependency scanning, least privileges

This creates a robust quality gate that maintains high code standards while providing excellent developer experience.
