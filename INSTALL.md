# Installation

## Download (Recommended)

Grab the latest release here:

- Windows Installer (EXE): https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/latest/download/Firstpass_setup.exe
- Portable JAR (all platforms): https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/latest/download/Firstpass_portable.jar

## Windows (Installer)

1. Download the `.exe` file.
2. Run it and follow the wizard.
3. Launch "Firstpass" from the Start Menu or desktop shortcut.

## Portable JAR (Windows / Linux / macOS)

1. Install a Java Runtime (JDK/JRE 25+ required!).
2. Download `Firstpass_portable.jar`.
3. Double click the jar

or

4. run:

```
java -jar Firstpass_portable.jar
```

(on linux you may need to `chmod +x Firstpass_portable.jar` first)

## Build From Source

Requirements: JDK 25, Maven.

```
mvn clean install clean
```

Artifacts copied to project root after build:

- `Firstpass.jar` (requires external `assets/` folder)
- `Firstpass_portable.jar` (self-contained)

### Creating an installer (Windows Only)

Requirements: Inno Setup, Launch4j

1. Execute ```mvn package -P jlink``` in the root dir
2. Unzip the archive, so that you have a `jre` folder
3. Open **Launch4j**
4. Load `installer files/launch4j_setup.xml`
5. Click on the ⚙️ button to create an `.exe`
6. Launch Inno Setup and load `installer files/Firstpass.iss`
7. Click the compile button
8. Done! Your `.exe` installer should be available under `installer files/Firstpass_setup.exe`

## Updating

Use the in‑app update button when shown, or re-download latest release and replace the old file.

## Uninstall

- Installer version: Use Windows "Apps & Features".
- Portable version: Delete the folder (optionally remove `accounts.txt` and `config.json` if you want to erase data).

## Data Files

- `accounts.txt` – stored accounts (encrypted if a password is set).
- `config.json` – settings + password hash/salt.
  Delete both to fully reset the application.
