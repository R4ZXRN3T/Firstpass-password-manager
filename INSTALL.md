# Installation

This document explains how to get Firstpass running on your machine (recommended downloads, portable JAR and
building from source). The project requires JDK 25+ for building and for creating the bundled runtime images.

## Downloads (recommended):

- Releases, packages and installers: https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/latest

#### Common packages you will find in Releases:

- Windows: installer EXE (Firstpass-\<version\>-setup.exe)
- Zips for all platforms (named like `Firstpass-<version>-<OS>-<arch>.zip`) containing a platform-specific launcher and
  bundled JRE runtime.
- Portable: a self-contained JAR (named like `Firstpass-<version>-portable.jar`) that includes assets

### Windows (Installer)

1. Download `Firstpass-<version>-setup.exe` from the latest release.
2. Run the installer and follow the wizard.
3. Launch Firstpass from the Start Menu or desktop shortcut.

### Pre-built packaged artifacts

1. Download the ZIP for your OS/architecture from Releases.
2. Extract the archive.
3. Run the platform launcher in the archive (`Firstpass.exe` / `firstpass`).

### Portable JAR (All platforms)

1. Ensure you have a Java runtime installed (JRE/JDK 25+).
2. Download the portable JAR from Releases (e.g. `Firstpass-2.1.3-portable.jar`).
3. Run the JAR with double-click or via command line:

```bash
java -jar Firstpass-<version>-portable.jar
```

## Build from source

##### Requirements: JDK 25+, Gradle (you can use the included wrapper).

### Available Gradle tasks:

- `./gradlew run` – runs the app with the development configuration (only recommended for development).
- `./gradlew jar` – builds a JAR without assets (external assets folder required) and the distribution type `other` (
  auto update disabled).
- `./gradlew portable` – builds a self-contained portable JAR with embedded assets and the distribution type
  `portable` (auto update enabled, downloads portable JAR).
- `./gradlew copyJarToFinal` – copies the JAR from `build/libs` to `final/Firstpass.jar` for easier access.
- `./gradlew copyPortableJarToFinal` – copies the portable JAR from `build/libs` to
  `final/Firstpass-<verison>-portable.jar` for easier access.
- `./gradlew copyJarsToFinal` – runs both copy tasks.
- `./gradlew makeJlinkPackage` – builds platform-specific (MacOS excluded) runtime images with Jlink (
  requires [Launch4j](https://launch4j.sourceforge.net/) on Windows).
- `./gradlew makeMacApp` - builds a MacOS .app bundle (use instead of `makeJlinkPackage`).
- `./gradlew makeWindowsInstaller` – builds a Windows installer EXE (
  requires [Inno Setup](https://jrsoftware.org/isinfo.php) and [Launch4j](https://launch4j.sourceforge.net/) installed).

### Updating

- Use the in-app update flow when available. The updater behaves differently for installer vs portable distributions:
	- Installer: downloads the new `.exe` and launches the installer.
	- Portable: downloads the new JAR and replaces the old one on next start.
	- Packaged: Replaces the whole folder.

### Uninstall

- Installer version: uninstall via the OS (e.g. Windows "Apps & Features").
- Portable version: remove the JAR and data files (see Data Files below) to fully remove the app and data.
- Packaged version: remove the folder containing the app (see Data Files below).

#### Data files

Firstpass stores two files: the encrypted vault and the configuration.

Installed (non-portable) locations

| Platform | Vault (`accounts.vault`)                                 | Config (`config.json`)                                |
|----------|----------------------------------------------------------|-------------------------------------------------------|
| Windows  | `%USERPROFILE%\AppData\Roaming\Firstpass\accounts.vault` | `%USERPROFILE%\AppData\Roaming\Firstpass\config.json` |
| macOS    | `~/Library/Application Support/Firstpass/accounts.vault` | `~/Library/Application Support/Firstpass/config.json` |
| Linux    | `~/.local/share/Firstpass/accounts.vault`                | `~/.config/Firstpass/config.json`                     |

##### Portable version

Both files are created next to the portable JAR in the same directory:

- `accounts.vault` – encrypted account vault (AES-256-GCM)
- `config.json` – application settings and Argon2id password hash

Delete both files to fully reset the application (THIS ERASES ALL DATA).
