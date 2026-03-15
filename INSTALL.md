# Installation

## Download (Recommended)

Grab the latest release here:

- Windows Installer (
  EXE): https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/latest/download/Firstpass_setup.exe
- Portable JAR (all
  platforms): https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/latest/download/Firstpass_portable.jar

## Windows (Installer)

1. Download the `Firstpass_setup.exe` file.
2. Run it and follow the wizard.
3. Launch "Firstpass" from the Start Menu or desktop shortcut.

## Pre-Built binaries (Windows / Linux / MacOS)

1. Download the zip from the releases with the fitting operating system and architecture for your setup
2. Extract the zip
3. Run `Firstpass.exe` (just `Firstpass` on Linux and MacOS)

You can copy the folder to any location on your computer

## Portable JAR (All platforms)

1. Install a Java Runtime (JDK/JRE 25+ required!).
2. Download `Firstpass_portable.jar`.
3. Double click the jar

or

4. run:

```
java -jar Firstpass_portable.jar
```

(on Linux you may need to `chmod +x Firstpass_portable.jar` first)

## Build From Source

Requirements: JDK 25, Gradle (or use the included `gradlew` wrapper).

### JARs only (quickest)

```
./gradlew fatJar portableJar
```

Artifacts are written to `build/libs/`:

- `Firstpass.jar` (requires external `assets/` folder next to it)
- `Firstpass_portable.jar` (self-contained, assets bundled)

### Current OS package

```
./gradlew packageCurrentOs
```

Produces `Firstpass.jar`, `Firstpass_portable.jar`, and a platform zip in `final/construo/`
with the bundled JRE for the OS you are building on. (Note that you still need the assets folder in the same directory
when running the normal Firstpass.jar)

### All platforms + Windows installer (release builds)

Requirements: additionally Inno Setup (Windows only, for the `.exe` installer).

```
./gradlew packageAll
```

Builds all platform zips into `final/construo/` and, on Windows with Inno Setup on `PATH`,
also creates `final/Firstpass_setup.exe`.

## Updating

Use the in-app update button when shown, or re-download the latest release and replace the old file.

## Uninstall

- Installer version: Use Windows "Apps & Features".
- Portable version: Delete the JAR (optionally remove the data files listed below if you want to erase all data).

## Data Files

Firstpass stores two files: the encrypted vault and the configuration.

### Installed (non-portable) version

| Platform | Vault (`accounts.vault`)                                 | Config (`config.json`)                                |
|----------|----------------------------------------------------------|-------------------------------------------------------|
| Windows  | `%USERPROFILE%\AppData\Roaming\Firstpass\accounts.vault` | `%USERPROFILE%\AppData\Roaming\Firstpass\config.json` |
| macOS    | `~/Library/Application Support/Firstpass/accounts.vault` | `~/Library/Application Support/Firstpass/config.json` |
| Linux    | `~/.local/share/Firstpass/accounts.vault`                | `~/.config/Firstpass/config.json`                     |

### Portable version

Both files are created **next to the JAR** in the same directory:

- `accounts.vault` – encrypted account vault
- `config.json` – settings + Argon2id password hash

Delete both to fully reset the application.
