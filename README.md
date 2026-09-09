# Machine Defence

Minecraft Forge 1.19.2 / Forge 43.4.0.

A vehicle tower-defense prototype: build a machine kit, enter a flat arena, survive waves in a forest corridor, and upgrade with gold and DNA.

## Current MVP prototype

- custom flat `machinedefence:machine_defence` dimension;
- 25x25 starter platform with three fog-glass walls;
- corridor foundation and entrance;
- Machine Frame, Machine Wheel, Machine Engine, and Machine Turret blocks with models and English/Russian names;
- starter machine kit through `/machinedefence build`;
- server-authoritative run with top-down camera angle and corridor movement;
- escalating zombie waves and periodic husk bosses;
- turret auto-targeting during a run;
- gold nuggets from kills and chance-based persistent DNA rewards;
- turret shop for 10 gold nuggets;
- death returns the player to the starter platform while keeping rewards;
- Gradle 8.8 installed explicitly in GitHub Actions.

## Test commands

```text
/machinedefence enter
/machinedefence build
/machinedefence start
/machinedefence status
/machinedefence shop
/machinedefence install-turret
/machinedefence stop
```

The remaining integration work is replacing the server-authoritative prototype vehicle with a true assembled moving structure and adding richer client controls, UI, and block damage simulation.

## Build

Requires Java 17 and Gradle 8+.

```bash
gradle build
```
