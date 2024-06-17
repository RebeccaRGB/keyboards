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
