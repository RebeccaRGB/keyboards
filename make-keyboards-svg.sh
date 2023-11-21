#!/usr/bin/env bash
rm keyboards-svg/*.svg
java -jar java/KeyCaps/keycaps.jar -m max -u 54 -o keyboards-svg -- keyboards/*.txt
