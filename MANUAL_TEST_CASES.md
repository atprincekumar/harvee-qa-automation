# Section A1 — Manual test suite

## Questions for the product owner and assumptions
1. Is the postal code validated against a country-specific format? **Assumption:** any non-empty value is accepted unless a validation rule is documented.
2. Should the cart badge count quantities or distinct product lines? **Assumption:** it counts total units; for this flow each product is added once.
3. What currency and tax-rounding rules apply? **Assumption:** use the displayed currency and the app's displayed tax; compare rounded monetary values to two decimal places.
4. Should the cart persist after logout/login? **Assumption:** persistence is not required unless specified; verify and document observed behavior.
5. What should happen when a user refreshes checkout or navigates backward? **Assumption:** no duplicate order should be placed and the flow should remain consistent.
6. Are keyboard-only navigation and screen-reader labels required? **Assumption:** core flow must be keyboard-operable and controls must have meaningful accessible names.
7. Which browsers and viewport sizes are officially supported? **Assumption:** current Chrome desktop is primary; responsive layout is checked at a narrow mobile viewport.
8. Should sorting be stable for equal prices/names? **Assumption:** ascending/descending order must be correct; tie ordering is not specified.

## Test cases
| ID | Area | Type | Title | Preconditions | Steps | Test data | Expected result | Priority | Automate? and why |
|---|---|---|---|---|---|---|---|---|---|
| TC01 | Login | Positive | Standard user logs in | Login page | Enter valid credentials; submit | standard_user / configured password | Products page opens | P1 | Yes — smoke coverage |
| TC02 | Login | Negative | Locked-out user is rejected | Login page | Enter locked user credentials; submit | locked_out_user | Clear error; remains on login | P1 | Yes — required negative case |
| TC03 | Sorting | Positive | Sort price low to high | Logged in | Select price ascending; read every product price | All displayed products | Entire list is nondecreasing | P1 | Yes — business-critical |
| TC04 | Cart | Positive | Add two items and remove one | Logged in, empty cart | Add two different items; verify badge; remove one | Two products | Badge and cart contents show one item | P1 | Yes — core cart behavior |
| TC05 | Checkout | Positive | Complete checkout and verify totals | One or more items in cart | Checkout; enter required fields; verify subtotal + tax = total; finish | Valid customer details | Confirmation shown; cart empty | P1 | Yes — end-to-end critical path |
| TC06 | Checkout | Negative/boundary | Required fields are validated individually | Logged in with item in cart | Submit checkout with each one of first name, last name, postal code blank | Blank each field in turn | Correct validation error; no order placed | P1 | Yes — parameterized |
| TC07 | Security | Negative | Direct products URL blocked after logout | Logged in | Log out; navigate directly to /inventory.html | Direct URL | User is redirected to login or access denied | P1 | Yes — access control |
| TC08 | Login | Negative | Invalid password is rejected | Login page | Enter valid username and incorrect password | wrong password | Error shown; no login | P1 | Yes — simple regression |
| TC09 | Login | Negative | Empty credentials are rejected | Login page | Submit with both fields blank | Blank values | Validation error shown | P2 | Yes — validation |
| TC10 | Sorting | Positive | Sort product names A to Z | Logged in | Choose name ascending; capture all names | Product list | Names are alphabetically ascending | P2 | Yes — deterministic |
| TC11 | Sorting | Positive | Sort product names Z to A | Logged in | Choose name descending; capture all names | Product list | Names are alphabetically descending | P2 | Yes — deterministic |
| TC12 | Sorting | Positive | Sort price high to low | Logged in | Choose price descending; capture all prices | Product list | Entire list is nonincreasing | P1 | Yes — sorting coverage |
| TC13 | Cart | Positive | Remove item from products page | Logged in; item added | Click Remove on selected item | One product | Button changes to Add to cart; badge updates | P2 | Yes — core behavior |
| TC14 | Cart | Positive | Remove item from cart page | Logged in; item added | Open cart; remove item | One product | Item disappears; badge updates or disappears at zero | P2 | Yes — core behavior |
| TC15 | Checkout | Positive | Cancel checkout overview | Items in cart; overview open | Click Cancel | Existing cart item | Returns without placing order; cart preserved | P2 | Yes — navigation behavior |
| TC16 | Checkout | Boundary | Checkout with minimum accepted field values | Item in cart | Enter one-character names and short non-empty postal code | A / B / 1 | App follows documented validation; no crash | P2 | Manual initially — requirement unclear |
| TC17 | Checkout | Negative | Refresh/back navigation does not duplicate order | Checkout flow | Refresh or navigate back before finishing; inspect state | Valid details | No duplicate order; state remains consistent | P2 | Manual first — browser behavior |
| TC18 | Usability | Usability | Cart count and item labels are understandable | Logged in | Add/remove items and inspect labels/status | Two products | Feedback is clear and cart state is easy to understand | P3 | No — human judgment |
| TC19 | Responsive | Compatibility | Checkout works at narrow viewport | Logged in | Set viewport to 375×812; complete checkout | Mobile viewport | Controls visible, usable, no blocking overlap | P2 | Partially — screenshots plus visual review |
| TC20 | Accessibility | Accessibility | Core flow is keyboard-operable | Login page | Use Tab/Shift+Tab, Enter/Space through login, sorting and checkout | Keyboard only | Focus visible; controls operable in logical order | P2 | Partially — automated accessibility scan plus manual |
| TC21 | Security | Negative | Protected cart URL blocked after logout | Logged in then logout | Navigate directly to /cart.html | Direct URL | Redirect/access denied; no protected data shown | P1 | Yes — security smoke |
| TC22 | Session | Negative | Browser back after logout does not expose protected content | Logged in then logout | Use browser Back and inspect page | Existing session | Protected content is not usable or exposed | P1 | Yes — security regression |
| TC23 | Cart | Boundary | Empty cart checkout is unavailable or handled safely | Logged in; cart empty | Open cart and inspect checkout path | No products | User cannot place an empty order | P2 | Yes — business rule check |
| TC24 | Browser | Compatibility | Core flow in another supported browser | Test account available | Repeat login, cart and checkout in second supported browser | Supported browser | Core flow behaves consistently | P2 | No initially — limited browser matrix |
| TC25 | Exploratory | Exploratory | Explore known-problem user behavior for 15 minutes | Login page; known-problem account identified on practice page | Explore product images, sorting, cart updates, checkout validation, navigation; record steps and evidence | `problem_user` if listed | Findings documented with reproducible steps, impact and screenshots | P2 | No — exploratory testing benefits from human observation |

