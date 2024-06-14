#!/usr/bin/env bash
if [ ! -f java/KeyCaps/keycaps.jar ]; then
	cd java/KeyCaps
	make
	cd ../..
fi
rm keyboards-svg/*.svg
java -jar java/KeyCaps/keycaps.jar --svg -m max -u 54 -o keyboards-svg -- keyboards/*.txt
