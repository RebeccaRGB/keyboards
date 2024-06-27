package com.kreative.keycaps;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.UIManager;

public class Viewer {
	public static void main(String[] args) throws IOException {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Key Caps"); } catch (Exception e) {}
		try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		
		try {
			Method getModule = Class.class.getMethod("getModule");
			Object javaDesktop = getModule.invoke(Toolkit.getDefaultToolkit().getClass());
			Object allUnnamed = getModule.invoke(Main.class);
			Class<?> module = Class.forName("java.lang.Module");
			Method addOpens = module.getMethod("addOpens", String.class, module);
			addOpens.invoke(javaDesktop, "sun.awt.X11", allUnnamed);
		} catch (Exception e) {}
		
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Field aacn = tk.getClass().getDeclaredField("awtAppClassName");
			aacn.setAccessible(true);
			aacn.set(tk, "Key Caps");
		} catch (Exception e) {}
		
		boolean opened = false;
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
					open(mold, moldScale, size, null);
					opened = true;
				} else if (arg.equals("-f") && argi < args.length) {
					File input = new File(args[argi++]);
					open(mold, moldScale, size, input);
					opened = true;
				} else {
					help();
					return;
				}
			} else {
				File input = new File(arg);
				open(mold, moldScale, size, input);
				opened = true;
			}
		}
		
		if (!opened) {
			File kbdDir = UIUtilities.getKeyboardDirectory();
			if (kbdDir != null) {
				File kbdFile = UIUtilities.getKeyboardFile(kbdDir);
				if (kbdFile != null) {
					try {
						KeyCapLayout layout = KeyCapReader.read(kbdFile);
						open(mold, moldScale, size, layout, kbdDir);
						return;
					} catch (IOException e) {
						System.err.println("Could not read from input: " + e);
					}
				}
			}
			try {
				InputStream in = Viewer.class.getResourceAsStream("ANSI.kkcx");
				KeyCapLayout layout = KeyCapReader.read("ANSI.kkcx", in);
				open(mold, moldScale, size, layout, kbdDir);
				return;
			} catch (IOException e) {
				System.err.println("Could not read from input: " + e);
			}
		}
	}
	
	public static void open(KeyCapMold mold, float moldScale, float size, File input) {
		try {
			if (input == null) {
				KeyCapLayout layout = KeyCapReader.read("input", System.in);
				open(mold, moldScale, size, layout, UIUtilities.getKeyboardDirectory());
			} else if (input.isDirectory()) {
				File file = UIUtilities.getKeyboardFile(input);
				if (file != null) {
					KeyCapLayout layout = KeyCapReader.read(file);
					open(mold, moldScale, size, layout, input);
				} else {
					throw new IOException("No applicable files found in directory");
				}
			} else {
				KeyCapLayout layout = KeyCapReader.read(input);
				open(mold, moldScale, size, layout, UIUtilities.getKeyboardDirectory());
			}
		} catch (IOException e) {
			System.err.println("Could not read from input: " + e);
		}
	}
	
	public static void open(KeyCapMold mold, float moldScale, float size, KeyCapLayout layout, File kbdDir) {
		AWTRenderer renderer = new AWTRenderer(mold, moldScale, size, null);
		ViewerComponent vc = new ViewerComponent(renderer, layout);
		ViewerFrame vf = new ViewerFrame(vc, kbdDir);
		vf.setVisible(true);
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
