# AGENTS.md

## General

- Follow the style and conventions of the surrounding code.
- Prefer modern and idiomatic Java.
- Do not introduce additional dependencies unless explicitly requested.
- Keep changes focused and minimal.

## Testing

- All code under `org.appland.settlers.model` must maintain **100% test coverage**.
- When writing tests, verify behavior through the public game API and observable game outcomes.
- Treat tests as black-box tests whenever possible.
- Do not inspect or assert against internal implementation details when behavior can be observed through normal game interactions.

## Game Simulation

- Do **not** use `Utils.constructHouse()`, `Utils.constructHouses()`, `Utils.occupyBuilding()`, `Utils.occupyBuildings()`, `occupyMilitaryBuilding`, or `Utils.assignBuilder()` to force game state.
- Instead, trigger actions through normal game mechanics and wait for the game to produce the expected state. Connect the new house to a headquarters or storehouse and wait for it to get built and occupied.
- Prefer validating that game events, commands, state transitions, and player-visible outcomes occur as expected.

## Code Style

- Use records, switch expressions, streams, and other modern Java language features where appropriate.
- Initialize fields where they are declared when practical.
- Prefer clear and readable code over clever solutions.
- Avoid unnecessary extraction of methods; preserve the existing flow of the surrounding code unless refactoring is clearly beneficial.

## Comments

- Public methods should have short Javadoc comments.
- Use `//` for one-line comments.
- Leave an empty line before adding a comment.
