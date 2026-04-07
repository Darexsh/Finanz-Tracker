# Shared Modules

Shared modules intended for both desktop and mobile apps.

## Goal
- Implement core business logic once
- Keep validation and financial rules consistent across platforms

## Structure
- `domain/`: business/domain logic
- `utils/`: generic helpers

## Current Status
- Folder scaffolding is in place
- Main logic still lives in desktop app and can be extracted incrementally
