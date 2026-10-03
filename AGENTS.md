# AGENTS — Agent Customization (compact)

Purpose: provide minimal, actionable guidance for AI coding agents to build, run, and test this project.

Quick commands

- **Build:** `./mvnw package` (Unix) or `mvnw.cmd package` (Windows)
- **Run (dev):** `./mvnw spring-boot:run`
- **Run (jar):** `java -jar target/*.jar`
- **Tests:** `./mvnw test`

Environment

- **Java:** 21 (project `pom.xml` property `java.version`)
- **Build tool:** Maven (wrapper present: `mvnw`, `mvnw.cmd`)

Project entrypoints & structure (compact)

- **Main class:** `com.careerconnect.CareerConnectApplication` (src/main/java)
- **Resources:** `src/main/resources` (application.properties, static/, templates/)
- **Tests:** `src/test/java`

Notable conventions

- Uses Lombok (annotation processing configured in `pom.xml`).
- Runtime DB: PostgreSQL (dependency present, not required for unit tests).
- Spring Boot DevTools included for local dev reloads.

Docs and links

- Short local help: [HELP.md](HELP.md)

Notes for agents

- Prefer using the Maven wrapper (`mvnw`/`mvnw.cmd`) when running commands.
- Do not copy or duplicate docs from existing files; link to them instead.
- Keep changes minimal and focused; update `AGENTS.md` if new developer setup steps appear.

Suggested next agent customizations

- Create a small `dev-scripts` skill to run common sequences (`clean package`, `test`, `run`).
- Add a `tests` hook to run fast unit tests and report failures concisely.
