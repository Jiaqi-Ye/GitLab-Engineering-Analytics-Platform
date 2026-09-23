# Walking Skeleton Plan

## Purpose

The walking skeleton will prove that the application's front end, back end, and database can communicate through one minimal end-to-end interaction. It is an integration check, not a claim that a dashboard feature or minimum viable product is complete.

## Confirmed Constraints

- The database technology is MySQL (REQ-2).
- The database will be hosted on the team's CSL VM (REQ-3).

No front-end framework, back-end framework, GitLab integration method, or deployment architecture is confirmed by the current repository.

## Planned End-to-End Path

1. A user performs one minimal action in the front end.
2. The front end sends a request to the back end.
3. The back end writes a small test record to, or reads a small test record from, MySQL on the team's CSL VM.
4. The back end returns the result to the front end.
5. The front end displays the returned result or a clear error.

The exact test record and interaction are intentionally left open until the team confirms them. They do not need to represent a finished analytics capability.

## Walking Skeleton Acceptance Criteria

- A user can initiate the chosen interaction from the front end.
- The interaction reaches a back-end endpoint.
- The back end successfully writes to or reads from the MySQL database on the team's CSL VM.
- The result of the database operation returns through the back end and is visible in the front end.
- A database or connection failure produces a visible error rather than an unexplained success.
- Setup and run instructions for the demonstrated path are documented so another team member can reproduce it.

## Decisions Still Required

| Decision | Status | Needed before implementation |
| --- | --- | --- |
| Front-end technology | TODO | Team/customer-approved choice |
| Back-end technology | TODO | Team/customer-approved choice |
| Front-end/back-end request format | TODO | Minimal endpoint contract |
| Skeleton interaction and test record | TODO | Small, demonstrable choice that does not imply a product requirement |
| MySQL schema for the test record | TODO | Minimal schema after the interaction is selected |
| GitLab data access | TODO | Confirm whether the skeleton should use GitLab data; if so, define access and authorization |
| CSL VM connection and deployment steps | TODO | Team-owned configuration and reproducible instructions |

## Traceability

- [REQ-2 and REQ-3](https://git.doit.wisc.edu/cdis/cs/courses/cs506/fa2026/team/T29/Project_29/-/requirements_management/requirements) provide the confirmed database constraints.
- [Work item #9](https://git.doit.wisc.edu/cdis/cs/courses/cs506/fa2026/team/T29/Project_29/-/work_items/9) tracks the walking skeleton.
- [README.md](README.md) records the product story, Sprint 0 goal, and open product questions.
