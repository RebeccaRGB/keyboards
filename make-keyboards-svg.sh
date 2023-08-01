#!/usr/bin/env bash
rm -rf keyboards-svg
mkdir keyboards-svg
cd keyboards
for i in *.txt
do java -jar ../java/KeyCaps/keycaps.jar -f "$i" -o "../keyboards-svg/${i%.*}.svg"
done
