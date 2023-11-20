package com.kreative.keycaps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public final class LayoutToSVG {
	public static void main(String[] args) throws IOException {
		boolean parseOpts = true;
		KeyCapMold mold = new IconKeyCapMold();
		float moldScale = 1;
		float size = 48;
		File output = null;
		boolean test = false;
		
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
					SVGRenderer svg = new SVGRenderer(mold, moldScale, size, null);
					int res = write(null, output, svg, layout, test);
					if (res == 0xE) return;
					if (res == 0xF) output = null;
				} else if (arg.equals("-f") && argi < args.length) {
					File input = new File(args[argi++]);
					KeyCapLayout layout = read(input);
					if (layout == null) return;
					SVGRenderer svg = new SVGRenderer(mold, moldScale, size, null);
					int res = write(input, output, svg, layout, test);
					if (res == 0xE) return;
					if (res == 0xF) output = null;
				} else if (arg.equals("-o") && argi < args.length) {
					output = new File(args[argi++]);
				} else if (arg.equals("-p")) {
					output = null;
				} else if (arg.equals("-t")) {
					test = true;
				} else if (arg.equals("-T")) {
					test = false;
				} else {
					help();
					return;
				}
			} else {
				File input = new File(arg);
				KeyCapLayout layout = read(input);
				if (layout == null) return;
				SVGRenderer svg = new SVGRenderer(mold, moldScale, size, null);
				int res = write(input, output, svg, layout, test);
				if (res == 0xE) return;
				if (res == 0xF) output = null;
			}
		}
	}
	
	private static KeyCapLayout read(File input) {
		try {
			InputStream in = new FileInputStream(input);
			KeyCapLayout layout = read(in);
			in.close();
			return layout;
		} catch (IOException e) {
			System.err.println("Could not read from input: " + e);
			return null;
		}
	}
	
	private static KeyCapLayout read(InputStream in) {
		try {
			StringBuffer contents = new StringBuffer();
			Scanner scanner = new Scanner(in, "UTF-8");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.length() == 0 || line.startsWith("#")) continue;
				contents.append(line);
				contents.append("\n");
			}
			scanner.close();
			KeyCapLayout layout = new KeyCapLayout();
			layout.parse(contents.toString());
			return layout;
		} catch (IllegalArgumentException e) {
			System.err.println("Could not parse input: " + e);
			return null;
		}
	}
	
	private static int write(File input, File output, SVGRenderer svg, KeyCapLayout layout, boolean test) {
		try {
			if (output == null) {
				write(System.out, svg, layout, test);
				return 0x5;
			} else if (output.isDirectory()) {
				String name = (input == null) ? "out.svg" : input.getName().replaceFirst("[.][Tt][Xx][Tt]$", ".svg");
				OutputStream out = new FileOutputStream(new File(output, name));
				write(out, svg, layout, test);
				out.close();
				return 0xD;
			} else {
				OutputStream out = new FileOutputStream(output);
				write(out, svg, layout, test);
				out.close();
				return 0xF;
			}
		} catch (IOException e) {
			System.err.println("Could not write to output: " + e);
			return 0xE;
		}
	}
	
	private static void write(OutputStream os, SVGRenderer svg, KeyCapLayout layout, boolean test) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		out.println(svg.render(layout));
		if (test) {
			out.println("<!-- toString():");
			String s = layout.toString();
			out.println(s);
			out.println("-->");
			out.println("<!-- toNormalizedString():");
			String n = layout.toNormalizedString();
			out.println(n);
			out.println("-->");
			out.println("<!-- toMinimizedString():");
			String m = layout.toMinimizedString();
			out.println(m);
			out.println("-->");
			out.println("<!-- Tests:");
			KeyCapLayout stdLayout = new KeyCapLayout(); stdLayout.parse(s);
			check(out, stdLayout.toString(), s, "parse(s).toString() != s");
			check(out, stdLayout.toNormalizedString(), n, "parse(s).toNormalizedString() != n");
			check(out, stdLayout.toMinimizedString(), m, "parse(s).toMinimizedString() != m");
			KeyCapLayout normLayout = new KeyCapLayout(); normLayout.parse(n);
			check(out, normLayout.toNormalizedString(), n, "parse(n).toNormalizedString() != n");
			check(out, normLayout.toMinimizedString(), m, "parse(n).toMinimizedString() != m");
			KeyCapLayout minLayout = new KeyCapLayout(); minLayout.parse(m);
			check(out, minLayout.toNormalizedString(), n, "parse(m).toNormalizedString() != n");
			check(out, minLayout.toMinimizedString(), m, "parse(m).toMinimizedString() != m");
			out.println("-->");
		}
	}
	
	private static void check(PrintWriter out, String actual, String expected, String failMsg) {
		boolean passes = actual.equals(expected);
		out.println(passes ? "PASS" : ("FAIL: " + failMsg));
		if (!passes) {
			for (String line : expected.split("\n")) {
				out.println("-" + line);
			}
			for (String line : actual.split("\n")) {
				out.println("+" + line);
			}
		}
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
	
	private LayoutToSVG() {}
}
