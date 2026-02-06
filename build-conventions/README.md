# Build Conventions

This directory contains the custom Gradle plugins and convention scripts used across the Cadento project. These conventions ensure consistent configuration for Kotlin Multiplatform, Android, Compose, and other recurring architectural layers.

## Purpose

The build conventions module centralization common build logic to reduce duplication and maintenance overhead in individual feature modules. Each plugin represents a specific intent or architectural layer, allowing modules to declare their purpose through a single plugin application.

## Key Conventions

- Domain: Configures pure Kotlin Multiplatform modules with essential dependencies and linter rules.
- Application: Sets up application layer modules with support for dependency injection and business orchestration logic.
- Infrastructure: Provides configuration for persistence and external integration modules, including SQLDelight and network clients.
- Presentation: Configures MVI-based presentation logic modules.
- UI: Sets up Compose Multiplatform and platform-specific UI components.

## Usage

Feature modules apply these conventions by using the corresponding plugin IDs defined in this module. This keeps individual build scripts declarative and focused on module-specific dependencies.
