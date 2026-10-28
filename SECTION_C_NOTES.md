# Section C — Live discussion notes

## C1. Checkout failing for some users: investigation approach

1. **Clarify impact first:** When did it start? Which users, regions, browsers, payment methods and order types are affected? Is it all checkout or a specific step? Are orders being charged/created despite an error?
2. **Assess severity and communicate:** Acknowledge the report, open/attach an incident ticket, identify an incident owner, and send a short update with known facts and next update time.
3. **Check signals:** Review application and server logs, error rates, traces, checkout/payment metrics, database health, alerts, recent deployments/configuration changes, feature flags and dependency status.
4. **Reproduce safely:** Use the approved test environment and representative non-sensitive test data. Record exact steps, account state, browser, timestamps, correlation/request IDs and expected vs actual behavior. Do not expose customer secrets.
5. **Narrow the cause:** Compare affected vs unaffected cases; inspect the failing request/response and logs; isolate UI validation, API, data, environment or third-party dependency. Check whether a recent release correlates with the start.
6. **Validate root cause:** Create a minimal reproducible case and test a proposed fix in staging. Add a regression test and verify adjacent checkout paths.
7. **Mitigate and close:** If impact is ongoing, discuss rollback/feature flag/workaround with engineering and product. Confirm recovery using monitoring and test orders; communicate resolution, impact and prevention actions.

## C2. Recent production issue — truthful STAR template

Use a real incident from your own experience. Do not claim that you personally found or fixed a production issue unless you did.

- **Situation:** In [project/release], [user-facing behavior] failed for [scope of users/transactions].
- **Detection:** I noticed it through [failed automated test / monitoring alert / user report] at [time].
- **Impact:** Users could not [specific action], resulting in [verified impact; quantify only if known].
- **Investigation:** I reproduced it using [environment and test data], checked [logs/API response/database/recent changes], and compared a working case with a failing one.
- **Root cause:** The evidence pointed to [actual cause]. I verified this by [specific experiment or log evidence].
- **Communication:** I notified [developer/QA lead/product owner] promptly and explained the user impact in plain language.
- **Fix and prevention:** We [fixed/rolled back/configured], added [regression/API/monitoring check], and verified recovery by [specific evidence].
- **Reflection:** Next time I would [earlier alert, better test data, clearer runbook, improved observability].

If you do not have a production incident, be transparent and describe a real integration or test-environment defect instead, clearly stating that it was not a production incident.
