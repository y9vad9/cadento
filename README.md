![Latest GitHub release](https://img.shields.io/github/v/release/y9vad9/cadento?include_prereleases)
![GitHub](https://img.shields.io/github/license/y9vad9/cadento)
![GitHub issues](https://img.shields.io/github/issues/y9vad9/cadento)

# Cadento

Cadento is a multiplatform productivity application built with Kotlin, Compose Multiplatform, and Coroutines. The application is designed to support both individual and team productivity through a local-first architecture, providing robust task management and customizable timers.

## Core Features

- Customizable Timers
    - Create and manage timers tailored to specific workflows.
    - Track time allocation across different tasks and projects.
- Task Management
    - Integrated TODO lists with full lifecycle management.
    - Direct reference of tasks within active timers for seamless tracking.
- Focus Dividend Timer
    - A specialized timer designed to reward deep focus with earned free time.
    - Configurable coefficients to adapt the earning rate to your personal productivity needs.
- Data Synchronization
    - Local-first storage ensuring full offline capability.
    - Optional synchronization using a REST API built with Ktor for multi-device support.

## Architectural Principles

The project follows Hexagonal Architecture and Domain-Driven Design principles. We prioritize semantic typing, rich domain models, and a strict separation between business logic and technical infrastructure. For a comprehensive explanation of our approach, please refer to the [Architecture](ARCHITECTURE.md) file.

## Project Status

The project is currently in the early stages of active development. You can track our progress and upcoming features on [our project board](https://github.com/users/y9vad9/projects/5).

## Contributing

We welcome community contributions that align with our architectural vision and code standards. Please review [Contributing](CONTRIBUTING.md) for detailed guidelines on how to participate in the project.

## Code of Conduct

All participants are expected to uphold the standards of our community as defined in the [Code of Conduct](CODE_OF_CONDUCT.md) file.

## License

This project is licensed under the MIT License. See the [License](LICENSE) file for more details.
