# Scripts Directory

This directory contains utility scripts for the Elvah Charge project.

## pre-commit

Git pre-commit hook that runs quality checks before allowing commits.

### What it checks:
- **Detekt** - Static analysis on changed Kotlin files
- **Android Lint** - Lint checks on charge module (always) and app module (when changed)

### Features:
- ⚡ Fast - Only checks changed files
- 🎯 Smart - Skips if no Kotlin/Gradle files changed
- 📋 Clear - Provides helpful error messages and instructions
- 🚪 Bypassable - Use `git commit --no-verify` in emergencies

### Installation:
The hook is automatically installed via Gradle:
```bash
./gradlew installGitHooks
```

### Manual installation:
If you need to manually install or update the hook:
```bash
cp scripts/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

### Hook behavior:

**When checks pass:**
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

**When checks fail:**
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

### Customization:
To modify the checks or add new ones, edit `scripts/pre-commit` and run `./gradlew installGitHooks` to update the hook.

### Note:
The pre-commit hook file in `.git/hooks/` is not tracked by git. Each developer needs to run the installation command after cloning the repository.
