# KKCX Document Structure Reference

KKCX (Kreative Key Caps XML) is an XML document format for describing the physical layout of a keyboard for a computer, terminal, or typewriter. Its document type declaration is:

    <!DOCTYPE keyCapLayout PUBLIC "-//Kreative//DTD KreativeKeyCaps 1.0//EN" "http://www.kreativekorp.com/dtd/kkcx.dtd">

## `<keyCapLayout>`

The root element of a KKCX document. Describes the physical layout of a keyboard.

A `<keyCapLayout>` element may contain zero or more `<k>` (keycap) or `<p>` (property) elements.

| Attribute | Data Type | Description                                                                                                                     |
| --------- | --------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `name`    | string    | An identifying name for the keyboard.                                                                                           |
| `kbdType` | integer   | The value of the `kbdType` low-memory global under Mac OS Classic corresponding to this keyboard.                               |
| `gestalt` | integer   | The value of the `kbd ` gestalt selector under Mac OS Classic corresponding to this keyboard.                                   |
| `vs`      | string    | A code describing the default topography of keycaps, for e.g. stepped keycaps. See [Variation Selectors](#variation-selectors). |
| `cc`      | color     | The default color of keycaps. See [Colors](#colors).                                                                            |
| `lc`      | color     | The default color of legend items. See [Colors](#colors).                                                                       |
| `lh`      | float     | The default line height of legend items as a fraction of the height of their bounding boxes.                                    |
| `a`       | anchor    | The default position of legend items within their bounding boxes. See [Anchors](#anchors).                                      |

## `<k>`

Describes an individual key on a keyboard.

A `<k>` element may contain zero or more plain text, `<l>` (legend), or `<p>` (property) elements. If there is at least one `<l>` element, plain text elements are ignored; if there are no `<l>` elements, the plain text content is used as the content of an implied `<l>` element.

| Attribute | Data Type | Description                                                                                                                                                                      |
| --------- | --------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `x`       | unit      | The X coordinate of the bottom left of the keycap. The origin is the left edge of the keyboard. Positive coordinates extend to the right. See [Keyboard Units](#keyboard-units). |
| `y`       | unit      | The Y coordinate of the bottom left of the keycap. The origin is the bottom edge of the keyboard. Positive coordinates extend upwards. See [Keyboard Units](#keyboard-units).    |
| `shape`   | shape     | The dimensions of the keycap starting at the bottom left and proceeding counter-clockwise. See [Keycap Shapes](#keycap-shapes).                                                  |
| `type`    | string    | A code describing the purposes and locations of child `<l>` (legend) elements without explicit `type` attributes. See [Keycap Types](#keycap-types).                             |
| `usb`     | integer   | The USB HID code produced by this key, e.g. "123" or "0xAB".                                                                                                                     |
| `vs`      | string    | A code describing the topography of the keycap, for e.g. stepped keycaps. See [Variation Selectors](#variation-selectors).                                                       |
| `cc`      | color     | The color of the keycap. See [Colors](#colors).                                                                                                                                  |
| `lc`      | color     | The default color of legend items. See [Colors](#colors).                                                                                                                        |
| `lh`      | float     | The default line height of legend items as a fraction of the height of their bounding boxes.                                                                                     |
| `a`       | anchor    | The default position of legend items within their bounding boxes. See [Anchors](#anchors).                                                                                       |

## `<l>`

Describes an individual item of a legend printed on a keycap. A legend item may be a printable character such as "A", "1", or "!" or a function such as "esc", "enter", or "caps lock".

An `<l>` element may contain zero or more plain text or `<p>` (property) elements.

| Attribute | Data Type | Description                                                                                                                  |
| --------- | --------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `type`    | string    | A code describing the purpose and location of the legend item. See [Legend Types](#legend-types).                            |
| `path`    | SVG path  | A custom SVG path to print on the keycap in place of text. Path coordinates are fractions of the height of the bounding box. |
| `lc`      | color     | The color of the legend item. See [Colors](#colors).                                                                         |
| `lh`      | float     | The line height of the legend item as a fraction of the height of its bounding box.                                          |
| `a`       | anchor    | The position of the legend item within its bounding box. See [Anchors](#anchors).                                            |

## `<p>`

Sets the value of a custom property on a `<keyCapLayout>`, `<k>`, or `<l>` element. The property value also applies to all descendant elements unless overridden by a descendant `<p>` element.

A `<p>` element may not contain any child elements.

| Attribute | Data Type | Description         |
| --------- | --------- | ------------------- |
| `k`       | string    | The property key.   |
| `v`       | string    | The property value. |

## Keyboard Units

The X and Y coordinates of keycaps are expressed using a numeric value followed by a unit, e.g. "1u" (one unit) or "0.75in" (three quarters of an inch). The units supported are:

| Unit | Description                                                                                                                                                     |
| ---- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `u`  | The standard keyboard unit. 1u = 4v = 20w = 0.75in = 19.05mm = 54pt.                                                                                            |
| `v`  | One quarter of a standard keyboard unit. 4v = 1u. The minimal unit of the size of a standard keycap.                                                            |
| `w`  | One twentieth of a standard keyboard unit. 20w = 1u. Equivalent to the units of a Mac OS Classic `KCAP` resource or a pixel within the Key Caps desk accessory. |
| `in` | Inches. 0.75in = 1u.                                                                                                                                            |
| `mm` | Millimeters. 19.05mm = 1u.                                                                                                                                      |
| `pt` | Points. 54pt = 1u. 72pt = 1in. Equivalent to a pixel at 72dpi.                                                                                                  |

## Keycap Shapes

The shape of a keycap is described using a series of numeric values followed by a unit (see [Keyboard Units](#keyboard-units)), e.g. "1.5u", "1+2u", or "1.25+2+1.5u". A keycap shape starts at the leftmost corner of the bottom edge of the keycap and proceeds counter-clockwise for positive values or clockwise for negative values. Later values can be omitted if the rest of the shape can be inferred from earlier values. Most keycap shapes can be specified using only one, two, or three values.

![](/docs/shapes.svg)

## Keycap Types

Codes used for `type` attributes on `<k>` (keycap) elements. Child `<l>` (legend) elements without explicit `type` attributes will be assigned legend types in order from the third column shown below.

| Type | Description                     | Legend Types, In Order |
| ---- | ------------------------------- | ---------------------- |
| `F`  | function                        | `F`                    |
| `G`  | dual function, vertical         | `F`, `AF`              |
| `H`  | dual function, horizontal       | `LF`, `RF`             |
| `L`  | letter                          | `L`                    |
| `A`  | letter with letter alt          | `L`, `AL`              |
| `T`  | letter with symbol alt ("tri")  | `L`, `AU`, `AS`        |
| `S`  | symbol                          | `U`, `S`               |
| `Z`  | symbol with letter alt          | `U`, `S`, `AL`         |
| `Q`  | symbol with symbol alt ("quad") | `U`, `S`, `AU`, `AS`   |
| `N`  | numpad                          | `N`, `NF`              |

"Symbol" refers to a legend (or part of a legend) consisting of two printable characters stacked vertically.
In an unshifted state, the lower character is produced. In a shifted state, the upper character is produced.
None, one, or both of the characters may be letters, or even a lowercase/uppercase pair;
regardless, both characters are printed on the keycap.

"Letter" refers to a legend (or part of a legend) consisting of a single printable character, not necessarily an actual letter.
If the character is not an actual letter, both unshifted and shifted states produce the same character.
If the character is an actual letter, the unshifted state produces lowercase and the shifted state produces uppercase,
but only one case (usually uppercase) is printed on the keycap.

![](/docs/types.svg)

## Legend Types

Codes used for `type` attributes on `<l>` (legend) elements.

| Type  | Description     | Type  | Description           |
| ----- | --------------- | ----- | --------------------- |
| `F`   | function        | `FF`  | front function        |
| `AF`  | alt function    | `FAF` | front alt function    |
| `LF`  | left function   | `FLF` | front left function   |
| `RF`  | right function  | `FRF` | front right function  |
| `L`   | letter          | `FL`  | front letter          |
| `U`   | unshifted       | `FU`  | front unshifted       |
| `S`   | shifted         | `FS`  | front shifted         |
| `AL`  | alt letter      | `FAL` | front alt letter      |
| `AU`  | alt unshifted   | `FAU` | front alt unshifted   |
| `AS`  | alt shifted     | `FAS` | front alt shifted     |
| `N`   | numpad number   | `FN`  | front numpad number   |
| `NF`  | numpad function | `FNF` | front numpad function |

"Front" refers to legends printed on the side of the keycap facing the user, as opposed to the top of the keycap where legends are usually printed.

"Letter" refers to a legend (or part of a legend) consisting of a single printable character, not necessarily an actual letter.
If the character is not an actual letter, both unshifted and shifted states produce the same character.
If the character is an actual letter, the unshifted state produces lowercase and the shifted state produces uppercase,
but only one case (usually uppercase) is printed on the keycap.

![](/docs/types.svg)

## Variation Selectors

Codes used for `vs` attributes which describe the topography of the keycap, for e.g. stepped keycaps.

| Type | Description                                                                                                                        |
| ---- | ---------------------------------------------------------------------------------------------------------------------------------- |
| `s`  | Stepped. The active surface of the keycap is a square centered within the widest rectangle of the keycap shape.                    |
| `sc` | Stepped caps lock or control. The active surface of the keycap is the left two thirds of the widest rectangle of the keycap shape. |
| `sb` | Stepped backspace. The active surface of the keycap is the right two thirds of the widest rectangle of the keycap shape.           |
| `se` | Stepped enter. The active surface of the keycap is the tallest rectangle of the keycap shape.                                      |
| `r`  | Recessed. The keycap is half the height of other keycaps.                                                                          |

![](/docs/vs.svg)

## Colors

Colors for keycaps and legends may be specified as either a hex code (e.g. `#ABC` or `#AABBCC` for RGB or `#ABCD` or `#AABBCCDD` for ARGB) or an index into a color palette.

Colors 0-31 are a standardized set of opaque colors. Colors 32-63 are a corresponding set of translucent colors.

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

![](/docs/PopArt-6.svg)

Colors 64-255 (`0b01000000`-`0b11111111`) are an ARGB color space with two bits per channel (`0bAARRGGBB`).

![](/docs/PopArt-8.svg)

Colors `0x100`-`0x1FF` are undefined.

Colors `0x200`-`0xFFF` (`0b001000000000`-`0b111111111111`) are an ARGB color space with three bits per channel (`0bAAARRRGGGBBB`).

Colors `0x1000`-`0xFFFF` (`0b0001000000000000`-`0b1111111111111111`) are an ARGB color space with four bits per channel (`0bAAAARRRRGGGGBBBB`, `0xARGB`).

Colors `0x10000`-`0x3FFFF` are undefined.

Colors `0x040000`-`0xFFFFFF` are an ARGB color space with six bits per channel (`0bAAAAAARRRRRRGGGGGGBBBBBB`).

Colors `0x01000000`-`0xFFFFFFFF` are an ARGB color space with eight bits per channel (`0xAARRGGBB`).

## Anchors

Alignment of legend items is specified using one of the following codes.

| Anchor Code                                                        |
| ------------------------------------------------------------------ |
| `5`, `c`, `center`, `centre`, `m`, `mid`, or `middle`              |
| `7`, `nw`, `northwest`, `ul`, `upperleft`, `tl`, or `topleft`      |
| `8`, `n`, `north`, `u`, `up`, `upper`, `t`, or `top`               |
| `9`, `ne`, `northeast`, `ur`, `upperright`, `tr`, or `topright`    |
| `6`, `e`, `east`, `r`, or `right`                                  |
| `3`, `se`, `southeast`, `lr`, `lowerright`, `br`, or `bottomright` |
| `2`, `s`, `south`, `d`, `down`, `lower`, `b`, or `bottom`          |
| `1`, `sw`, `southwest`, `ll`, `lowerleft`, `bl`, or `bottomleft`   |
| `4`, `w`, `west`, `l`, or `left`                                   |
