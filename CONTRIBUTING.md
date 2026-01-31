# Contributing

This project adheres to the Developer Certificate of Origin (DCO). By contributing to this repository, you certify that
your contributions comply with the DCO.

## What is the DCO?

The DCO is a simple statement that contributors make to certify the origin of their code. It ensures that:

* The contributor wrote the code or has the right to submit it under the project’s license.
* The contribution complies with the project’s licensing and contribution guidelines.

### How to Sign Off Commits

Each commit must contain a sign-off line. This is done by adding the following line to the end of the commit message:

```
Signed-off-by: Your Name [your.email@example.com](mailto:your.email@example.com)
```

You can sign off using the -s or --signoff flag with git commit:

```bash
git commit -s -m "Your commit message"
```

### Web-based Commits

If you contribute via GitHub’s web interface, ensure that you check the box to sign off on the commit when submitting
your change.

## Why This Matters

Using the DCO helps protect all contributors and maintainers by ensuring that the source of the code is known and
accepted. It provides legal clarity and helps build a transparent and trustworthy development process.

## Enforcement

Pull requests that do not include properly signed-off commits will not be merged. You will be asked to amend your
commits to include the required sign-off.

---

## Workflow and Best Practices

To maintain a clean and efficient development process, please follow these guidelines:

### Branch Naming Rules

All branches must follow this strict naming format to ensure clarity and consistency across contributions:

%type%/%issue-number%-%scope%-%short-description%

Accepted `%type%` values:

* feature — for new features.
* bugfix — for fixing a bug.
* hotfix — for urgent fixes to main.
* chore — for non-functional changes (e.g., formatting, dependency bumps).
* docs — for changes to documentation only.
* refactor — for code restructuring without behavior change.
* test — for adding or changing tests.

`%scope%`:

* Should refer to a module, feature, or subsystem (e.g., auth, ui, timer).
* Use core or common for cross-cutting concerns.
* Use lowercase, hyphenated words.

`%short-description%`:

* Concise description using lowercase, hyphenated words.
* Avoid filler words like “add” or “implement” — the type already implies intent.

Branches that do not follow this format may be rejected or renamed.

### Starting Work on Features or Fixes

* Before starting significant work, open a discussion issue to propose the feature or report the bug. This helps avoid
  duplicate efforts and aligns development with project goals.
* Smaller fixes or improvements may be submitted directly as pull requests, but discussion is encouraged if the change
  is substantial.

### Pull Requests

* Each pull request should focus on a single logical change.
* Include clear, descriptive titles and detailed descriptions of the changes and their purpose.
* Reference related issues or discussions when applicable.
* Ensure your code passes all tests and follows the project's coding standards.
* Keep pull requests up to date with the target branch.

### Code Style and Quality

* Follow the established coding conventions and best practices. We use default Detekt ruleset to enforce best practices.
* Write meaningful commit messages. Use this guide for that – https://www.baeldung.com/ops/git-commit-messages.
* Ensure tests cover new features or bug fixes.
* Run linters and static analysis tools before submitting changes.

### Documentation

* Update documentation (README, ARCHITECTURE.md, etc.) when your changes affect usage, architecture, or project
  behavior. Note: such changes should be discussed prior to modifying.
* Reference documentation issues or requests in your pull request.

### Additional Resources

* Review the [Architecture Overview](ARCHITECTURE.md) file to understand the project’s architecture and design
  principles.
* Follow the [Code of Conduct](CODE_OF_CONDUCT.md) to maintain a respectful and collaborative community.

Thank you for contributing! Your efforts help improve the project for everyone.