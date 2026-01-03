# CODEBUDDY.md This file provides guidance to CodeBuddy when working with code in this repository.

## Commands

- **Build Android App**: `./gradlew :composeApp:assembleDebug`
- **Run Desktop App**: `./gradlew :composeApp:run`
- **Run Web App (Wasm)**: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
- **Run Web App (JS)**: `./gradlew :composeApp:jsBrowserDevelopmentRun`
- **Run Parser Tests**: `./run_parser_tests.sh` or `./gradlew :latex-parser:cleanJvmTest :latex-parser:jvmTest`
- **Run All Tests**: `./gradlew check`
- **Clean Project**: `./gradlew clean`

## High-level Code Architecture

This project is a Kotlin Multiplatform (KMP) LaTeX rendering library and demonstration application. It is structured into a multi-module Gradle project with a clear separation of concerns between parsing, rendering, and platform integration.

### Core Modules

1.  **latex-base** (`/latex-base`):
    -   Foundation module with minimal dependencies.
    -   Defines base types and interfaces used across other modules.

2.  **latex-parser** (`/latex-parser`):
    -   Responsible for parsing LaTeX strings into an Abstract Syntax Tree (AST).
    -   **Key Components**:
        -   `LatexParser.kt` & `IncrementalLatexParser.kt`: Main entry points for parsing.
        -   `LatexTokenizer.kt`: Tokenizes the input string.
        -   `model/LatexNode.kt`: Defines the AST nodes (e.g., recursive structure for LaTeX commands).
        -   `visitor/LatexVisitor.kt`: Visitor pattern implementation for traversing the AST.
    -   Depends on `latex-base`.

3.  **latex-renderer** (`/latex-renderer`):
    -   Handles the visual rendering of the parsed LaTeX AST using Compose Multiplatform.
    -   **Key Components**:
        -   `Latex.kt`: Likely contains the main `@Composable` entry point for rendering LaTeX.
        -   `layout/LatexMeasurer.kt` & `NodeLayout.kt`: Handles the complex layout logic required for mathematical formulas (measuring, positioning).
        -   `model/RenderStyle.kt`: Defines styling attributes (fonts, sizes, colors).
    -   Depends on `latex-parser` and Compose UI libraries.

4.  **latex-sdk** (`/latex-sdk`):
    -   Aggregator module that bundles `latex-base`, `latex-parser`, and `latex-renderer`.
    -   Intended to be the primary dependency for consumers of the library.

5.  **latex-preview** (`/latex-preview`):
    -   Module for local previews, likely not part of the published SDK.

### Application Modules

1.  **composeApp** (`/composeApp`):
    -   Shared UI logic for the sample application using Compose Multiplatform.
    -   Contains platform-specific source sets (`androidMain`, `iosMain`, `jvmMain`, `wasmJsMain`, `jsMain`) and common code (`commonMain`).

2.  **androidapp** (`/androidapp`) & **iosApp** (`/iosApp`):
    -   Native entry points for Android and iOS applications, respectively.

### Architecture Patterns

-   **Multi-Platform**: The core logic (parsing, rendering) is pure Kotlin/Compose Multiplatform, allowing it to run on JVM, Android, iOS, and Web.
-   **Pipeline Processing**: The rendering process follows a pipeline: `String -> Tokenizer -> Parser -> AST -> Renderer (Compose)`.
-   **Visitor Pattern**: Used in `latex-parser` to separate algorithms (like printing or traversing) from the object structure (AST).
