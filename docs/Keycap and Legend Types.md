# Keycap and Legend Types

Used for `type` attributes on `k` (keycap) and `l` (legend) elements.

![](/docs/types.svg)

## Keycap Types

Used for `type` attributes on `k` (keycap) elements.

Child `l` elements without `type` attributes will be assigned legend types in order from the third column.

"Symbol" refers to a legend (or part of a legend) consisting of two printable characters stacked vertically.
In an unshifted state, the lower character is produced. In a shifted state, the upper character is produced.
None, one, or both of the characters may be letters, or even a lowercase/uppercase pair;
regardless, both characters are printed on the keycap.

"Letter" refers to a legend (or part of a legend) consisting of a single printable character, not necessarily an actual letter.
If the character is not an actual letter, both unshifted and shifted states produce the same character.
If the character is an actual letter, the unshifted state produces lowercase and the shifted state produces uppercase,
but only one case is printed on the keycap.

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

## Legend Types

Used for `type` attributes on `l` (legend) elements.

"Front" refers to legends on the side of the keycap facing the user, as opposed to the top of the keycap where legends are usually printed.

"Symbol" refers to a legend (or part of a legend) consisting of two printable characters stacked vertically.
In an unshifted state, the lower character is produced. In a shifted state, the upper character is produced.
None, one, or both of the characters may be letters, or even a lowercase/uppercase pair;
regardless, both characters are printed on the keycap.

"Letter" refers to a legend (or part of a legend) consisting of a single printable character, not necessarily an actual letter.
If the character is not an actual letter, both unshifted and shifted states produce the same character.
If the character is an actual letter, the unshifted state produces lowercase and the shifted state produces uppercase,
but only one case (usually uppercase) is printed on the keycap.

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
