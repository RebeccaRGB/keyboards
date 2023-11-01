package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.getPaletteOpacity;
import static com.kreative.keycaps.ShapeUtilities.toSVGPath;
import static com.kreative.keycaps.ShapeUtilities.toSVGViewBox;

import java.awt.Color;
import java.awt.Shape;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeDebug {
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
				try { shape = new KeyCapShape(shapeString, size); }
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
		String vbox = toSVGViewBox(awtShape, padding, 1000);
		
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
			String path = toSVGPath(awtShape, null, 1000);
			String fill = (
				(color == null) ? "white" :
				("#" + Integer.toHexString(0xFF000000 | color.getRGB()).substring(2))
			);
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
			svg.add(o.toSVG("", ""));
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
	
	private static final Pattern HEX_PATTERN = Pattern.compile("^#([0-9A-Fa-f]+)$");
	
	private static int parseColorIndex(String s) {
		if (s == null || (s = s.trim()).length() == 0) return 0;
		try {
			Matcher m = HEX_PATTERN.matcher(s);
			if (m.matches()) {
				int v = (int)Long.parseLong(m.group(1), 16);
				switch (m.group(1).length()) {
					case 8: return ((v >> 24) == 0) ? 32 : v;
					case 6: return 0xFF000000 | v;
					case 4: return ((v >> 12) == 0) ? 32 : v;
					case 3: return 0xF000 | v;
					default: return 0;
				}
			}
			if (s.startsWith("0X")) return (int)Long.parseLong(s.substring(2), 16);
			if (s.startsWith("0x")) return (int)Long.parseLong(s.substring(2), 16);
			if (s.startsWith("0O")) return (int)Long.parseLong(s.substring(2), 8);
			if (s.startsWith("0o")) return (int)Long.parseLong(s.substring(2), 8);
			if (s.startsWith("$")) return (int)Long.parseLong(s.substring(1), 16);
			if (s.startsWith("0")) return (int)Long.parseLong(s.substring(1), 8);
			return (int)Long.parseLong(s, 10);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
