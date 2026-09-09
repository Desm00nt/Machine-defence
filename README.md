# Machine Defence

Minecraft Forge 1.19.2 / Forge 43.4.0.

Vehicle tower-defense mod: build a machine, drive into an endless forest corridor, survive enemy waves, and upgrade with gold and DNA.

## Current playable prototype

- custom flat `machinedefence:machine_defence` dimension;
- 25x25 starter platform with three fog-glass walls;
- corridor foundation and entrance;
- registered Machine Frame, Machine Wheel, Machine Engine, and Machine Turret blocks;
- `/machinedefence enter`, `/machinedefence start`, `/machinedefence stop`, and `/machinedefence status` commands;
- server-authoritative prototype vehicle run with top-down pitch, distance, waves, zombies, gold nuggets, and chance-based DNA rewards;
- death returns the player to the starter platform while keeping rewards.

## Test commands

```text
/machinedefence enter
/machinedefence start
/machinedefence status
/machinedefence stop
```

The next pass connects the registered blocks to an actual assembled vehicle, turret targeting, shop UI, and the camera/movement client layer.

## Build

Requires Java 17 and Gradle 8+.

```bash
gradle build
```
