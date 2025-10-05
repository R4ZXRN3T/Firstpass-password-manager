# Usage

## Quick Start

1. Launch the app. On first start a default config is created.
2. (Optional) Set a master password: Settings -> Change/Set Password.
3. Click "Add" to create your first entry.
4. Click "Save" (File -> Save) or exit with "Save & Exit" to persist changes.

## Managing Accounts

- Add: Bottom toolbar "Add".
- Edit: Select a row and click "Edit" or double‑click the row.
- Remove: Select a row and click "Remove".
- Undo Deletion: Click "Undo" (only for the most recently deleted entries, stack-based).

## Search / Filter

Use the search bar at the top; choose a scope (All, Name, Username, Password, URL, Comment) from the dropdown for
targeted filtering.

## Password Generator

Click "Generator". Pick: length (1–64), include uppercase / lowercase / numbers / special chars. Copy using the copy
icon.

## Export / Import

File -> Export: Choose TXT / CSV / JSON / XML.
File -> Import: Select a supported file, then choose Merge (append) or Replace (overwrite existing list).

## Themes

Settings -> Theme. Choose a Look & Feel. Some changes require a restart (you'll be prompted).

## Updates

If enabled (Settings: "Check for updates on startup"), a blue "New version available!" button appears when an update
exists. Click to download & apply.

## Security Basics

- Master password: Protects data at rest. Hash + salt stored in `config.json`.
- Account fields are encrypted before saving and decrypted after loading (when a password is set).
- Removing the password stores future data unencrypted.

## Reset / Data Location

- `accounts.txt` : stored entries
- `config.json` : settings + password hash/salt
  Delete both to fully reset (THIS ERASES ALL DATA).

## Troubleshooting

| Problem                 | Hint                                                         |
|-------------------------|--------------------------------------------------------------|
| Wrong password loop     | Password is case sensitive; no recovery except deleting data |
| Can't edit              | Select the row first or double‑click                         |
| Undo disabled           | No deletions in the stack yet                                |
| Garbled text after load | Likely used wrong password previously                        |

## Disclaimer

No formal security audit. Use at your own risk.

