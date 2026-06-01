# Stock Tracker Governance

## Governance Principles
- No merge with a failing test.
- No direct push to `main` without peer-reviewed pull requests.
- Every new behavior must be covered by a test before implementation.
- Code changes must be evaluated against both `spec.md` and `plan.md`.

## Color Scheme
- 🟥 Red: Blocker condition.
  - failing tests
  - merge without CI status
  - unresolved review comments
- 🟨 Yellow: Warning condition.
  - incomplete documentation for new behavior
  - missing targeted regression test
  - low test coverage on changed files
- 🟩 Green: Approval condition.
  - all automated tests passed
  - test-first evidence exists for the feature
  - reviews resolved and branch is clean

## Merge Principle
- A pull request may only merge when the overall state is `🟩 Green`.
- Failing tests must stop the merge at the gate.
- A single broken test is enough to turn the branch to `🟥 Red`.
- Use the CI gating rule: `all tests pass -> code review complete -> merge approved`.

## Code Enforcement
- Enforce test execution by requiring `./gradlew test` results in CI.
- Enforce formatting and quality by requiring consistent build and lint checks.
- Where possible, automate status checks so developers do not merge manually on red.
- Document exceptions and temporary workarounds in the PR description, never in production code.

## Governance Workflow
1. Open a feature branch from `main`.
2. Author writes tests first, then implementation.
3. Run local tests and verify before pushing.
4. Create a pull request with the relevant spec and test evidence.
5. Obtain at least one reviewer approval.
6. Merge only after CI reports green and all checks pass.
