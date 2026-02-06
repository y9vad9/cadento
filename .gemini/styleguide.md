# Code Review Styleguide

This document provides essential context and instructions for AI-driven code reviews in the Cadento project.

## General Instructions

- **Focus on Intent and Design**: Prioritize reviewing business logic, architectural alignment, and domain integrity.
- **Ignore Build and Styling**: Do not report build errors, linting issues (Detekt), or formatting mistakes. These are strictly enforced by our CI pipeline.
- **Avoid Technical Jargon**: Review code based on ubiquitous language. Domain entities and operations should reflect business concepts, not technical implementation details.

## Core Architectural Patterns

We strictly follow Hexagonal Architecture and Domain-Driven Design (DDD).

- **Domain Integrity**: All business logic must reside within the domain layer. Use Cases should only orchestrate domain logic.
- **Packages as Namespaces**: We treat packages as semantic boundaries. Avoid "drawer" packages like `.utils`, `.model`, or `.common`. Everything related to a feature should be in its own namespace.
- **Semantic Typing**: Every domain concept must be represented by a type-safe value object (usually a Kotlin value class). Primitive types (String, Int, Long) should not exist unwrapped in the domain.

## Contract Violation and Input Handling

We distinguish between user-facing input and internal system invariants.

### User Input (Safe-First)
All user input or data crossing a trust boundary must return a type-safe result (sealed interface). We do not use exceptions for expected validation failures.

**Example Pattern:**
```kotlin
@JvmInline
value class TimerName private constructor(val string: String) {
    companion object {
        fun create(value: String): CreationResult {
            return when {
                value.isEmpty() -> CreationResult.Empty
                else -> CreationResult.Success(TimerName(value))
            }
        }
    }
    sealed interface CreationResult {
        data class Success(val timerName: TimerName) : CreationResult
        data object Empty : CreationResult
    }
}
```

### System Input (Guard-First)
Internal logic or trusted system input should use guards (like `require`, `check`). If a failure occurs here, it indicates a programmer error (bug), and an exception is appropriate.

**Example Pattern:**
```kotlin
fun applyTransition(transition: TimerStateTransition) {
    require(transition.updatedOldState.endTime != null) { 
        "End time must be set before applying transition" 
    }
    // logic...
}
```

## Application Layer Standards

- **Use Case Results**: Use Cases must return specific sealed interfaces for results, never generic `kotlin.Result` or `null`.
- **Dependency Injection**: Always inject abstractions for external concerns (e.g., `kotlin.time.Clock`, `TimerIdProvider`).
- **Repository Pattern**: Repositories should return `kotlin.Result<T>` to capture environmental failures, which are then mapped to domain results in the Use Case.

## Testing Standards

- **BDD Style**: All unit tests must use `// GIVEN`, `// WHEN`, `// THEN` comments.
- **Naming Conventions**:
    - For functions returning values: `methodName with condition returns Result`
    - For functions that should fail: `methodName with condition throws Exception`
    - For functions with side effects: `methodName with condition changes state`
- **Coverage**: Ensure that all branches and lines of new logic are covered by tests.
