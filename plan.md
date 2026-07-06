# Stock Tracker Technical Plan

## Governance Check
- Every change must pass a documented TDD workflow.
- Every branch must include at least one automated test for new behavior.
- All code must meet formatting and static-analysis rules before merge.
- Merge gates must enforce passing CI and peer review.

## TDD Workflow
1. Write a failing test for the behavior.
2. Run the test and confirm it fails for the intended reason.
3. Implement the minimal code to make the test pass.
4. Refactor the code while preserving all passing tests.
5. Commit with a message linking to the user story or task.

## Test Layers
- Unit tests: validate business logic, transformation, and scheduler behavior.
- Integration tests: verify data flow between quote retrieval, market status, and UI model.
- UI acceptance tests: validate user-facing scenarios such as add symbol, filter groups, and market state display.
- Regression tests: lock down bug fixes and previously failing edge cases.

## Compliance
- `./gradlew test` must pass on every branch, every commit, and every pull request.
- Adopt JUnit Jupiter for test execution and use Gradle test reports.
- Keep test coverage focused on behavior-driving code paths.
- Use the project toolchain Java 21 and only approved dependencies.
- Record known limitations and design tradeoffs in the implementation notes.

## Service Architecture
- **StockQuoteService**: Fetches live stock quotes from Yahoo Finance API with browser User-Agent headers.
- **StockNewsService**: Fetches latest news articles for ticker symbols from Yahoo Finance API.
- **WatchlistStore**: Persists user watchlist and groups to disk.

## Implementation Notes
- User-Agent header set to browser Chrome string to avoid 404 errors from Yahoo Finance API.
- News service returns up to 5 most recent articles per ticker.
- Both API services handle errors gracefully and return empty results on failure.

## Merge Gate Strategy
- Pull requests must include:
  - Target branch context and purpose.
  - Related user story or task reference.
  - A passing CI status badge or summary in the PR description.
- The merge gate must reject:
  - failing tests
  - missing test coverage for new features
  - unresolved review comments
- Use automated checks to ensure:
  - `./gradlew test` completes successfully
  - there are no static analysis violations
  - code formatting is consistent

## Release Planning
- Keep feature work small and incremental.
- Prefer small pull requests that map directly to one user story.
- Use this technical plan to guide implementation choices and branch hygiene.
