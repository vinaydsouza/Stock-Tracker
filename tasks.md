# Stock Tracker Tasks

## Phase 1: Define and Verify
- [ ] 1.1 Define business stories in `spec.md`
- [ ] 1.2 Identify acceptance criteria and TDD test cases
- [ ] 1.3 Confirm priority and standards for the first delivery

## Phase 2: Test-First Implementation
- [ ] 2.1 Create test task: write a failing unit test for adding symbols
- [ ] 2.2 Implement add-symbol flow to satisfy the test
- [ ] 2.3 Create test task: write a failing unit test for market direction label
- [ ] 2.4 Implement market direction evaluation and status output
- [ ] 2.5 Create test task: write a failing integration test for group filtering
- [ ] 2.6 Implement group filter support in the view model
- [ ] 2.7 Create test task: write a failing scheduler test for market closed pause
- [ ] 2.8 Implement refresh pause/resume behavior around market hours

## Phase 3: Validate and Harden
- [ ] 3.1 Create test task: write UI acceptance tests for symbol management
- [ ] 3.2 Implement UI scenarios and verify with the desktop app
- [ ] 3.3 Create test task: write regression tests for group actions
- [ ] 3.4 Implement rename/refresh/delete group behavior
- [ ] 3.5 Create test task: write a CI validation checklist for merge readiness
- [ ] 3.6 Add CI gating to enforce passing tests and formatting

## Phase 4: Review and Merge
- [ ] 4.1 Conduct peer review against `governance.md`
- [ ] 4.2 Confirm all tests pass before merge
- [ ] 4.3 Verify branch history is clean and ready for merge
- [ ] 4.4 Merge only when `tests == passing` and merge gate checklist is complete
