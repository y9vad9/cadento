# Architecture

Cadento is built upon the foundational principles of Hexagonal Architecture and Domain-Driven Design. The primary objective is to maintain a strict separation between business logic and the technical environment, ensuring the codebase remains a clear reflection of the problem domain rather than a collection of technical implementation details.

## Core Philosophy

The architecture is centered on the concept of intent. We do not organize code by where it fits into a framework, but by what it represents in the business domain. The system prioritize business concepts over technical convenience, using ubiquitous language to ensure that domain entities and operations use business terminology. This approach prevents technical jargon from leaking into the core logic. For a detailed overview of finding the right balance between these architectural patterns, check this: https://y9vad9.com/en/notes/finding-balance-between-ddd-hexagonal-and-clean-architectures

## Structural Methodology

Our approach to code organization is based on semantic boundaries rather than technical layers. Packages are treated as namespaces that define a clear area of responsibility. We avoid the common mistake of creating generic containers like ".model", ".utils", or ".common", which often lead to hidden coupling and a fragmented domain. Instead, all concepts related to a specific business area reside within a single package. For a detailed overview of this package naming philosophy, check this: https://y9vad9.com/en/notes/package-naming-problem

## Domain Layer

The domain layer is the heart of the application. It contains the business rules, entities, and value objects that define the system behavior. This layer is entirely pure and depends only on the Kotlin standard library and essential multiplatform tools like coroutines.

### Semantic Typing

We move beyond primitive types to ensure that every domain concept is represented by a semantically meaningful type. Typically implemented as Kotlin value classes, these types provide compile-time safety and centralize validation at the point of creation. A timer identifier is not a raw UUID, but a TimerId, and a timer name is a specific TimerName type that enforces its own invariants. For a detailed overview of the importance of semantic typing, check this: https://y9vad9.com/en/notes/semantic-typing

### Rich Domain Entities

Entities are designed to be rich and behavior-oriented rather than simple data holders. Complexity is encapsulated within the entity itself, where state transitions are managed to maintain consistency. A FocusDividendTimer, for instance, provides a stop method that orchestrates internal state changes and balance calculations. This ensures that business logic remains where it belongs—within the domain—and prevents the use cases from becoming bloated with procedural logic.

## Application Layer

The application layer acts as the coordinator. It contains Use Cases that serve as inbound ports, defining the operations available to the outside world. Each Use Case is responsible for a single business operation, fetching the necessary entities from a repository, invoking domain logic, and persisting the result. Use Cases also manage the injection of external dependencies like clocks or identity providers to keep the domain layer pure.

### Result Modeling and Contract Enforcement

Failures and boundary conditions are modeled as first-class citizens within the application layer. We do not rely on exceptions for expected business outcomes. Instead, Use Cases define specific sealed interfaces for their results, allowing callers to explicitly handle scenarios like a missing resource or a valid business rejection. For a detailed overview of how we model contracts and handle violations, check this: https://y9vad9.com/en/notes/contract-violation-handling

## Infrastructure and Integration

The infrastructure layer provides concrete implementations for the outbound ports. This includes database persistence via SQLDelight and external communication.

### Networking and Data Flow

Cadento follows a local-first approach. All primary data operations occur against the local database to ensure offline availability and responsiveness. Synchronization with remote systems is handled using a regular REST API built with Ktor. Incoming updates are stored in the local database, and the UI reacts to these changes.

### Mapping Strategy

Data models are unique to each layer to prevent technical constraints from leaking into the domain. Database types and SDK models are kept separate from domain entities. Explicit mapping occurs at the boundary of the infrastructure layer, ensuring that the domain remains decoupled from specific persistence or transmission formats.

## Further Reading

For high-level project information and features, refer to the [README](README.md). If you wish to contribute to the project, please consult the [Contributing](CONTRIBUTING.md) guide.