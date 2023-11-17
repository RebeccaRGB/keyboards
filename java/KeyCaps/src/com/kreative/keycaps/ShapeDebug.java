package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.colorToString;
import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.getPaletteOpacity;
import static com.kreative.keycaps.ColorUtilities.parseColorIndex;
import static com.kreative.keycaps.KeyCapUnits.ROUNDING;
import static com.kreative.keycaps.ShapeUtilities.toSVGPath;
import static com.kreative.keycaps.ShapeUtilities.toSVGViewBox;

import java.awt.Color;
import java.awt.Shape;
import java.io.*;
import java.util.*;

public final class ShapeDebug {
	public static void main(String[] args) {
		KeyCapMold mold = null;
		String shapeString = "";
		KeyCapShape shape = KeyCapShape.DEFAULT;
		float size = 20;
		Color color = null;
		Float opacity = null;
		float padding = 10;
		File output = null;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equals("--help")) {
				help();
				return;
			} else if (arg.equals("-m") && argi < args.length) {
				mold = KeyCapMold.forName(args[argi++]);
			} else if (arg.equals("-s") && argi < args.length) {
				shapeString = args[argi++];
				try { shape = KeyCapShape.parse(shapeString, size); }
				catch (NumberFormatException e) {
					System.err.println("Invalid shape: " + arg);
					return;
				}
			} else if (arg.equals("-u") && argi < args.length) {
				arg = args[argi++];
				try { size = Float.parseFloat(arg); }
				catch (NumberFormatException e) {
					System.err.println("Invalid size: " + arg);
					return;
				}
			} else if (arg.equals("-c") && argi < args.length) {
				int i = parseColorIndex(args[argi++]);
				color = getPaletteColor(i);
				opacity = getPaletteOpacity(i);
			} else if (arg.equals("-a") && argi < args.length) {
				try { opacity = Float.parseFloat(args[argi++]); }
				catch (NumberFormatException e) { opacity = null; }
			} else if (arg.equals("-p") && argi < args.length) {
				arg = args[argi++];
				try { padding = Float.parseFloat(arg); }
				catch (NumberFormatException e) {
					System.err.println("Invalid padding: " + arg);
					return;
				}
			} else if (arg.equals("-o") && argi < args.length) {
				output = new File(args[argi++]);
			} else if (arg.equals("--")) {
				output = null;
			} else {
				help();
				return;
			}
		}
		
		Shape awtShape = shape.toAWTShape(size);
		String vbox = toSVGViewBox(awtShape, padding, ROUNDING);
		
		List<String> svg = new ArrayList<String>();
		svg.add("<?xml version=\"1.0\"?>");
		svg.add("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"" + vbox + ">");
		svg.add("<!--");
		svg.add("As Input:   " + shapeString);
		svg.add("As Parsed:  " + shape.toString());
		svg.add("Normalized: " + shape.toNormalizedString());
		svg.add("Minimized:  " + shape.toMinimizedString());
		svg.add("-->");
		if (mold == null) {
			String path = toSVGPath(awtShape, null, ROUNDING);
			String fill = colorToString(color, "white");
			if (opacity == null || opacity >= 1) {
				svg.add("<path d=\"" + path + "\" fill=\"" + fill + "\" stroke=\"black\"/>");
			} else if (opacity <= 0) {
				svg.add("<path d=\"" + path + "\" fill=\"none\" stroke=\"black\"/>");
			} else {
				svg.add("<path d=\"" + path + "\" fill=\"" + fill + "\" opacity=\"" + opacity + "\"/>");
				svg.add("<path d=\"" + path + "\" fill=\"none\" stroke=\"black\"/>");
			}
		} else {
			LayeredObject o = mold.createLayeredObject(awtShape, color, opacity);
			svg.add(o.toSVG("", "", ROUNDING));
		}
		svg.add("</svg>");
		
		if (output == null) {
			for (String line : svg) {
				System.out.println(line);
			}
		} else {
			try {
				FileOutputStream fos = new FileOutputStream(output);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				PrintWriter out = new PrintWriter(osw, true);
				for (String line : svg) out.println(line);
				out.flush();
				out.close();
			} catch (IOException e) {
				System.err.println("Could not write to output: " + e);
			}
		}
	}
	
	public static void help() {
		System.err.println("  -m <str>      Specify mold; default null");
		System.err.println("  -s <str>      Specify shape; default 1u");
		System.err.println("  -u <num>      Specify size; default 20");
		System.err.println("  -c <color>    Specify color");
		System.err.println("  -a <num>      Specify opacity (0.0 - 1.0)");
		System.err.println("  -p <num>      Specify padding; default 10");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  --            Specify standard output");
	}
	
	private ShapeDebug() {}
}
