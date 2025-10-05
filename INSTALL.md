# Installation

## Download (Recommended)

Grab the latest release here:

- Windows Installer (
  MSI): https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/latest/download/Firstpass_setup.msi
- Portable JAR (all
  platforms): https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/latest/download/Firstpass_portable.jar

## Windows (Installer)

1. Download the `.msi` file.
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
mvn clean package
```

Artifacts copied to project root after build:

- `Firstpass.jar` (requires external `assets/` folder)
- `Firstpass_portable.jar` (self-contained)

## Updating

Use the in‑app update button when shown, or re-download latest release and replace the old file.

## Uninstall

- Installer version: Use Windows "Apps & Features".
- Portable version: Delete the folder (optionally remove `accounts.txt` and `config.json` if you want to erase data).

## Data Files

- `accounts.txt` – stored accounts (encrypted if a password is set).
- `config.json` – settings + password hash/salt.
  Delete both to fully reset the application.

