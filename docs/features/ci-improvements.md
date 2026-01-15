# CI/CD Pipeline Improvements

This document outlines improvements to the GitHub Actions CI/CD pipeline for the Charge SDK project, following Android development best practices.

## Current State Assessment

### Existing Configuration
- ✅ JDK 21 with Temurin distribution
- ✅ Gradle caching enabled
- ✅ Basic build and test execution
- ✅ Coverage reporting workflow (test-coverage.yml)
- ⚠️ **Issue**: Kover tasks in test-coverage.yml will FAIL (plugin not configured)
- ❌ No Detekt configuration
- ❌ No explicit Lint checks in CI
- ❌ No Gradle wrapper validation
- ❌ No test result uploads
- ❌ No dependency review
- ❌ ci.yml runs on ALL pushes without filtering

### Modules
- `charge/` - Library module (main SDK)
- `app/` - Demo application module

---

## Implementation Checklist

### Phase 1: Critical Fixes & Security (High Priority)

#### 1.1 Fix Kover Configuration
- [ ] Add Kover plugin to project (test-coverage.yml currently fails)
- [ ] Configure Kover for charge module
- [ ] Verify coverage report generation works locally
- [ ] Update test-coverage.yml if needed

#### 1.2 Add Gradle Wrapper Validation
- [ ] Add wrapper validation step to both workflows
- [ ] Verify validation passes
- [ ] Test with modified wrapper (should fail)

#### 1.3 Add Explicit Workflow Permissions
- [ ] Define minimal permissions in both workflows
- [ ] Test workflows still function correctly
- [ ] Document permission requirements

