package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.getPaletteOpacity;
import static com.kreative.keycaps.ShapeUtilities.toSVGViewBox;
import static com.kreative.keycaps.ShapeUtilities.translate;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;

public class PopArt {
	public static void main(String[] args) {
		KeyCapMold mold = new MaxKeyCapMold();
		KeyCapShape shape = KeyCapShape.DEFAULT;
		float size = 54;
		int columns = 16;
		int rows = 16;
		File output = null;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equals("--help")) {
				help();
				return;
			} else if (arg.equals("-m") && argi < args.length) {
				arg = args[argi++];
				mold = KeyCapMold.forName(arg);
				if (mold == null) {
					System.err.println("Unknown mold: " + arg);
					return;
				}
			} else if (arg.equals("-s") && argi < args.length) {
				arg = args[argi++];
				try { shape = new KeyCapShape(arg, size); }
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
			} else if (arg.equals("-6")) {
				columns = rows = 8;
			} else if (arg.equals("-8")) {
				columns = rows = 16;
			} else if (arg.equals("-c") && argi < args.length) {
				arg = args[argi++];
				try {
					columns = Integer.parseInt(arg);
					if (columns <= 0) throw new NumberFormatException(arg);
				} catch (NumberFormatException e) {
					System.err.println("Invalid column count: " + arg);
					return;
				}
			} else if (arg.equals("-r") && argi < args.length) {
				arg = args[argi++];
				try {
					rows = Integer.parseInt(arg);
					if (rows <= 0) throw new NumberFormatException(arg);
				} catch (NumberFormatException e) {
					System.err.println("Invalid row count: " + arg);
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
		Rectangle2D bounds = shape.getBounds(size);
		String vbox = toSVGViewBox(new Rectangle2D.Double(
			bounds.getX(), bounds.getY(),
			bounds.getWidth() * columns,
			bounds.getHeight() * rows
		), 0, 1000);
		
		List<String> svg = new ArrayList<String>();
		svg.add("<?xml version=\"1.0\"?>");
		svg.add("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"" + vbox + ">");
		for (int i = 0, y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++, i++) {
				float fx = (float)(bounds.getWidth() * x);
				float fy = (float)(bounds.getHeight() * y);
				Shape s = translate(awtShape, fx, fy);
				Color color = getPaletteColor(i);
				Float opacity = getPaletteOpacity(i);
				LayeredObject o = mold.createLayeredObject(s, color, opacity);
				svg.add(o.toSVG("", ""));
			}
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
		System.err.println("  -m <str>      Specify mold; default MaxKeyCapMold");
		System.err.println("  -s <str>      Specify shape; default 1u");
		System.err.println("  -u <num>      Specify size; default 54");
		System.err.println("  -6            Specify 8 rows and 8 columns");
		System.err.println("  -8            Specify 16 rows and 16 columns");
		System.err.println("  -c <int>      Specify column count");
		System.err.println("  -r <int>      Specify row count");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  --            Specify standard output");
	}
}
