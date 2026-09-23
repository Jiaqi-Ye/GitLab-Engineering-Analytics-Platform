# GitLab Analytics Dashboard

This repository is maintained by CS/ECE 506 Team 29. This specification is a living document and should be updated as requirements are confirmed with the course customer.

## Project Abstract

The project is a GitLab analytics dashboard for GitLab project members and developers. Its purpose is to make important project activity understandable without requiring a user to search through individual GitLab pages or raw API responses. The exact dashboard capabilities and presentation remain subject to customer clarification.

## Customer

The course customer and the intended user population beyond GitLab project members/developers have not yet been recorded in the repository.

**TODO (Product Owner):** Confirm the course customer, intended users, and the project activity that is most important for the dashboard to present.

## Product Story

> As a GitLab project member or developer, I want a dashboard view of important project activity so that I can understand the project without manually searching GitLab pages or API responses.

## Primary User

The primary user currently recorded in work item #8 is a GitLab project member or developer. Other intended users have not yet been confirmed with the course customer.

## Candidate Ideas and Approved Requirements

Existing backlog notes identify the following as candidate areas, not yet confirmed requirements:

- merge request activity and review/merge times;
- CI/CD and test results;
- issue and requirement history;
- contribution activity from project members; and
- development trends or bottlenecks.

### Acceptance Criteria Status

The product story is not ready for implementation until the Product Owner confirms with the customer:

- which candidate activity areas are in scope and their priority;
- what GitLab project(s) and data sources the dashboard may access;
- how users select or identify a project;
- what calculations or definitions apply to any displayed analytics; and
- what evidence will allow each selected capability to be accepted.

Until those questions are answered, candidate areas must not be treated as approved requirements.

## Sprint 0 Goal

Create a reviewable product foundation for later development by maintaining the backlog, recording existing requirements, clarifying the primary user and product story, and identifying questions that require customer input. This PO goal does not claim implementation, architecture, or technology-selection work.

## Requirements and Backlog Traceability

| Source | Recorded statement | Repository evidence / next step |
| --- | --- | --- |
| REQ-1 | The team will maintain a backlog. | The GitLab Sprint 0 issue board is the current backlog. Product questions above still need customer clarification. |
| REQ-2 | The system will use a MySQL database. | Recorded in the GitLab requirements tracker; implementation details are outside this PO contribution. |
| REQ-3 | The database will be hosted on the team's CSL VM. | Recorded in the GitLab requirements tracker; architecture and deployment work are outside this PO contribution. |
| [Work item #8](https://git.doit.wisc.edu/cdis/cs/courses/cs506/fa2026/team/T29/Project_29/-/work_items/8) | Identify the primary user role and clarify the user's dashboard need. | Primary role and product story are documented above; capability scope and acceptance evidence remain open. |
| [Work item #9](https://git.doit.wisc.edu/cdis/cs/courses/cs506/fa2026/team/T29/Project_29/-/work_items/9) | Plan the walking skeleton. | Tracked separately; its technical planning and implementation are not claimed as PO work in this contribution. |

## Customer Clarification Questions

- Which candidate dashboard capability is in scope and highest priority?
- Who is the course customer, and are there intended users beyond GitLab project members/developers?
- Which GitLab data may the application access, and how will access be authorized?
- How should a user select or identify a GitLab project?
- How should each selected analytic be defined?
- What customer-approved acceptance criteria apply to the first capability?

## Standards & Conventions

[Style Guide & Conventions](STYLE.md)
