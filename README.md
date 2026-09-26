# GitLab Analytics Dashboard

This repository is maintained by CS/ECE 506 Team 29. This specification is a living document and should be updated as product decisions and customer feedback evolve.

## Project Abstract

The product is a GitLab analytics dashboard that helps project members quickly understand important project activity without manually inspecting many GitLab pages or raw API responses.

## Product Owner Decisions

The following decisions establish the initial product direction and backlog order. They are Product Owner decisions for planning purposes and are not claims of customer approval.

### Product Goal

Help GitLab project members quickly understand important project activity through a focused analytics dashboard.

### Primary User

The initial primary user is a GitLab project member or developer. This role is also recorded in [work item #8](https://git.doit.wisc.edu/cdis/cs/courses/cs506/fa2026/team/T29/Project_29/-/work_items/8).

### Initial Product Capabilities and Backlog Priority

| Backlog order | Capability | Priority |
| --- | --- | --- |
| 1 | Merge request review and merge time analytics | Priority 1 |
| 2 | CI/CD and test result trends | Priority 1 |
| 3 | Issue and requirement history | Priority 2 |
| 4 | Contributor activity analysis | Priority 2 |
| 5 | Additional development trend or bottleneck insights | Lower priority / later |

This ordering identifies what the team should refine first. It does not state that the customer has approved every capability.

### Sprint 0 Product Goal

Create a clear, reviewable product foundation by:

- defining the initial product direction;
- organizing and prioritizing the backlog;
- documenting initial user stories;
- preparing product-level acceptance criteria;
- documenting remaining customer questions; and
- distinguishing Product Owner decisions from unresolved technical decisions.

## Initial User Stories and Acceptance Criteria

These stories translate the ordered backlog into product outcomes. Metric definitions and customer-specific expectations still require validation as noted below.

### Story 1: Merge Request Analytics

> As a GitLab project member, I want to view merge request review and merge times so that I can identify review bottlenecks.

Acceptance criteria:

- The dashboard clearly displays review-time and merge-time information for the selected GitLab project.
- The meaning of each displayed metric is documented.
- Empty or unavailable merge request data is handled without presenting a misleading result.
- The user can identify which GitLab project and data period the result represents.
- The displayed result can be traced back to its GitLab source data.

### Story 2: CI/CD and Test Trends

> As a GitLab project member, I want to see CI/CD and test result trends so that I can quickly understand project stability.

Acceptance criteria:

- The dashboard clearly displays CI/CD and test result trends for the selected GitLab project.
- The meaning of each displayed trend or status is documented.
- Empty or unavailable pipeline or test data is handled without presenting a misleading result.
- The user can identify which GitLab project and data period the result represents.
- The displayed result can be traced back to its GitLab source data.

### Story 3: Issue and Requirement History

> As a GitLab project member, I want to view issue and requirement history so that I can understand how project scope changes over time.

Acceptance criteria:

- The dashboard presents issue and requirement history for the selected GitLab project in a clear order.
- The types of changes included in the history are documented.
- Empty or unavailable history is communicated clearly.
- The user can identify which GitLab project and data period the history represents.
- Displayed history items can be traced back to their GitLab source data.

### Story 4: Contributor Activity

> As a GitLab project member, I want to view contributor activity so that I can understand participation across the project.

Acceptance criteria:

- The dashboard clearly presents contributor activity for the selected GitLab project.
- The activities included in the analysis are documented.
- Empty or unavailable contributor data is communicated clearly.
- The user can identify which GitLab project and data period the result represents.
- The displayed result can be traced back to its GitLab source data.

## Customer Clarification Still Needed

The Product Owner has established the initial direction above. Customer or stakeholder input is still needed to clarify:

- whether intended user groups extend beyond GitLab project members or developers;
- which metrics or capabilities the customer considers most valuable;
- whether the customer has specific acceptance expectations for the initial stories; and
- whether there are restrictions on which GitLab data the product may access.

Customer feedback may change the backlog order or acceptance criteria. Until that feedback is recorded, the priorities above remain the Product Owner's initial planning decisions rather than customer-approved requirements.

## Technical and Architecture Decisions Not Yet Made

This Product Owner contribution does not select or claim a technical implementation. The following remain outside its scope:

- **TODO:** Add the approved technology stack after a team/TA decision.
- **TODO:** Add an architecture diagram after the technical design is approved.
- **TODO:** Document approved GitLab integration and authorization details.
- **TODO:** Document approved data-storage and deployment design.

## Requirements and Backlog Traceability

| Source | Recorded statement | Relationship to this specification |
| --- | --- | --- |
| REQ-1 | The team will maintain a backlog. | The ordered capability list above records the initial PO backlog priority. |
| REQ-2 | The system will use a MySQL database. | Existing recorded requirement; implementation and database design are outside this PO contribution. |
| REQ-3 | The database will be hosted on the team's CSL VM. | Existing recorded requirement; architecture and deployment are outside this PO contribution. |
| [Work item #8](https://git.doit.wisc.edu/cdis/cs/courses/cs506/fa2026/team/T29/Project_29/-/work_items/8) | Identify the primary user role and clarify the user's dashboard need. | Supports the primary user, product goal, and initial user stories above. |

## Standards & Conventions

[Style Guide & Conventions](STYLE.md)
