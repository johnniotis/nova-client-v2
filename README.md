# Nova Client v2 (Fabric, Minecraft 26.2)

Modules: Freecam, ESP. Menu: Right Shift.

## Controls
- Right Shift: menu (click a module to toggle it, click settings to change them)
- F6: Freecam. Mouse turns the camera, WASD flies where you look, Space/Shift = up/down, Sprint = 3x speed
- G: ESP (outlines players/hostiles/mobs/items through walls; pick which in the menu)
- Rebind in Options > Controls > Key Binds > Nova Client

## Build without installing anything: GitHub Actions
1. Create a free GitHub repo, upload everything in this folder (including the hidden .github folder).
2. Open the Actions tab, wait for the green check, download the "novaclient-jar" artifact.
3. Put novaclient-0.2.0.jar in your mods folder with Fabric API, launch the fabric-loader 26.2 profile.

## Build locally
JDK 25 + the Gradle wrapper from https://fabricmc.net/develop/template/ (pick 26.2), then `./gradlew build`.
The jar is build/libs/novaclient-0.2.0.jar (not the -sources one).
