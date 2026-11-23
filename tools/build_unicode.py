#!/usr/bin/env python3
import urllib.request
from pathlib import Path

URL = "https://www.unicode.org/Public/UCD/latest/ucd/UnicodeData.txt"
out = Path("app/src/main/assets/unicode.tsv")
out.parent.mkdir(parents=True, exist_ok=True)
src = Path("UnicodeData.txt")
urllib.request.urlretrieve(URL, src)

# Exclude unified Han ideographs and compatibility ideographs, including extensions.
HAN = [(0x3400,0x4DBF),(0x4E00,0x9FFF),(0xF900,0xFAFF),(0x20000,0x2FA1F),(0x30000,0x323AF)]
def is_han(cp): return any(a <= cp <= b for a,b in HAN)

rows = []
pending = None
for line in src.read_text(encoding="utf-8").splitlines():
    f = line.split(";")
    cp, name, category = int(f[0],16), f[1], f[2]
    if name.endswith(", First>"):
        pending = (cp, name, category); continue
    if name.endswith(", Last>") and pending:
        start, first_name, first_cat = pending
        base = first_name[1:-len(", First>")]
        for n in range(start, cp+1):
            if not is_han(n): rows.append((n, f"<{base}>", first_cat))
        pending = None; continue
    if name.startswith("<") or not name or is_han(cp): continue
    rows.append((cp, name, category))

with out.open("w", encoding="utf-8") as f:
    for cp, name, cat in rows: f.write(f"{cp:X}\t{name}\t{cat}\n")
print(f"Wrote {len(rows)} Unicode entries to {out}")
