# Firstpass Password Manager

A lightweight, Java-based, cross-platform (portable) password manager focused on simplicity, offline use, and transparency.

> Current version: 2.0.9

## Table of Contents
- [Highlights](#highlights)
- [Feature Overview](#feature-overview)
- [How It Works (Architecture)](#how-it-works-architecture)
- [Security Model](#security-model)
  - [Master Password & Verification](#master-password--verification)
  - [Account Encryption](#account-encryption)
  - [What Is NOT Covered](#what-is-not-covered)
- [File Formats](#file-formats)
- [Installation](#installation)
  - [Windows Installer](#windows-installer)
  - [Portable JAR](#portable-jar)
  - [Linux / macOS Notes](#linux--macos-notes)
- [Build From Source](#build-from-source)
  - [Prerequisites](#prerequisites)
  - [Build Steps](#build-steps)
  - [Produced Artifacts](#produced-artifacts)
- [Usage](#usage)
  - [First Run & Password Prompt](#first-run--password-prompt)
  - [Managing Accounts](#managing-accounts)
  - [Searching & Filtering](#searching--filtering)
  - [Password Generator](#password-generator)
  - [Undo Deletions](#undo-deletions)
  - [Themes (Look & Feel)](#themes-look--feel)
  - [Updating](#updating)
- [Configuration Reference](#configuration-reference)
- [Troubleshooting](#troubleshooting)
- [Roadmap / Ideas](#roadmap--ideas)
- [Contributing](#contributing)
- [License](#license)
- [Screenshots](#screenshots)

## Highlights
- Fully offline – your data never leaves your machine.
- Simple, transparent plaintext storage format (with optional encryption per field).
- Fast bulk encryption/decryption on load/save using Jasypt (StrongTextEncryptor).
- Configurable master password (can be removed if you just want a local plain list – not recommended).
- Multiple export/import formats: TXT, CSV, JSON, XML.
- Undo stack for deletions.
- Built-in random password generator (1–64 chars, selectable char classes).
- Theme support via FlatLaf + system/Metal options.
- Optional automatic update check (GitHub releases).
- Portable build variant bundles assets inside the JAR.

## Feature Overview
| Category | Features |
|----------|----------|
| Account Management | Add / Edit / Remove / Undo Delete, inline double‑click edit shortcut |
| Search | Real-time filtering across all fields or a specific column (Provider, Username, Password, URL, Comment) |
| Export / Import | TXT (native layout), CSV, JSON (provider → [fields]), XML (simple schema) with merge or replace import modes |
| Security | Per-field symmetric encryption with a user master password; salted SHA‑256 hash (config) for login verification |
| Themes | Flat Light / Flat Dark / Flat Mac Light / Flat Mac Dark / Flat IntelliJ / Flat Darcula / Swing Metal / System Default |
| Password Generation | Custom length (1–64), include/exclude uppercase, lowercase, numbers, special characters |
| Updates | Manual + optional startup check; in‑app downloader & self-replace (installer or portable) |
| Portability | Assets auto-detected (internal vs external folder) |

## How It Works (Architecture)
- Core UI built with Swing.
- `Firstpass` orchestrates lifecycle: config load → password check → account decrypt → UI init.
- Accounts stored in `accounts.txt`, 5 consecutive lines per record: Provider, Username, Password, URL, Comment.
- Table is a non-editable `JTable` wrapper (`AccountTable`); double-click a row to edit.
- Encryption/decryption happens in memory using the current master password just before save / immediately after load.
- Configuration (`config.json`) holds hashed password, salt, theme index, recent export/import directories, update preference.
- Update system queries GitHub Releases API and downloads either the MSI or portable JAR, then performs a self-replacement script.

## Security Model
### Master Password & Verification
- The master password itself is NOT stored directly.
- A random 16 char salt (`salt`) and a SHA‑256 hash of (password + salt) are stored in `config.json`.
- On startup, user input is hashed with the stored salt and compared.
- A new salt + hash pair are generated on every successful save (rotating salt improves resistance to certain offline attacks if config were extracted).

### Account Encryption
- Each non-empty field of each account is encrypted individually using Jasypt `StrongTextEncryptor` with the master password.
- Encryption occurs only on disk (i.e. data in memory after login is plaintext for usability).
- If you remove the master password (supported), future saves will store the fields unencrypted.

### What Is NOT Covered
- No memory scrubbing / secure string handling.
- No tamper detection / integrity MAC.
- No multi-user separation or cloud sync.
- Not audited; suitable for personal / educational use. Use at your own risk.

## File Formats
### Native (`accounts.txt`)
Sequential plaintext (or encrypted tokens) in groups of 5 lines.
```
Provider
Username
Password
URL
Comment
(repeat...)
```
### CSV
`Provider, Username, Password, URL, Comment`
### JSON
```json
{
  "providerName": ["username", "password", "url", "comment"],
  "...": ["...", "...", "...", "..."]
}
```
### XML
```xml
<accounts>
  <account>
    <provider>...</provider>
    <username>...</username>
    <password>...</password>
    <url>...</url>
    <comment>...</comment>
  </account>
</accounts>
```
Import Modes: Merge (append) or Replace (clear then import).

## Installation
### Windows Installer
1. Download the latest `.msi` from Releases.
2. Run installer (it places program + assets where chosen).
3. Launch “Firstpass”.

### Portable JAR
1. Download `Firstpass_portable.jar` from Releases.
2. (Optional) Put it in its own folder.
3. Run with: `java -jar Firstpass_portable.jar`.

### Linux / macOS Notes
- No native installer yet. Use the portable JAR.
- Ensure a compatible JDK (see below) is installed and on PATH.

## Build From Source
### Prerequisites
- JDK 25 (Project targets source/target 25 in `pom.xml`). Earlier JDKs will fail compilation.
- Maven 3.9+.

### Build Steps
```
mvn clean package
```
### Produced Artifacts
- `target/Firstpass.jar` (shaded dependencies, expects external `assets/` alongside unless using portable)
- `target/Firstpass_portable.jar` (includes assets via assembly descriptor)
- Both are also copied to project root after the `verify` phase (`Firstpass.jar`, `Firstpass_portable.jar`).

## Usage
### First Run & Password Prompt
- On first launch a default config is generated and the app restarts.
- If no password is set yet, accounts are loaded without decryption.
- To set a password: Settings → Change/Set Password.

### Managing Accounts
- Add: Bottom toolbar “Add”.
- Edit: Select + bottom “Edit” or double-click row.
- Remove: Select + “Remove”.
- Undo last deletion: “Undo” (stack-based, only deletions tracked).
- Save: File → Save (also run on “Save & Exit”).

### Searching & Filtering
- Search bar filters in real-time.
- Dropdown selects field scope (All / Name (Provider) / Username / Password / URL / Comment).

### Password Generator
- Open via “Generator” button.
- Choose: uppercase / lowercase / numbers / special characters / length.
- Copy result with the copy icon.

### Undo Deletions
- Each removal pushes a snapshot (with original index) to an undo stack.
- Only deletions are undoable (not edits or imports).

### Themes (Look & Feel)
Available options (index mapping in config):
0 Flat Light
1 Flat Dark
2 Flat Mac Light
3 Flat Mac Dark
4 Flat IntelliJ
5 Flat Darcula
6 Swing Metal
7 System Default (fallback/default branch else system)

Changes requiring restart will prompt confirmation.

### Updating
- If enabled (Settings), a background check runs at startup.
- When an update is found, a blue “New version available!” button appears.
- Clicking it downloads and self-applies the update (MSI or portable replacement) and restarts.

## Configuration Reference
`config.json` example:
```json
{
  "password": "<64 hex chars SHA-256 hash>",
  "salt": "<16 chars>",
  "lookAndFeel": "0-7",
  "export": "<last export directory>",
  "import": "<last import directory>",
  "checkForUpdates": "true|false"
}
```
Notes:
- Changing `salt` manually will invalidate the stored hash (you'll be treated as having no password if mismatch logic passes initial checks).
- Deleting `config.json` regenerates defaults and restarts the app.

## Troubleshooting
| Issue | Cause | Fix |
|-------|-------|-----|
| Stuck at password prompt | Wrong master password | Verify keyboard layout / caps; no reset option (delete all data to reset) |
| Garbled account fields after load | Wrong password used previously to encrypt | Restore backup or re-enter correct password |
| Update fails to replace file | File locked or permissions | Close other processes; re-run update manually from Releases |
| Theme not changing | Requires restart | Accept restart prompt after Apply |
| Build fails (unsupported target) | Older JDK in use | Install JDK 25 and ensure `java -version` reflects it |

## Roadmap / Ideas
- Optional auto-lock after inactivity
- Clipboard auto-clear
- Field masking / reveal toggle
- Integrity verification (HMAC)
- Encrypted export formats
- Multi-select delete/edit

(Feel free to open an issue to discuss or contribute.)

## Contributing
1. Fork repository
2. Create feature branch: `git checkout -b feature/my-feature`
3. Build & test locally (`mvn clean package`)
4. Submit a PR with a clear description

Please keep code style consistent and avoid introducing heavy dependencies.

## License
MIT License – see [LICENSE](LICENSE).

## Screenshots
(Same images retained as requested.)

![Screenshot 2025-03-19 200253](https://github.com/user-attachments/assets/352a382c-82bc-4a2b-867e-f1642fc742a5)
![Screenshot 2025-03-19 200219](https://github.com/user-attachments/assets/cab845fa-6d78-4bb6-a215-5c71ac2c2d65)
![Screenshot 2025-03-19 200324](https://github.com/user-attachments/assets/9d9bc1a0-f58a-4bb4-97fd-9790cc4b59ba)

---

> Disclaimer: This project has not undergone a professional security audit. Use it for personal / educational purposes at your own discretion.
