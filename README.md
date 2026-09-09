# Machine Defence

Minecraft Forge 1.19.2 / Forge 43.4.0.

A vehicle tower-defense prototype: build a machine kit, enter a flat arena, survive waves in a forest corridor, and upgrade with gold and DNA.

## Current MVP prototype

- custom flat `machinedefence:machine_defence` dimension;
- 25x25 starter platform with three fog-glass walls;
- decorated corridor with forest trees and entrance;
- Machine Frame, Machine Wheel, Machine Engine, and Machine Turret blocks with models and English/Russian names;
- starter machine kit physically placed in the build zone through `/machinedefence build`;
- server-authoritative run with top-down camera angle and corridor movement;
- escalating zombie waves and periodic high-health husk bosses;
- turret auto-targeting during a run;
- simulated machine armor that is damaged when enemies reach the vehicle;
- gold nuggets from kills and chance-based persistent DNA rewards;
- extra boss rewards: 20 gold nuggets and 5 DNA;
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

The remaining polish pass is the true assembled moving-structure entity and richer client UI/input. The complete server-side gameplay loop is now present as the MVP prototype.

## Build

Requires Java 17 and Gradle 8+.

```bash
gradle build
```
