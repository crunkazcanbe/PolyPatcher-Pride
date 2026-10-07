# Changelog

## 1.10.4-pride.2 - 2026-10-07

### Fixed
- **BufferBuilder memory creep crash**: the buffer position was never reset on `begin()`, so it grew every frame until the game crashed with "newPosition > limit" after a few minutes on menus. It now resets on every begin and never steps past the end.
- **No sound on Cleanroom**: PolyPatcher no longer touches the master volume at startup (it was muting all sound until the window lost and regained focus); only a saved volume is restored.
- **Audio device switcher** rewritten for Cleanroom / LWJGL 3: devices are listed through OpenAL's full device enumeration, a chosen device is applied with `ALC_SOFT_reopen_device` on the existing device (keeps Cleanroom's HRTF choice), and the button can cycle back to "Default Sound Device". LWJGL 2 keeps the original behaviour.
- **Startup time toast** showed an epoch-sized number on Cleanroom; it now falls back to the JVM start time. The toast is shorter and **off by default** (it covered other mods' menu buttons); the time is always written to the log.
- Class writer no longer hits a bare `NullPointerException` that could leave a half-written class; it walks `ClassInfo`, then the deobfuscated class resources, then `Object`.
- The coremod no longer fails on Java 9+ (blocked `setAccessible` on `Shutdown.halt`), and an LWJGL unlock failure can no longer throw out of the tweaker.
- Font renderer: the string width cache is fully thread-safe (re-entrant lookups threw `ConcurrentModificationException`), and strings with non-vanilla colour codes (for example EMI custom colours) fall back to vanilla drawing.
- Screenshots: only the unnamed F2 screenshot is taken over (mods' named screenshots stay vanilla), the game-directory argument is respected, and chat, preview and clipboard work run on the client thread.
- Worker threads are daemon threads and never keep the game process alive on exit.
- OptiFine version lookups are skipped unless OptiFine is installed.

### Changed
- **Windowed Fullscreen** uses GLFW borderless mode on LWJGL 3 and defers to Cleanroom's own borderless-fullscreen option.
- The Linux keyboard-layout workaround is skipped on LWJGL 3 (GLFW keys are already physical).
- Performance features made mod-friendly:
  - Chunk optimisation no longer overwrites `getBlockState(BlockPos)` on 1.12, which bypassed mods that hook `getBlockState(int, int, int)` (for example extended world depth).
  - The entity optimisation applies to the client world only and skips only the entity box search, so Forge's `GetCollisionBoxesEvent` still fires.
  - The item display-name cache is only used for stacks without NBT and with unchanged item/damage (names went stale when mods changed them in place).
  - Smart Fullbright also turns Fullbright off with Celeritas, and the lightmap freeze follows the Fullbright state.
  - ResourceLocation de-duplication is skipped when StellarCore already does it.

### Added
- New **"Open PolyPatcher Settings"** key binding (Controls > Patcher, unbound by default), because OneConfig's own hotkey does not fire on Cleanroom.

