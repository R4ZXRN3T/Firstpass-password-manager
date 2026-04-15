# Usage

## Quick start

1. Launch the app. On first start a default `config.json` is created.
2. (Optional) Set a master password in Settings → Password → Set Password.
3. Add accounts with the bottom toolbar Add button.
4. Save (File → Save) or Save & Quit to persist changes.

## Managing accounts

- `Add`: Bottom toolbar "Add" — fill Provider, Username, Password, URL and Comment, then OK.
- `Edit`: Select a row and click "Edit" or double-click the row.
- `Remove`: Select a row and click "Remove".
- `Undo deletion`: "Undo" on the bottom toolbar restores the last deleted entry (stack-based; edits are not undoable).

## Search / filter

Use the search bar at the top and select the scope dropdown to restrict where the query is matched:

| Scope    | Fields searched |
|----------|-----------------|
| All      | All fields      |
| Provider | Provider field  |
| Username | Username field  |
| Password | Password field  |
| URL      | URL field       |
| Comment  | Comment field   |

## Password generator

Open `Generator` from the bottom toolbar. Options:

- **Length**: 1–64 characters
- **Character sets**: Uppercase, Lowercase, Numbers, Special Characters (all enabled by default)

Click `Generate!` to produce a password and use the copy icon to place it on the clipboard (a short toast confirms the
copy).

## Export / import

- File → Export: export formats TXT, CSV, JSON, XML (Change with the file type filter in the file picker).
- File → Import: supported imports are `.txt`, `.csv`, `.json`, `.xml` — you will be asked to Merge (append) or Replace
  the current list.

## Themes

Choose Settings → Theme. Available options (index only relevant for config.json):

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

Applying a theme will prompt for restart to take effect.

## Updates

If "Check for updates on startup" is enabled (Settings), Firstpass checks for new versions and shows a blue "New version
available!" indicator in the menu bar when an update is found. Click it or use Settings → Check for Updates to run the
updater.

- **Installer distribution**: the updater downloads the new installer and runs it.
- **Portable distribution**: the updater downloads a new JAR and will replace the running JAR.
- **Packaged distribution**: the updater downloads a new ZIP and replaces the whole folder.

## Security basics

- **Vault encryption**: account data is stored in `accounts.vault`, encrypted with `AES-256-GCM`. The AES key is derived
  from the master password using `PBKDF2-HMAC-SHA256` (210000 iterations).
- **Master password hashing**: the master password is hashed with `Argon2id` and the hash is stored in `config.json`.
- **No password set**: an empty-password key is used; set a master password to protect your data at rest. Removing the
  master password re-encrypts the vault using the empty-password key.

## Reset / data location

| Platform | Vault                                                    | Config                                                |
|----------|----------------------------------------------------------|-------------------------------------------------------|
| Windows  | `%USERPROFILE%\AppData\Roaming\Firstpass\accounts.vault` | `%USERPROFILE%\AppData\Roaming\Firstpass\config.json` |
| macOS    | `~/Library/Application Support/Firstpass/accounts.vault` | `~/Library/Application Support/Firstpass/config.json` |
| Linux    | `~/.local/share/Firstpass/accounts.vault`                | `~/.config/Firstpass/config.json`                     |
| Portable | `accounts.vault` (next to the JAR)                       | `config.json` (next to the JAR)                       |

To fully reset the app delete both files (this erases all account data). From inside the app you can also use Settings →
Delete Everything.

## Troubleshooting

| Problem               | Hint                                                                                                                                                          |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Wrong password loop   | Passwords are case-sensitive. If you cannot unlock the vault and have no backup, the only recovery is removing the vault and config (You will lose all data). |
| Can't edit            | Make sure a row is selected or double-click the entry to edit.                                                                                                |
| Undo disabled         | No deletions have been performed yet.                                                                                                                         |
| Vault load error      | The vault file may be corrupted; the app offers to delete the vault on startup if it cannot decrypt it.                                                       |
| Update button missing | "Check for updates on startup" may be disabled in Settings. Alternatively update manually with the `Check for updates` button.                                |

## Disclaimer

No formal security audit — use at your own risk.
