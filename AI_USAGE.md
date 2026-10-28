# AI_USAGE.md

## Assistance used
- Used ChatGPT to help structure the assessment, draft manual test cases, review the sample AI-generated test, and outline client communication and live-interview notes.

## What I reviewed/changed
- Selected a consistent test strategy and mapped manual case IDs to automation names.
- Added checks for the full sorted price list, subtotal + tax = total, required checkout fields, cart state and protected-page access.
- Replaced fixed-sleep guidance with explicit, condition-based waits.
- Added an explicit warning that selectors and site behavior must be verified against the live practice site before use.
- Added a requirement to use a real, truthful incident example for Section C.

## Human verification still required
- Run the project locally and fix any compilation or locator issues.
- Confirm the current practice site's selectors, test-user details, product prices and tax behavior.
- Capture real execution output/screenshots and update this file with exact prompts and changes made.
- Do not claim tests passed until they have been executed.
