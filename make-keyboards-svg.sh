#!/usr/bin/env bash
rm keyboards-svg/*.svg
java -jar java/KeyCaps/keycaps.jar -o keyboards-svg -- keyboards/*.txt
