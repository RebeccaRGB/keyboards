package com.kreative.keycaps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Viewer {
	public static void main(String[] args) throws IOException {
		boolean parseOpts = true;
		KeyCapMold mold = new IconKeyCapMold();
		float moldScale = 1;
		float size = 48;
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
					ViewerComponent vc = new ViewerComponent(renderer, layout);
					ViewerFrame vf = new ViewerFrame(vc);
					vf.setVisible(true);
				} else if (arg.equals("-f") && argi < args.length) {
					File input = new File(args[argi++]);
					KeyCapLayout layout = read(input);
					if (layout == null) return;
					AWTRenderer renderer = new AWTRenderer(mold, moldScale, size, null);
					ViewerComponent vc = new ViewerComponent(renderer, layout);
					ViewerFrame vf = new ViewerFrame(vc);
					vf.setVisible(true);
				} else {
					help();
					return;
				}
			} else {
				File input = new File(arg);
				KeyCapLayout layout = read(input);
				if (layout == null) return;
				AWTRenderer renderer = new AWTRenderer(mold, moldScale, size, null);
				ViewerComponent vc = new ViewerComponent(renderer, layout);
				ViewerFrame vf = new ViewerFrame(vc);
				vf.setVisible(true);
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
	
	public static void help() {
		System.err.println("  -m <str>      Specify mold; default IconKeyCapMold");
		System.err.println("  -s <num>      Specify mold scaling; default 1");
		System.err.println("  -u <num>      Specify size; default 48");
		System.err.println("  -i            Specify standard input");
		System.err.println("  -f <path>     Specify input file");
		System.err.println("  --            Treat remaining arguments as input files");
	}
	
	private Viewer() {}
}
