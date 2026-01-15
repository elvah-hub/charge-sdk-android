### Project Improvement Plan

####  Kategorie: Dependencies & Build
- [x] **1. Analyze Dependencies**: Review `gradle/libs.versions.toml` and `build.gradle.kts` files to identify outdated libraries. Outdated dependencies can lead to security vulnerabilities and bugs.
- [x] **2. Update Dependencies**: Upgrade outdated dependencies to their latest stable versions, ensuring compatibility and bringing in the latest features and security patches.
- [x] **3. Gradle Wrapper**: Check for and update the Gradle version in `gradle/wrapper/gradle-wrapper.properties` to improve build performance and gain access to new Gradle features.
- [x] **4. Build Speed**: Analyze `gradle.properties` and build scripts for potential optimizations like enabling the build cache, parallel execution, and configuration caching.

#### Kategorie: Security
- [x] **1. Secret Scanning**: Scan the codebase for any hardcoded API keys, tokens, or other secrets. These should be moved to a secure storage mechanism.
- [x] **2. Vulnerability Scanning**: Integrate a dependency vulnerability scanner (like OWASP Dependency-Check or GitHub's Dependabot) into the CI pipeline to automatically detect known vulnerabilities in libraries.

#### Kategorie: Code Quality & Static Analysis
- [x] **1. Setup Static Analysis**: Integrate `Detekt` or `Ktlint` to enforce Kotlin coding standards and detect potential issues. (Note: Detekt found a large number of issues, which should be addressed incrementally.)
- [ ] **2. Enforce Code Formatting**: Configure `ktlint --format` or a similar tool to run automatically, possibly as a Git pre-commit hook or a CI step, to ensure consistent code style across the project.
- [ ] **3. Review `TODO`s**: Search the codebase for `TODO` comments to identify and prioritize unfinished work or technical debt.

#### Kategorie: CI/CD & Automation
- [ ] **1. Enhance CI Pipeline**: Review `.github/workflows/ci.yml` to ensure it runs a comprehensive set of checks, including:
    - [ ] Unit Tests (`./gradlew test`)
    - [ ] Instrumented Tests (`./gradlew connectedCheck`)
    - [ ] Linting (`./gradlew lint`)
    - [ ] Static Analysis (Detekt/Ktlint)
- [ ] **2. Add Code Coverage**: Integrate JaCoCo or a similar tool to generate code coverage reports. This will help identify areas of the code that lack sufficient test coverage.

#### Kategorie: Documentation
- [ ] **1. Update README**: Review and enhance the `README.md` to include clear instructions on how to build, run, and test the project. Add a brief architectural overview.
- [ ] **2. Document Public APIs**: Add KDoc comments to the public classes and methods in the `:charge` module's `public_api` package to improve its usability as a library.
