package com.kreative.keycaps;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

public class LayoutToPNG {
	public static void main(String[] args) throws IOException {
		boolean parseOpts = true;
		KeyCapMold mold = new IconKeyCapMold();
		float moldScale = 1;
		float size = 48;
		File output = null;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parseOpts && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parseOpts = false;
				} else if (arg.equals("--help")) {
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
					try { moldScale = Float.parseFloat(arg); }
					catch (NumberFormatException e) {
						System.err.println("Invalid scale: " + arg);
						return;
					}
				} else if (arg.equals("-u") && argi < args.length) {
					arg = args[argi++];
					try { size = Float.parseFloat(arg); }
					catch (NumberFormatException e) {
						System.err.println("Invalid size: " + arg);
						return;
					}
				} else if (arg.equals("-i")) {
					KeyCapLayout layout = read(System.in);
					if (layout == null) return;
					AWTRenderer renderer = new AWTRenderer(mold, moldScale, size, null);
					int res = write(null, output, renderer, layout);
					if (res == 0xE) return;
					if (res == 0xF) output = null;
				} else if (arg.equals("-f") && argi < args.length) {
					File input = new File(args[argi++]);
					KeyCapLayout layout = read(input);
					if (layout == null) return;
					AWTRenderer renderer = new AWTRenderer(mold, moldScale, size, null);
					int res = write(input, output, renderer, layout);
					if (res == 0xE) return;
					if (res == 0xF) output = null;
				} else if (arg.equals("-o") && argi < args.length) {
					output = new File(args[argi++]);
				} else if (arg.equals("-p")) {
					output = null;
				} else {
					help();
					return;
				}
			} else {
				File input = new File(arg);
				KeyCapLayout layout = read(input);
				if (layout == null) return;
				AWTRenderer renderer = new AWTRenderer(mold, moldScale, size, null);
				int res = write(input, output, renderer, layout);
				if (res == 0xE) return;
				if (res == 0xF) output = null;
			}
		}
	}
	
	private static KeyCapLayout read(File input) {
		try {
			return KeyCapReader.read(input);
		} catch (IOException e) {
			System.err.println("Could not read from input: " + e);
			return null;
		}
	}
	
	private static KeyCapLayout read(InputStream in) {
		try {
			return KeyCapReader.read("input", in);
		} catch (IOException e) {
			System.err.println("Could not parse input: " + e);
			return null;
		}
	}
	
	private static String outputFileName(File input, String extension) {
		if (input == null) return "out" + extension;
		String inputName = input.getName();
		int offset = inputName.lastIndexOf(".");
		if (offset <= 0) return inputName + extension;
		if (inputName.substring(offset).equalsIgnoreCase(extension)) return inputName + extension;
		return inputName.substring(0, offset) + extension;
	}
	
	private static int write(File input, File output, AWTRenderer renderer, KeyCapLayout layout) {
		try {
			if (output == null) {
				write(System.out, renderer, layout);
				return 0x5;
			} else if (output.isDirectory()) {
				String name = outputFileName(input, ".png");
				OutputStream out = new FileOutputStream(new File(output, name));
				write(out, renderer, layout);
				out.close();
				return 0xD;
			} else {
				OutputStream out = new FileOutputStream(output);
				write(out, renderer, layout);
				out.close();
				return 0xF;
			}
		} catch (IOException e) {
			System.err.println("Could not write to output: " + e);
			return 0xE;
		}
	}
	
	private static void write(OutputStream os, AWTRenderer renderer, KeyCapLayout layout) throws IOException {
		Rectangle2D bounds = renderer.getBounds(layout);
		int w = (int)Math.round(bounds.getWidth());
		int h = (int)Math.round(bounds.getHeight());
		bounds = new Rectangle(0, 0, w, h);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.paint(g, layout, bounds, false);
		g.dispose();
		ImageIO.write(image, "png", os);
	}
	
	public static void help() {
		System.err.println("  -m <str>      Specify mold; default IconKeyCapMold");
		System.err.println("  -s <num>      Specify mold scaling; default 1");
		System.err.println("  -u <num>      Specify size; default 48");
		System.err.println("  -i            Specify standard input");
		System.err.println("  -f <path>     Specify input file");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  -p            Specify standard output");
		System.err.println("  --            Treat remaining arguments as input files");
	}
	
	private LayoutToPNG() {}
}
