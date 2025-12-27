# Color Palette

Used for `cc` (cap color) and `lc` (legend color) attributes.

Colors 0-31 are a standardized set of opaque colors. Colors 32-63 are a corresponding set of translucent colors.

![](/docs/PopArt-6.svg)

| Index | Color                        | Index | Color                         |
| ----- | ---------------------------- | ----- | ----------------------------- |
| 0     | Default                      | 32    | Transparent                   |
| 1     | Black                        | 33    | Translucent Black             |
| 2     | Dark Gray; Charcoal          | 34    | Translucent Dark Gray         |
| 3     | Medium Gray; Graphite        | 35    | Translucent Medium Gray       |
| 4     | Light Gray; Slate Gray       | 36    | Translucent Light Gray        |
| 5     | White Gray; Platinum         | 37    | Translucent White Gray        |
| 6     | Dark Beige                   | 38    | Translucent Dark Beige        |
| 7     | Medium Beige                 | 39    | Translucent Medium Beige      |
| 8     | Light Beige                  | 40    | Translucent Light Beige       |
| 9     | White Beige                  | 41    | Translucent White Beige       |
| 10    | Dark Red; Maroon             | 42    | Translucent Dark Red          |
| 11    | Medium Red                   | 43    | Translucent Medium Red        |
| 12    | Light Red; Soft Pink         | 44    | Translucent Light Red         |
| 13    | Dark Orange; Brown           | 45    | Translucent Dark Orange       |
| 14    | Medium Orange                | 46    | Translucent Medium Orange     |
| 15    | Light Orange                 | 47    | Translucent Light Orange      |
| 16    | Dark Yellow; Olive           | 48    | Translucent Dark Yellow       |
| 17    | Medium Yellow                | 49    | Translucent Medium Yellow     |
| 18    | Light Yellow                 | 50    | Translucent Light Yellow      |
| 19    | Dark Green; Woodland         | 51    | Translucent Dark Green        |
| 20    | Medium Green                 | 52    | Translucent Medium Green      |
| 21    | Light Green; Mint            | 53    | Translucent Light Green       |
| 22    | Dark Blue; Navy Blue         | 54    | Translucent Dark Blue         |
| 23    | Medium Blue; Royal Blue      | 55    | Translucent Medium Blue       |
| 24    | Light Blue; Sky Blue         | 56    | Translucent Light Blue        |
| 25    | Dark Purple                  | 57    | Translucent Dark Purple       |
| 26    | Medium Purple                | 58    | Translucent Medium Purple     |
| 27    | Light Purple; Lavender       | 59    | Translucent Light Purple      |
| 28    | Dark Magenta                 | 60    | Translucent Dark Magenta      |
| 29    | Medium Magenta; Hot Pink     | 61    | Translucent Medium Magenta    |
| 30    | Light Magenta; Bright Pink   | 62    | Translucent Light Magenta     |
| 31    | White                        | 63    | Translucent White             |

Colors 64-255 (`0b01000000`-`0b11111111`) are an ARGB color space with two bits per channel (`0bAARRGGBB`).

![](/docs/PopArt-8.svg)

Colors `0x100`-`0x1FF` are undefined.

Colors `0x200`-`0xFFF` (`0b001000000000`-`0b111111111111`) are an ARGB color space with three bits per channel (`0bAAARRRGGGBBB`).

Colors `0x1000`-`0xFFFF` (`0b0001000000000000`-`0b1111111111111111`) are an ARGB color space with four bits per channel (`0bAAAARRRRGGGGBBBB`, `0xARGB`).

Colors `0x10000`-`0x3FFFF` are undefined.

Colors `0x040000`-`0xFFFFFF` are an ARGB color space with six bits per channel (`0bAAAAAARRRRRRGGGGGGBBBBBB`).

Colors `0x01000000`-`0xFFFFFFFF` are an ARGB color space with eight bits per channel (`0xAARRGGBB`).
