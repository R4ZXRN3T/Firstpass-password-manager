# Usage

## Quick Start

1. Launch the app. On first start a default config is created.
2. (Optional) Set a master password: Settings → Password → Change Password.
3. Click "Add" to create your first entry.
4. Click "Save" (File → Save) or exit with "Save & Exit" to persist changes.

## Managing Accounts

- **Add**: Bottom toolbar "Add" — fill in Provider, Username, Password, URL, Comment and click OK.
- **Edit**: Select a row and click "Edit", or double-click the row.
- **Remove**: Select a row and click "Remove".
- **Undo Deletion**: Click "Undo" in the bottom toolbar (stack-based; only deleted entries can be restored, not edits).

## Search / Filter

Use the search bar at the top; choose a scope from the dropdown for targeted filtering:

| Scope    | Searches in           |
|----------|-----------------------|
| All      | All fields            |
| Name     | Provider / name field |
| Username | Username field        |
| Password | Password field        |
| URL      | URL field             |
| Comment  | Comment field         |

## Password Generator

Click "Generator" in the bottom toolbar. Configure:

- **Length**: 1–64 characters (slider)
- **Character sets**: Uppercase, Lowercase, Numbers, Special Characters (all enabled by default)

Click "Generate!" to produce a password, then use the copy icon to copy it to the clipboard.

## Export / Import

**File → Export**: Choose TXT / CSV / JSON / XML and pick a save location.  
**File → Import**: Select a supported file (`.txt`, `.csv`, `.json`, `.xml`), then choose:

- **Merge** — append imported entries to the existing list.
- **Replace** — overwrite the existing list with the imported entries.

## Themes

Settings → Theme. Eight themes are available:

| # | Theme          |
|---|----------------|
| 0 | Flat Light     |
| 1 | Flat Dark      |
| 2 | Flat Mac Light |
| 3 | Flat Mac Dark  |
| 4 | Flat IntelliJ  |
| 5 | Flat Darcula   |
| 6 | Swing Metal    |
| 7 | System Default |

Theme changes require a restart (you'll be prompted when applying).

## Updates

If enabled (Settings → "Check for updates on startup"), a blue "New version available!" button appears in the menu bar
when an update exists. Click it — or use Settings → Check for Updates — to download and apply the update automatically.

- **Installer version**: downloads the new `.exe` installer and launches it.
- **Portable version**: downloads the new JAR alongside the current one and swaps them on next start.

## Security Basics

- **Vault encryption**: all account data is stored in `accounts.vault`, encrypted with **AES-256-GCM**.  
  The AES key is derived from the master password using **PBKDF2-HMAC-SHA256** (210 000 iterations).
- **Master password hashing**: the password itself is hashed with **Argon2id** and stored in `config.json`.
- **No password set**: the vault is still written using an empty-password key (AES-256-GCM with a blank passphrase).
  Set a real master password to fully protect the vault at rest.
- Removing the master password re-encrypts the vault with the blank-password key.

## Reset / Data Location

| Platform | Vault                                                    | Config                                                |
|----------|----------------------------------------------------------|-------------------------------------------------------|
| Windows  | `%USERPROFILE%\AppData\Roaming\Firstpass\accounts.vault` | `%USERPROFILE%\AppData\Roaming\Firstpass\config.json` |
| macOS    | `~/Library/Application Support/Firstpass/accounts.vault` | `~/Library/Application Support/Firstpass/config.json` |
| Linux    | `~/.local/share/Firstpass/accounts.vault`                | `~/.config/Firstpass/config.json`                     |
| Portable | `accounts.vault` (next to the JAR)                       | `config.json` (next to the JAR)                       |

**Delete both files to fully reset the app — THIS ERASES ALL DATA.**

You can also use Settings → Delete Everything from within the app.

## Troubleshooting

| Problem               | Hint                                                                                           |
|-----------------------|------------------------------------------------------------------------------------------------|
| Wrong password loop   | Password is case-sensitive; there is no recovery except deleting the vault and the config file |
| Can't edit            | Select the row first, or double-click it                                                       |
| Undo disabled         | No deletions in the undo stack yet                                                             |
| Vault load error      | Vault file may be corrupted; the app will offer to delete it on startup                        |
| Update button missing | "Check for updates on startup" may be disabled in Settings                                     |

## Disclaimer

No formal security audit. Use at your own risk.
