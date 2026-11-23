UniKey
======

Android Unicode keyboard.

Features
--------
- Search Unicode characters by official name or code point.
- Character categories and favorites (long-press a character).
- Android Input Method Service.

Exclusion policy
----------------
All Han ideographs are excluded, including unified and compatibility ideographs and their extension ranges. This removes Chinese Han characters and Japanese kanji. Kana, Hangul, Latin, Cyrillic, Arabic, mathematical symbols, emoji, and other non-Han Unicode characters remain when named in UnicodeData.

GitHub Actions downloads the current Unicode Character Database, generates the character list, builds a debug APK, and uploads it as an artifact.
