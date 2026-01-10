# Alchemy – Canned Beverages Pack

A small, Minecraft-friendly content pack for the **Alchemy** Fabric mod that adds an `Aluminum Can` container plus 8 fantasy-styled canned beers, each with flavor text, ingredients, brew time, and a gameplay effect.

## Included content

### Container

- **Aluminum Can** (`alchemy:aluminum_can`) – lightweight container used for brewed drinks.

### Beers in cans

- Coppercap Lager – Haste (5 min).
- Frostmarsh Pils – Speed (3 min).
- Emberhold Amber Ale – Fire Resistance (2 min).
- Sunvale Golden Pale – Haste (2 min).
- Stormwake Session IPA – Dolphin’s Grace (3 min).
- Blackvault Stout – Resistance (3 min).
- Thornveil Herbal Ale – Regeneration (45 sec).
- Ropesend Dockside Brew – Luck (2 min).

## How it’s meant to work

- Drinks are defined in a data-driven way (language keys + JSON-driven drink definitions), fitting the mod’s “schema-driven/modular” direction.
- Brewing is performed using a **Fermenting Barrel**-style workflow (place ingredients, wait for brew time, collect output).

## Crafting & brewing

### Craft Aluminum Can

- **Aluminum Can**: “Crafted using Aluminum Ingots at a Crafting Table.”

### Brew canned beers

Each beer includes an “ingredients” line and a “brew time” line intended for display in tooltips/lore.
General flow:

1. Gather the listed ingredients (e.g., Barley, Hops, etc.).
2. Place them into a Fermenting Barrel.
3. Wait for the brew time to complete, then collect the finished can.

## Localization keys

This pack expects `assets/alchemy/lang/en_us.json` entries similar to:

- `item.alchemy.<id>` (name)
- `item.alchemy.<id>.desc` (short description)
- `item.alchemy.<id>.tooltip`, `.ingredients`, `.brew_time`, `.rarity`, `.container`, etc. (extra UI text).

## Art guidelines (optional)

For best vanilla-style results:

- 16×16 pixel art, consistent palette, simple label icons, and readable silhouettes.
- Keep “Minecraft-style subtle, suggestive, never explicit.”

## Contributing

Suggested workflow:

- Add/adjust drink entries in your data definitions.
- Add a matching 16×16 texture.
- Run your generator/validation pipeline and open a PR.

## Notes / warnings

In-game warnings (Nausea/Slowness/etc.) are intentionally included for flavor and balance.
Real-world alcohol consumption is not encouraged; this is fantasy game content.