#### 1.4 Optimize ci.yml Triggers
- [ ] Add branch filtering (main, develop, feature/**)
- [ ] Add path ignore patterns (docs, *.md)
- [ ] Verify workflow triggers only on relevant changes

### Phase 2: Code Quality & Testing (High Priority)

#### 2.1 Add Android Lint to CI
- [ ] Add lint step to ci.yml for both modules
- [ ] Upload lint reports as artifacts
- [ ] Configure lint baseline if needed
- [ ] Add custom lint.xml configuration (optional)

#### 2.2 Add Test Result Uploads
- [ ] Upload test results on success/failure
- [ ] Add test summary to GitHub step summary
- [ ] Configure retention period (7-30 days)

#### 2.3 Build Multiple Variants
- [ ] Build both debug and release variants
- [ ] Verify release build configurations
- [ ] Add proguard validation for release builds

#### 2.4 Setup Detekt
- [ ] Add Detekt plugin and configuration
- [ ] Create detekt.yml configuration file
- [ ] Add Detekt step to ci.yml
- [ ] Upload Detekt reports
- [ ] Configure baseline for existing issues

### Phase 3: Optimization & Enhancements (Medium Priority)

#### 3.1 Add Dependency Review
- [ ] Add dependency-review-action for PRs
- [ ] Configure vulnerability thresholds
- [ ] Test with known vulnerable dependency

#### 3.2 Parallelize Jobs
- [ ] Split ci.yml into multiple jobs
- [ ] Configure job dependencies
- [ ] Measure performance improvement
- [ ] Optimize job distribution

#### 3.3 Optimize Build Cache
- [ ] Configure cache-read-only for non-main branches
- [ ] Add cache key versioning
- [ ] Monitor cache hit rates

#### 3.4 Consolidate Workflows
- [ ] Evaluate merging ci.yml into test-coverage.yml
- [ ] Use job-level conditions for different triggers
- [ ] Reduce workflow duplication

### Phase 4: Advanced Features (Low Priority)

#### 4.1 Add API Binary Validation
- [ ] Verify ABI validation is working (already in charge/build.gradle.kts)
- [ ] Add CI step to check for binary incompatibilities
- [ ] Generate API compatibility reports

#### 4.2 Create SDK Publication Workflow
- [ ] Create publish.yml workflow
- [ ] Add release trigger configuration
- [ ] Configure Maven publication
- [ ] Add version validation
- [ ] Test publication to Maven Local

#### 4.3 Add Performance Benchmarks
- [ ] Create benchmark module (if applicable)
- [ ] Add benchmark execution to CI
- [ ] Store baseline results
- [ ] Compare against baseline

---

## LLM Implementation Tasks

Each task below provides complete context for an LLM to implement independently with unit tests where applicable.

### Task 1: Configure Kover Plugin

**Context:**
- The test-coverage.yml workflow calls `./gradlew :charge:koverXmlReport` and `./gradlew :charge:koverHtmlReport`
- These tasks will FAIL because Kover plugin is not configured in the project
- Kover is Kotlin's code coverage library (alternative to JaCoCo)
- Need to add plugin to charge module only (not app module)

**Files to modify:**
- `gradle/libs.versions.toml` - Add Kover plugin version and reference
- `charge/build.gradle.kts` - Apply Kover plugin and configure

**Implementation steps:**
1. Add Kover version to `gradle/libs.versions.toml`:
   ```toml
   [versions]
   kover = "0.9.0"

   [plugins]
   kover = { id = "org.jetbrains.kotlinx.kover", version.ref = "kover" }
   ```

2. Apply plugin in `charge/build.gradle.kts`:
   ```kotlin
   plugins {
       // ... existing plugins
       alias(libs.plugins.kover)
   }

   kover {
       reports {
           filters {
               excludes {
                   // Exclude generated code
                   classes("*.BuildConfig")
                   classes("*.*_Factory")
                   classes("*.*_MembersInjector")
                   classes("*.Dagger*")
                   packages("de.elvah.charge.databinding")
                   packages("de.elvah.charge.generated")
               }
           }
       }
   }
   ```

3. Run locally to verify:
   ```bash
   ./gradlew :charge:test
   ./gradlew :charge:koverXmlReport
   ./gradlew :charge:koverHtmlReport
   ```

4. Check generated reports exist:
   - `charge/build/reports/kover/xml/report.xml`
   - `charge/build/reports/kover/html/index.html`

**Testing:**
- Run `./gradlew :charge:koverXmlReport` - should succeed and generate XML report
- Run `./gradlew :charge:koverHtmlReport` - should succeed and generate HTML report
- Verify report.xml contains coverage data with `<counter>` elements
- Open HTML report and verify it displays coverage percentages

**Acceptance criteria:**
- [ ] Kover plugin configured in charge module
- [ ] XML and HTML reports generate successfully
- [ ] Reports exclude generated code (BuildConfig, etc.)
- [ ] Coverage data is accurate (matches actual test execution)
- [ ] test-coverage.yml workflow will succeed

---

### Task 2: Add Gradle Wrapper Validation

**Context:**
- Gradle wrapper is the recommended way to execute Gradle builds
- Malicious actors can modify gradle-wrapper.jar to inject malicious code
- GitHub provides an official action to validate the wrapper hasn't been tampered with
- This is a security best practice for all Gradle projects
- Should be the FIRST step in every workflow (before checkout)

**Files to modify:**
- `.github/workflows/ci.yml`
- `.github/workflows/test-coverage.yml`

**Implementation steps:**
1. Add validation step to `.github/workflows/ci.yml` after checkout:
   ```yaml
   steps:
     - uses: actions/checkout@v5

     - name: Validate Gradle wrapper
       uses: gradle/actions/wrapper-validation@v4

     # ... rest of steps
   ```

2. Add the same step to `.github/workflows/test-coverage.yml` after checkout

3. Commit changes and push to trigger workflows

4. Verify workflows pass with validation step

**Testing:**
1. Verify workflows pass with validation step
2. Manually modify `gradle/wrapper/gradle-wrapper.jar` (change 1 byte)
3. Commit and push - workflow should FAIL at validation step
4. Revert changes - workflow should PASS again

**Acceptance criteria:**
- [ ] Validation step added to both workflows
- [ ] Validation runs before any Gradle tasks
- [ ] Workflow fails when wrapper is modified
- [ ] Workflow passes when wrapper is legitimate

**No unit tests needed** (workflow validation only)

---

### Task 3: Add Explicit Workflow Permissions

**Context:**
- GitHub Actions has a permission model for accessing resources
- By default, workflows get broad permissions
- Following least-privilege principle, should explicitly declare minimal permissions
- Required permissions vary by workflow actions used

**Files to modify:**
- `.github/workflows/ci.yml`
- `.github/workflows/test-coverage.yml`

**Implementation steps:**
1. Add permissions block to `.github/workflows/ci.yml`:
   ```yaml
   name: Android CI

   on:
     push:

   permissions:
     contents: read      # Read repository contents
     checks: write       # Write check runs (for test results)

   jobs:
     build:
       runs-on: ubuntu-latest
       # ... rest of workflow
   ```

2. Add permissions to `.github/workflows/test-coverage.yml`:
   ```yaml
   name: Test Coverage

   on:
     push:
       branches: [ main, develop ]
     pull_request:
       branches: [ main, develop ]

   permissions:
     contents: read           # Read repository contents
     pull-requests: write     # Write PR comments
     checks: write            # Write check runs

   jobs:
     test-coverage:
       runs-on: ubuntu-latest
       # ... rest of workflow
   ```

3. Test workflows still function correctly

**Testing:**
- Trigger both workflows and verify they complete successfully
- For test-coverage.yml on a PR, verify coverage comment is posted
- Verify no permission errors in workflow logs

**Acceptance criteria:**
- [ ] Permissions explicitly declared in both workflows
- [ ] Workflows function correctly with minimal permissions
- [ ] No permission-related errors in logs
- [ ] PR comments still post (test-coverage.yml)

**No unit tests needed** (workflow validation only)

---

### Task 4: Optimize ci.yml Triggers

**Context:**
- Current ci.yml runs on EVERY push to ANY branch
- This wastes CI minutes on documentation changes, WIP branches, etc.
- Should filter by branch patterns and ignore irrelevant file changes
- Pull request triggers should also be added for validation

**Files to modify:**
- `.github/workflows/ci.yml`

**Implementation steps:**
1. Replace the `on:` section in `.github/workflows/ci.yml`:
   ```yaml
   on:
     push:
       branches:
         - main
         - develop
         - 'feature/**'
         - 'bugfix/**'
         - 'hotfix/**'
       paths-ignore:
         - '**.md'
         - 'docs/**'
         - '.gitignore'
         - 'LICENSE'
         - 'README.md'
         - 'CLAUDE.md'
     pull_request:
       branches:
         - main
         - develop
       paths-ignore:
         - '**.md'
         - 'docs/**'
         - '.gitignore'
         - 'LICENSE'
         - 'README.md'
         - 'CLAUDE.md'
   ```

2. Commit and test:
   - Push a change to a feature branch - should trigger
   - Push only a .md file change - should NOT trigger
   - Open a PR to main - should trigger
   - Push to a random branch name - should NOT trigger

**Testing:**
1. Create feature branch, modify code, push - workflow RUNS
2. Modify only README.md, push - workflow SKIPPED
3. Modify docs/something.md, push - workflow SKIPPED
4. Open PR to main with code changes - workflow RUNS
5. Push to branch named `test/something` - workflow SKIPPED

**Acceptance criteria:**
- [ ] Workflow only runs on specified branches
- [ ] Documentation changes don't trigger workflow
- [ ] Pull requests to main/develop trigger workflow
- [ ] Unrelated branches don't trigger workflow

**No unit tests needed** (workflow validation only)

---

### Task 5: Add Android Lint to CI

**Context:**
- Android Lint is a static code analysis tool built into Android Gradle Plugin
- Catches potential bugs, performance issues, accessibility issues, etc.
- Already available in project (no plugin needed)
- Should run on both modules (charge and app)
- Reports should be uploaded as artifacts for review
- Lint failures should NOT block CI (can be warnings)

**Files to modify:**
- `.github/workflows/ci.yml`

**Implementation steps:**
1. Add lint steps to `.github/workflows/ci.yml` after the test step:
   ```yaml
   - name: Run Tests with Gradle
     run: ./gradlew test

   - name: Run Android Lint
     run: ./gradlew :charge:lintDebug :app:lintDebug
     continue-on-error: true  # Don't fail CI on lint issues

   - name: Upload Lint Reports
     if: always()
     uses: actions/upload-artifact@v4
     with:
       name: lint-reports
       path: |
         charge/build/reports/lint-results-*.html
         charge/build/reports/lint-results-*.xml
         app/build/reports/lint-results-*.html
         app/build/reports/lint-results-*.xml
       retention-days: 14
   ```

2. (Optional) Create custom lint configuration file `charge/lint.xml`:
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <lint>
       <!-- Fail build on these errors -->
       <issue id="MissingTranslation" severity="error" />
       <issue id="HardcodedText" severity="warning" />

       <!-- Ignore these in SDK -->
       <issue id="UnusedResources" severity="ignore" />
   </lint>
   ```

3. Test locally:
   ```bash
   ./gradlew :charge:lintDebug
   ./gradlew :app:lintDebug
   ```

**Testing:**
1. Run `./gradlew :charge:lintDebug` locally - should succeed
2. Check `charge/build/reports/lint-results-debug.html` exists
3. Trigger workflow and verify lint reports uploaded
4. Introduce a lint issue (e.g., hardcoded string) - verify it's reported
5. Verify CI doesn't fail on lint warnings (continue-on-error works)

**Acceptance criteria:**
- [ ] Lint runs on both charge and app modules
- [ ] Lint reports uploaded as artifacts
- [ ] CI continues even with lint issues
- [ ] Reports are human-readable HTML format
- [ ] XML reports available for parsing

**No unit tests needed** (integration test with workflow validation)

---

### Task 6: Add Test Result Uploads

**Context:**
- When tests fail, developers need to see detailed failure information
- GitHub Actions can display test results inline in PR checks
- Test reports should be available even when tests pass (for analysis)
- Should upload both XML (for parsing) and HTML (for viewing)

**Files to modify:**
- `.github/workflows/ci.yml`
- `.github/workflows/test-coverage.yml`

**Implementation steps:**
1. Add test result upload to `.github/workflows/ci.yml`:
   ```yaml
   - name: Run Tests with Gradle
     run: ./gradlew test

   - name: Upload Test Results
     if: always()
     uses: actions/upload-artifact@v4
     with:
       name: test-results
       path: |
         **/build/test-results/test*/*.xml
         **/build/reports/tests/
       retention-days: 14

   - name: Test Summary
     if: always()
     run: |
       echo "## Test Results" >> $GITHUB_STEP_SUMMARY
       echo "" >> $GITHUB_STEP_SUMMARY
       if [ -f "charge/build/test-results/testDebugUnitTest/TEST-*.xml" ]; then
         echo "✅ Charge module tests completed" >> $GITHUB_STEP_SUMMARY
       fi
       if [ -f "app/build/test-results/testDebugUnitTest/TEST-*.xml" ]; then
         echo "✅ App module tests completed" >> $GITHUB_STEP_SUMMARY
       fi
   ```

2. Add same steps to `.github/workflows/test-coverage.yml` after test execution

3. Consider adding test report action for better visualization:
   ```yaml
   - name: Publish Test Report
     uses: mikepenz/action-junit-report@v4
     if: always()
     with:
       report_paths: '**/build/test-results/test*/TEST-*.xml'
       check_name: Test Results
   ```

**Testing:**
1. Run workflow with passing tests - verify reports uploaded
2. Introduce failing test - verify failure details available in artifact
3. Check GitHub step summary shows test status
4. Download artifact and verify XML/HTML reports present

**Acceptance criteria:**
- [ ] Test results uploaded on both success and failure
- [ ] Artifacts contain XML and HTML reports
- [ ] Step summary shows test status
- [ ] Reports downloadable for analysis
- [ ] Works for both modules

**No unit tests needed** (workflow validation only)

---

### Task 7: Build Multiple Variants

**Context:**
- Android projects have multiple build variants (debug, release)
- Release builds may have different configurations (minification, obfuscation)
- Should verify both variants compile successfully
- Current ci.yml only runs `./gradlew assemble` (debug variant)
- For SDK library, release variant is what gets published

**Files to modify:**
- `.github/workflows/ci.yml`

**Implementation steps:**
1. Replace the build step in `.github/workflows/ci.yml`:
   ```yaml
   - name: Build with Gradle
     run: ./gradlew assemble
   ```

   With:
   ```yaml
   - name: Build Debug Variants
     run: ./gradlew assembleDebug

   - name: Build Release Variants
     run: ./gradlew assembleRelease

   - name: Verify Charge SDK Release Build
     run: |
       if [ ! -f "charge/build/outputs/aar/charge-release.aar" ]; then
         echo "❌ Charge SDK release AAR not found!"
         exit 1
       fi
       echo "✅ Charge SDK release build successful"
   ```

2. Alternative (build all at once):
   ```yaml
   - name: Build All Variants
     run: ./gradlew build assembleRelease
   ```

3. Add verification step to check release artifacts:
   ```yaml
   - name: List Build Outputs
     run: |
       echo "## Build Artifacts" >> $GITHUB_STEP_SUMMARY
       echo "" >> $GITHUB_STEP_SUMMARY
       find . -name "*.aar" -o -name "*.apk" | while read file; do
         echo "- $file" >> $GITHUB_STEP_SUMMARY
       done
   ```

**Testing:**
1. Run `./gradlew assembleDebug` locally - should succeed
2. Run `./gradlew assembleRelease` locally - should succeed
3. Verify AAR files generated in `charge/build/outputs/aar/`
4. Trigger workflow and verify both variants build
5. Check step summary lists build artifacts

**Acceptance criteria:**
- [ ] Both debug and release variants build successfully
- [ ] Release AAR artifact verified to exist
- [ ] Build outputs listed in step summary
- [ ] No build errors or warnings for release variant
- [ ] ProGuard rules validated (if applicable)

**No unit tests needed** (build validation only)

---

### Task 8: Setup Detekt

**Context:**
- Detekt is a static code analysis tool for Kotlin
- Catches code smells, complexity issues, style violations
- NOT currently configured in project (plugin missing from version catalog)
- Requires plugin, configuration file, and CI integration
- Can use baseline for existing violations

**Files to modify:**
- `gradle/libs.versions.toml` - Add Detekt plugin
- `build.gradle.kts` - Apply plugin at root level
- `detekt.yml` - Configuration file (create new)
- `.github/workflows/ci.yml` - Add Detekt step

**Implementation steps:**

1. Add Detekt to `gradle/libs.versions.toml`:
   ```toml
   [versions]
   detekt = "1.23.7"

   [plugins]
   detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }
   ```

2. Apply plugin in root `build.gradle.kts`:
   ```kotlin
   plugins {
       // ... existing plugins
       alias(libs.plugins.detekt)
   }

   detekt {
       buildUponDefaultConfig = true
       allRules = false
       config.setFrom("$rootDir/codequality/detekt//detekt.yml")
       baseline = file("$rootDir/codequality/detekt//detekt-baseline.xml")
   }

   dependencies {
       detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
   }
   ```

3. Create `detekt.yml` in project root:
   ```yaml
   build:
     maxIssues: 0
     excludeCorrectable: false
     weights:
       complexity: 2
       LongParameterList: 1
       style: 1
       comments: 1

   config:
     validation: true
     warningsAsErrors: false

   complexity:
     active: true
     LongMethod:
       threshold: 60
     LongParameterList:
       functionThreshold: 6
     ComplexMethod:
       threshold: 15

   naming:
     active: true
     FunctionNaming:
       functionPattern: '[a-z][a-zA-Z0-9]*'

   style:
     active: true
     MagicNumber:
       ignoreNumbers: [-1, 0, 1, 2, 100]
     MaxLineLength:
       maxLineLength: 120
   ```

4. Generate baseline for existing issues:
   ```bash
   ./gradlew detektBaseline
   ```

5. Add Detekt step to `.github/workflows/ci.yml`:
   ```yaml
   - name: Run Detekt
     run: ./gradlew detekt
     continue-on-error: true

   - name: Upload Detekt Reports
     if: always()
     uses: actions/upload-artifact@v4
     with:
       name: detekt-reports
       path: |
         build/reports/detekt/
       retention-days: 14
   ```

**Testing:**
1. Run `./gradlew detekt` locally - should run and report issues
2. Check `build/reports/detekt/detekt.html` for results
3. Generate baseline: `./gradlew detektBaseline`
4. Run `./gradlew detekt` again - should pass with baseline
5. Introduce code smell (long function) - verify Detekt catches it
6. Trigger workflow and verify Detekt runs and reports upload

**Unit testing:**
Create a test to verify Detekt configuration is valid:

```kotlin
// charge/src/test/kotlin/de/elvah/charge/DetektConfigTest.kt
package de.elvah.charge

