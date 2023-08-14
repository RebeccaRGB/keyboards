#!/usr/bin/env bash
rm keyboards-svg/*.svg
cd keyboards
for i in *.txt
do java -jar ../java/KeyCaps/keycaps.jar -f "$i" -o "../keyboards-svg/${i%.*}.svg"
done
