# Section B — Answers

## B1. Review the AI-generated cart-total test

### Problems
1. Hard-coded base URL and credentials make environment switching difficult.
2. Uses broad `text=Add to cart`, which may select an unintended product or become ambiguous.
3. Uses a fixed 3-second sleep, making the test slow and flaky; use locator/actionability waits.
4. Selectors are embedded in the test instead of Page Objects, reducing maintainability.
5. It never asserts that a product was added or that the cart contains the expected item.
6. The test fills one-character customer details without confirming they are valid under the requirements.
7. It reads only `.summary_total_label` and checks that text exists; it does not verify the numeric total.
8. It does not compare item subtotal + tax to grand total.
9. It does not verify the expected product price or tax amount, so a wrong but non-empty total passes.
10. It does not finish checkout or verify order confirmation and cart cleanup.
11. It lacks test isolation/cleanup, failure screenshots and readable report configuration.
12. It does not cover failures or provide clear assertion messages.
13. It does not account for currency parsing/rounding.
14. It does not explicitly wait for the checkout overview to become visible before reading totals.

### Safer rewrite (Playwright example)
The assessment permits JavaScript/TypeScript and Playwright, Selenium or Cypress. The snippet below illustrates the assertions and wait strategy; adapt it to the chosen project and verify current selectors.

```ts
test('TC05 checkout total equals subtotal plus tax', async ({ page }) => {
  const baseUrl = process.env.BASE_URL ?? 'https://www.saucedemo.com';
  const username = process.env.STANDARD_USER ?? 'standard_user';
  const password = process.env.PASSWORD ?? 'secret_sauce';

  await page.goto(baseUrl);
  await page.getByTestId('username').fill(username);
  await page.getByTestId('password').fill(password);
  await page.getByTestId('login-button').click();

  const firstProduct = page.locator('.inventory_item').first();
  const expectedPriceText = await firstProduct.locator('.inventory_item_price').innerText();
  await firstProduct.getByRole('button', { name: 'Add to cart' }).click();
  await page.locator('.shopping_cart_link').click();
  await page.getByRole('button', { name: 'Checkout' }).click();

  await page.getByTestId('firstName').fill('Prince');
  await page.getByTestId('lastName').fill('Kumar');
  await page.getByTestId('postalCode').fill('110001');
  await page.getByRole('button', { name: 'Continue' }).click();

  await expect(page.locator('.summary_subtotal_label')).toBeVisible();
  const money = (s: string) => Number(s.replace(/[^0-9.]/g, ''));
  const subtotal = money(await page.locator('.summary_subtotal_label').innerText());
  const tax = money(await page.locator('.summary_tax_label').innerText());
  const total = money(await page.locator('.summary_total_label').innerText());

  expect(subtotal).toBeCloseTo(money(expectedPriceText), 2);
  expect(total).toBeCloseTo(subtotal + tax, 2);

  await page.getByRole('button', { name: 'Finish' }).click();
  await expect(page.locator('.complete-header')).toBeVisible();
});
```

**Review note:** The example is illustrative, not verified against the current site in this environment. Verify test IDs and selectors before running. In the Java project, implement the same checks in Page Objects.

## B2. AI feature evaluation — Test case generator

| Check | Test design | Scoring method |
|---|---|---|
| Coverage | Give a user story with positive, negative, boundary and permission scenarios; compare output with a reviewed expected set | Human rubric: required risk areas covered; optionally calculate coverage percentage |
| Invented requirements | Include a story that does not specify a password policy or timeout; check that generated tests do not assert invented rules | Human review against source story; zero unsupported requirements is pass |
| Duplicate cases | Supply a story with overlapping behaviors; detect semantically duplicate cases | Automated similarity/duplicate heuristic plus human confirmation |
| Valid JSON schema | Validate generated output against the exact required JSON schema, required fields, types and enums | Automatic: parse and schema validation; any invalid JSON is a fail |

Keep a versioned evaluation dataset, score each release against the same cases, and inspect regressions rather than relying on one successful example.

## B3. Jira defect report

**Title:** [Sorting] Price low-to-high places $29.99 before $15.99 on release 4.2.0

**Environment:** Sauce Demo practice application; release 4.2.0; Chrome; record exact Chrome version, OS and test date during execution.

**Preconditions:** User is logged in and the products page is displayed.

**Steps to reproduce:**
1. Open the product sorting dropdown.
2. Select “Price (low to high)”.
3. Compare the full product list from top to bottom.
4. Repeat the action to confirm reproducibility.

**Expected result:** Products are sorted in nondecreasing price order; $15.99 appears before $29.99.

**Actual result:** A product priced at $29.99 appears before one priced at $15.99. The issue reproduces on every run since release 4.2.0.

**Severity:** High / major (proposed). Sorting is a visible core shopping feature and consistently misleads users comparing prices. Confirm final severity with the team's impact/severity matrix. Priority: high, because it is reproducible and release-related.

**Evidence:** Screenshot showing the selected sort option and incorrectly ordered prices; screen recording if helpful; exact steps; timestamp; browser/OS details; release/build identifier; relevant console/network evidence if available.

## B4. Client message (under 150 words)

Hi, our release checks found two issues: product prices are not consistently ordered from lowest to highest, and checkout fails intermittently in about 1 out of 10 runs. The sorting issue is reproducible, while checkout needs further investigation; a passing retry does not prove it is safe. I recommend holding the full release until we identify the checkout cause and confirm the sorting fix. If Friday's date is critical, a practical alternative is to release only unaffected functionality behind a feature flag or to a limited group, provided checkout is excluded or independently verified and rollback/monitoring are ready. I’ll share a reproducible defect report, impact assessment and retest results as soon as we have them. Can we agree on the release scope after a short risk review?

## B3/B4 release decision note
Do not hide a known defect or represent a flaky retry as a pass. Explain impact, uncertainty, mitigation, rollback and the decision owner. Record any business acceptance of residual risk.