import org.junit.Test
import java.io.File

class DetektConfigTest {

    @Test
    fun `detekt configuration file exists`() {
        val configFile = File("../detekt.yml")
        assert(configFile.exists()) { "detekt.yml not found in project root" }
    }

    @Test
    fun `detekt configuration is valid yaml`() {
        val configFile = File("../detekt.yml")
        val content = configFile.readText()

        // Basic YAML validation
        assert(content.contains("build:")) { "Missing build section" }
        assert(content.contains("complexity:")) { "Missing complexity section" }
        assert(content.contains("naming:")) { "Missing naming section" }
    }
}
```

**Acceptance criteria:**
- [ ] Detekt plugin configured in project
- [ ] detekt.yml configuration file created
- [ ] Baseline generated for existing issues
- [ ] Detekt runs in CI and uploads reports
- [ ] Configuration test passes
- [ ] Reports are human-readable HTML format

---

### Task 9: Add Dependency Review

**Context:**
- GitHub's Dependency Review action scans for vulnerable dependencies
- Checks for known CVEs in dependencies
- Only runs on pull requests (compares dependency changes)
- Helps prevent introducing vulnerable dependencies
- Free for public repositories

**Files to modify:**
- `.github/workflows/ci.yml` OR create new `.github/workflows/dependency-review.yml`

**Implementation steps:**

Option 1 - Add to existing ci.yml:
```yaml
- name: Dependency Review
  uses: actions/dependency-review-action@v4
  if: github.event_name == 'pull_request'
  with:
    fail-on-severity: moderate
    allow-licenses: Apache-2.0, MIT, BSD-3-Clause
