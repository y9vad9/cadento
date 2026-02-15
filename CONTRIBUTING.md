# Contributing Guidelines

Thank you for your interest in Cadento! To maintain high code quality and a clear project history, please follow these Git and GitHub conventions.

## 1. Standardized Commits

We use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) (`type(scope): subject`).

### Standardized Commit Requirements
- **Format**: `type(scope): subject [#issue]`
- **Sign-off**: Every commit must be signed off (use `git commit -s`). This adds a `Signed-off-by: Name <email>` line to the message.
- **Cryptographic Signing**: Every commit must be GPG/SSH signed (use `git commit -S`).

### Allowed Scopes
Scopes must be one or more of the following enums (separated by commas if multiple, no spaces):
- `tasks`: Task domain logic, infrastructure, or UI.
- `timers`: Timer domain logic, infrastructure, or UI.
- `ui-kit`: Common UI components and design system implementations.
- `foundation`: Foundational utilities (coroutines, time extensions).
- `core`: System-wide logic or global configurations.
- `app`: Main application entry points and orchestration.
- `build`: Gradle build scripts, conventions, and plugin changes.
- `deps`: Dependency updates (including dependabot).
- `ci`: GitHub Actions and CI/CD pipelines.
- `docs`: Documentation updates.

**Note:** While multiple scopes (e.g., `chore(deps,build): ...`) and multiple issue references (e.g., `[#123,#456]`) are supported, we strongly recommend keeping PRs localized to a single scope or issue whenever possible.

### Examples
- `feat(timers): implement pomodoro countdown logic`
- `fix(ui-kit): resolve button text visibility in dark mode`
- `chore(deps,build): update dependencies and gradle version [#202,#203]`

---

## 2. Pull Request (PR) Conventions

### Title Format
PR titles must follow the commit format exactly, with optional issue references.
**Format:** `<type>(<scope>): <subject> [#issue-number,#issue-number]`

- **Good:** `feat(tasks): add drag-and-drop support [#123]`
- **Bad:** `feat(tasks): add drag-and-drop support (#456)`

### Description Requirements
Every PR description must contain:
- **Summary**: A brief overview of the changes.
- **Related Issue**: Link using keywords (e.g., `Closes #123`).
- **Checklist**: Ensure local tests pass and conventions are followed.

---

## 3. Branch Naming Conventions

Branch names must follow a structured format similar to commits.

**Format:** `<type>/<scope>-<subject>`

- **Allowed Types**: Same as commits (`feat`, `fix`, `docs`, etc.).
- **Allowed Scopes**: Same as commits (`tasks`, `timers`, `ui-kit`, etc.).
- **Constraints**:
    - Use lowercase alphanumeric characters and hyphens (`-`) only.
    - No spaces or underscores (`_`) allowed.
    - The subject should be a short, kebab-case description of the task.

### Examples
- `feat/timers-pomodoro-logic`
- `fix/ui-kit-button-visibility`
- `docs/foundation-readme-update`

---

## 4. Issue Conventions

### Humane Titles
Issue titles must be human-readable and descriptive for non-developers. Do **not** use technical prefixes or bracketed tags.

- **Good:** "Start button is disabled after timer finishes"
- **Bad:** `fix: timer button disabled`, `[Bug] button state`

### Categorization via Labels
Technical categorization is handled strictly through GitHub Labels. Ensure every issue is tagged with:
- `type:*`: (e.g., `type:bug`, `type:feature`, `type:documentation`)
- `priority:*`: (e.g., `priority:high`, `priority:medium`)
- `scope:*`: (e.g., `scope:tasks`, `scope:build`, `scope:deps`)

### Status Tracking
Use `status:*` labels to track the lifecycle:
- `status:triage`: Needs review.
- `status:blocked`: Waiting on external factors.
- `status:in-progress`: Actively being worked on.
- `status:duplicate`: This issue or pull request already exists.
- `status:wontfix`: This will not be worked on.
- `status:invalid`: Doesn’t meet guidelines or incomplete.
