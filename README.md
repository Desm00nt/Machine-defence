# Machine Defence

Minecraft Forge 1.19.2 / Forge 43.4.0.

A vehicle-based tower-defense mod: build a machine, drive into an endless forest corridor, survive enemy waves, and upgrade with gold and DNA.

## Current milestone

The first playable foundation is in progress. The repository currently contains:

- Forge project bootstrap;
- a custom flat `machinedefence:machine_defence` dimension;
- `/machinedefence enter` for testing the dimension in-game;
- GitHub Actions build workflow.

## Build

Requires Java 17 and Gradle 8+.

```bash
gradle build
```

## Planned MVP order

1. Start platform and corridor boundary.
2. Build-zone blocks and machine assembly.
3. Controllable vehicle and top-down camera.
4. First turret and enemy wave.
5. Damage, rewards, death, and return to base.