```

Option 2 - Create separate workflow `.github/workflows/dependency-review.yml`:
```yaml
name: Dependency Review

on:
  pull_request:
    branches: [ main, develop ]

permissions:
  contents: read
  pull-requests: write

jobs:
  dependency-review:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v5

      - name: Dependency Review
        uses: actions/dependency-review-action@v4
        with:
          fail-on-severity: moderate
          allow-licenses: |
            Apache-2.0
            MIT
            BSD-2-Clause
            BSD-3-Clause
          deny-licenses: |
            GPL-2.0
            GPL-3.0
          comment-summary-in-pr: on-failure
```

**Testing:**
1. Create a PR with no dependency changes - should pass
2. Add a dependency with known vulnerability - should fail with details
3. Add dependency with restricted license (GPL) - should fail
4. Check PR comments for dependency review results

**Acceptance criteria:**
- [ ] Dependency review runs on all PRs
- [ ] Fails on moderate or higher severity vulnerabilities
- [ ] License compliance checked
- [ ] Results commented on PR
- [ ] Provides actionable vulnerability information

**No unit tests needed** (workflow validation only)

---

### Task 10: Parallelize CI Jobs

**Context:**
- Current ci.yml runs all steps sequentially in one job
- Can split into multiple jobs that run in parallel
- Reduces total CI time significantly
- Jobs: build, test, lint, detekt
- Some jobs depend on others (test needs build artifacts)
- GitHub Actions provides job dependencies with `needs`

**Files to modify:**
- `.github/workflows/ci.yml`

**Implementation steps:**
1. Restructure `.github/workflows/ci.yml` into multiple jobs:
   ```yaml
   name: Android CI

   on:
     push:
       branches: [main, develop, 'feature/**']
       paths-ignore: ['**.md', 'docs/**']
     pull_request:
       branches: [main, develop]

   permissions:
     contents: read
     checks: write

   jobs:
     validate:
       name: Validate
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v5
         - name: Validate Gradle wrapper
           uses: gradle/actions/wrapper-validation@v4

     build:
       name: Build
       runs-on: ubuntu-latest
       needs: validate
       steps:
         - uses: actions/checkout@v5
         - name: Set up JDK 21
           uses: actions/setup-java@v5
           with:
             java-version: '21'
             distribution: 'temurin'
             cache: gradle
         - name: Setup Gradle
           uses: gradle/actions/setup-gradle@v4
         - name: Build all variants
           run: ./gradlew assembleDebug assembleRelease
         - name: Upload build outputs
           uses: actions/upload-artifact@v4
           with:
             name: build-outputs
             path: |
               **/build/outputs/aar/
               **/build/outputs/apk/
             retention-days: 1

     test:
       name: Unit Tests
       runs-on: ubuntu-latest
       needs: build
       steps:
         - uses: actions/checkout@v5
         - name: Set up JDK 21
           uses: actions/setup-java@v5
           with:
             java-version: '21'
             distribution: 'temurin'
             cache: gradle
         - name: Setup Gradle
           uses: gradle/actions/setup-gradle@v4
         - name: Run tests
           run: ./gradlew test
         - name: Upload test results
           if: always()
           uses: actions/upload-artifact@v4
           with:
             name: test-results
             path: '**/build/test-results/test*/*.xml'

     lint:
       name: Android Lint
       runs-on: ubuntu-latest
       needs: validate
       steps:
         - uses: actions/checkout@v5
         - name: Set up JDK 21
           uses: actions/setup-java@v5
           with:
             java-version: '21'
             distribution: 'temurin'
             cache: gradle
         - name: Setup Gradle
           uses: gradle/actions/setup-gradle@v4
         - name: Run lint
           run: ./gradlew :charge:lintDebug :app:lintDebug
           continue-on-error: true
         - name: Upload lint reports
           if: always()
           uses: actions/upload-artifact@v4
           with:
             name: lint-reports
             path: '**/build/reports/lint-results-*.html'

     detekt:
       name: Detekt
       runs-on: ubuntu-latest
       needs: validate
       steps:
         - uses: actions/checkout@v5
         - name: Set up JDK 21
           uses: actions/setup-java@v5
           with:
             java-version: '21'
             distribution: 'temurin'
             cache: gradle
         - name: Setup Gradle
           uses: gradle/actions/setup-gradle@v4
         - name: Run detekt
           run: ./gradlew detekt
           continue-on-error: true
         - name: Upload detekt reports
           if: always()
           uses: actions/upload-artifact@v4
           with:
             name: detekt-reports
             path: 'build/reports/detekt/'
   ```

2. Test the parallelized workflow and measure performance improvement

**Testing:**
1. Trigger workflow and observe jobs running in parallel
2. Check that validate runs first, then build/lint/detekt in parallel
3. Verify test runs after build completes
4. Measure total workflow time vs sequential execution
5. Verify all artifacts upload correctly

**Acceptance criteria:**
- [ ] Jobs run in parallel where possible
- [ ] Proper job dependencies configured
- [ ] Total workflow time reduced (measure before/after)
- [ ] All artifacts upload successfully
- [ ] Gradle cache shared between jobs

**No unit tests needed** (workflow optimization validation)

---

### Task 11: Optimize Build Cache

**Context:**
- Gradle caching speeds up builds significantly
- Should cache aggressively on main branch (cache-read-only: false)
- Feature branches should only read cache, not write (saves storage)
- Reduces CI time and GitHub Actions minutes usage

**Files to modify:**
- `.github/workflows/ci.yml`
- `.github/workflows/test-coverage.yml`

**Implementation steps:**
1. Update Setup Gradle step in both workflows:
   ```yaml
   - name: Setup Gradle
     uses: gradle/actions/setup-gradle@v4
     with:
       cache-read-only: ${{ github.ref != 'refs/heads/main' && github.ref != 'refs/heads/develop' }}
   ```

2. Add build cache configuration comment:
   ```yaml
   # Cache strategy:
   # - main/develop branches: read and write cache
   # - feature branches: read-only cache
   # This prevents cache pollution from feature branches
   ```

3. Monitor cache hit rates in workflow logs

**Testing:**
1. Push to main branch - verify cache is written
2. Push to feature branch - verify cache is read-only
3. Check workflow logs for cache hit/miss statistics
4. Measure build time improvement with cache hits

**Acceptance criteria:**
- [ ] Main/develop branches write to cache
- [ ] Feature branches only read from cache
- [ ] Cache hit rate improves build times
- [ ] Configuration applied to all workflows

**No unit tests needed** (workflow optimization validation)

---

## Summary

### Phase 1 (Critical - Week 1)
1. ✅ Fix Kover Configuration
2. ✅ Add Gradle Wrapper Validation
3. ✅ Add Explicit Workflow Permissions
4. ✅ Optimize ci.yml Triggers

### Phase 2 (High Priority - Week 2)
5. ✅ Add Android Lint to CI
6. ✅ Add Test Result Uploads
7. ✅ Build Multiple Variants
8. ✅ Setup Detekt

### Phase 3 (Medium Priority - Week 3-4)
9. ✅ Add Dependency Review
10. ✅ Parallelize CI Jobs
11. ✅ Optimize Build Cache

### Phase 4 (Low Priority - As Needed)
- API Binary Validation
- SDK Publication Workflow
- Performance Benchmarks

---

## Testing Strategy

Each task should be tested in this order:
1. **Local verification** - Run commands locally first
2. **Feature branch test** - Push to feature branch, verify workflow
3. **PR test** - Open PR to develop, verify all checks pass
4. **Main branch test** - Merge to main, verify production workflow

## Success Metrics

- ✅ All workflows pass consistently
- ✅ CI time reduced by >30% (with parallelization)
- ✅ Test coverage reports accurate and up-to-date
- ✅ Lint/Detekt issues tracked and reduced over time
- ✅ No security vulnerabilities in dependencies
- ✅ Build cache hit rate >70%

---

## Implementation Complete! 🎉

All tasks from Phases 1-3 have been successfully implemented and tested locally.

### Additional Enhancements

Beyond the original 11 tasks, the following was also added:

**Pre-commit Hook:**
- Created `scripts/pre-commit` - Git hook for local quality checks
- Added Gradle tasks: `installGitHooks` and `setupProject`
- Runs Detekt and Lint before commits
- Smart detection of changed files
- Documented in `scripts/README.md`

**Comprehensive Documentation:**
- Created `docs/ci-pipeline.md` - Complete CI/CD pipeline documentation
- Covers all workflows, quality checks, usage guide, and troubleshooting
- Updated `CLAUDE.md` with git hooks section

### Files Created/Modified

**Workflows:**
- ✅ `.github/workflows/ci.yml` - Restructured with parallel jobs, optimized caching
- ✅ `.github/workflows/test-coverage.yml` - Added test uploads, optimized caching
- ✅ `.github/workflows/dependency-review.yml` - New workflow for security scanning

**Configuration:**
- ✅ `detekt.yml` - Detekt static analysis rules
- ✅ `detekt-baseline.xml` - Existing issues baseline (128KB)
- ✅ `build.gradle.kts` - Applied Detekt, added git hooks tasks
- ✅ `charge/build.gradle.kts` - Added Kover plugin and configuration
- ✅ `gradle/libs.versions.toml` - Added Kover and Detekt plugins

**Scripts:**
- ✅ `scripts/pre-commit` - Pre-commit hook script
- ✅ `scripts/README.md` - Hook documentation

**Documentation:**
- ✅ `docs/ci-pipeline.md` - Complete CI/CD documentation (NEW)
- ✅ `CLAUDE.md` - Updated with git hooks section

### Next Steps

For future improvements (Phase 4 - optional):
- API Binary Validation (ABI compatibility checks)
- SDK Publication Workflow (automated Maven publishing)
- Performance Benchmarks (if applicable)

### Documentation

For complete pipeline documentation, see: **[docs/ci-pipeline.md](../ci-pipeline.md)**

---

Last updated: 2026-01-15
