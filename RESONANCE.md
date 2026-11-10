# Resonance 1.0 — Fabric 1.21.11

Source release. Compilation and in-game testing are NOT verified: dependency downloads were blocked by DNS in the authoring environment.

## GitHub build

Replace matching files in your existing repository with this folder's contents. Put build.gradle, settings.gradle, src and .github at the repository root. Use the included .github/workflows/build.yml, which pins Java 21 and Gradle 9.2.1. Do not delete unrelated repository files.

After Actions succeeds, download the artifact and replace your old Sound Detector JAR with sound-detector-1.0.0.jar in the same Minecraft instance's mods folder. Keep Fabric API for 1.21.11 installed.

## Controls

- /sounddetector opens the charcoal-and-gold settings panel.
- /sounddetector toggle switches detection on/off.
- /sounddetector clear clears history.
- Escape or Done closes the menu. Keyboard navigation uses standard Minecraft buttons.

Settings save in config/resonance.json: enable, block ESP, signal feed, muted sounds, ambient sounds, range, lifetime and three accent palettes (champagne, jade, iris).

## Behaviour

The sound-engine hook captures positional sound attempts before playback volume and distance checks. Muted sounds can therefore be detected when the client receives them and they reach this hook. The mod cannot discover sounds never sent by a server or discarded earlier in the client. Music, records and relative UI sounds are excluded. Ambient is off by default. Turning muted capture off filters zero-volume settings, not every perceptually inaudible event.

Each marker outlines the unit block containing the event position, including air blocks. A sound position does not prove a block type or a player's identity or current position. Moving sounds are recorded at initial playback. Repeated events in the same block refresh its lifetime. At most 32 markers are retained; the feed shows five. World changes clear history.

The twelve block edges are projected using captured world-render matrices and drawn through walls with soft pixel coverage and time-based fade-in/fade-out. This CPU-side overlay needs profiling in busy scenes; many large nearby boxes can reduce FPS. No compatibility claim is made for shader packs or alternate rendering mods until tested.

## Required validation

1. Build on GitHub and launch first with just Fabric API and this mod.
2. Open the menu, change settings and restart to verify persistence.
3. In a command-enabled world, run /playsound minecraft:block.anvil.land master @s ~5 ~ ~ 1 1.
4. Repeat with master volume muted and muted capture enabled.
5. Walk around the sound position, including behind a wall, and check alignment and fade-out.
6. Test FOV changes, third person, resource reload, window resizing and world switching.
7. Check your full mod pack and a busy sound area. Lower GUI scale if the menu does not fit a very small window.

## API references

https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/client/sound/SoundSystem.html

https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/client/render/WorldRenderer.html
