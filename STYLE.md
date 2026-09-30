# Style Guide

## Code
- Match the style of the surrounding code.
- Use clear names with units where relevant (`timeout_ms`, not `t`).
- Comment the *why*, not the *what*.
- Delete dead code instead of commenting it out.

## Formatting
- 4-space indentation, no tabs.
- Max line length: 100 characters.

## Git
- Never push directly to `main`.
- Branch names: `INITIALS-short-name` or `USERNAME-short-name`.
- Commit messages: imperative, descriptful, and under 72 characters (`Add button debounce`).
- Every PR gets at least one review from someone else before merging.

## Team
- Create tests along with new functionality.
- Tests and CI must pass before merging.