#!/usr/bin/env bash
if [ ! -f java/KeyCaps/keycaps.jar ]; then
	cd java/KeyCaps
	make keycaps.jar
	cd ../..
fi
if [ -d keyboards-inkbox ]; then
	rm -rf keyboards-inkbox/*
	java -jar java/KeyCaps/keycaps.jar --inkbox -o keyboards-inkbox -f keyboards-xml/Inkbox
fi
if [ -d keyboards-inkbox-preview ]; then
	rm -rf keyboards-inkbox-preview/*
	java -jar java/KeyCaps/keycaps.jar --png -m "flat(6.5,32.5)" -u 188 -b "#F0F" -o keyboards-inkbox-preview -f keyboards-xml/Inkbox
fi
