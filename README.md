# Harvee Technologies — QA Automation Assessment

Candidate: Prince Kumar  
Role: QA Automation Tester

## Stack and assumptions
- Java 17, Maven, Selenium 4, TestNG.
- Practice site: https://www.saucedemo.com (as specified by the assessment).
- Test data is configurable using environment variables `BASE_URL`, `STANDARD_USER`, `LOCKED_USER`, and `PASSWORD`.
- This is a starter project. Verify selectors and tax/price assertions against the current practice site before submission.

## Run
1. Install JDK 17+ and Maven.
2. Set environment variables if desired:
   - `BASE_URL=https://www.saucedemo.com`
   - `STANDARD_USER=standard_user`
   - `LOCKED_USER=locked_out_user`
   - `PASSWORD=secret_sauce`
3. Run `mvn test`.
4. TestNG results are written under `target/surefire-reports`.

## Project layout
- `src/main/java/pages`: Page Object Model classes
- `src/test/java/base`: browser setup and failure screenshots
- `src/test/java/tests`: tests named with manual test case IDs
- `ANSWERS.md`: Section B responses
- `AI_USAGE.md`: transparent record of AI assistance and human verification required

## Manual-to-automated mapping
| Manual ID | Automated test |
|---|---|
| TC01 | TC01_standardUserCanLogIn |
| TC02 | TC02_lockedOutUserCannotLogIn |
| TC03 | TC03_sortPriceLowToHighChecksWholeList |
| TC04 | TC04_addTwoRemoveOneChecksBadgeAndContents |
| TC05 | TC05_checkoutCalculatesTotalAndEmptiesCart |
| TC06 | TC06_checkoutRequiredFieldsAreValidated (parameterized) |
| TC07 | TC07_loggedOutUserCannotOpenProductsUrl |
| TC08–TC25 | Manual only / candidates for later automation as listed in `MANUAL_TEST_CASES.md` |

## Growing the suite to 200 tests
Keep page objects and test data separate, use data providers for combinations, group tests by smoke/regression/checkout, and tag tests by risk. Run smoke tests on each pull request and broader regression in CI. Add API-level checks where an API is available, avoid duplicating every UI permutation, and use stable test data with cleanup. Track flaky tests, failure screenshots, browser logs and JUnit XML. Review test ownership and periodically remove redundant or obsolete tests.
