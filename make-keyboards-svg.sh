#!/usr/bin/env bash
if [ ! -f java/KeyCaps/keycaps.jar ]; then
	cd java/KeyCaps
	make keycaps.jar
	cd ../..
fi
if [ -d keyboards-svg ]; then
	rm -rf keyboards-svg/*
	java -jar java/KeyCaps/keycaps.jar --svg -m max -u 54 -o keyboards-svg -f keyboards-xml
fi
if [ -d keyboards-svg-usb ]; then
	rm -rf keyboards-svg-usb/*
	java -jar java/KeyCaps/keycaps.jar --svg -m max -u 54 -U -o keyboards-svg-usb -f keyboards-xml
	grep -L -R -Z font-family=\"monospace\" keyboards-svg-usb | xargs -0 rm
	find keyboards-svg-usb -type d -empty -delete
fi
