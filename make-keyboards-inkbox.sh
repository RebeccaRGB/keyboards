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