## 15-minute exploratory charter
**Mission:** Explore the known-problem user (`problem_user`, if listed on the practice login page) through product browsing, sorting, cart changes and checkout.

- Minutes 0–3: log in and inspect product cards, images, labels and layout.
- Minutes 3–6: test all sorting choices and compare displayed values.
- Minutes 6–9: add/remove products and compare badge with cart contents.
- Minutes 9–12: enter checkout, try missing fields and use Back/Cancel.
- Minutes 12–15: reproduce the most important anomaly, capture screenshot and record exact steps, expected/actual result, severity and any workaround.

## Test strategy (under 150 words)
Automate stable, repeatable high-risk paths at the UI layer: valid and locked-out login, complete-list sorting, cart add/remove, checkout totals, required-field validation and protected-page access after logout. Use Page Objects, explicit waits, isolated test setup and failure screenshots. If APIs are available, validate product/catalog data, cart calculations, checkout validation and order creation at the API layer; cover calculation edge cases with unit tests. Keep exploratory testing, accessibility judgment, responsive visual review and unclear postal-code rules manual until requirements and tools are confirmed. The biggest remaining risk is a checkout/order integrity issue that may intermittently prevent or duplicate orders. I would recommend resolving the sorting defect and investigating flaky checkout before a broad release, with a limited release only if business stakeholders accept documented risks and monitoring/rollback are ready.
