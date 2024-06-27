package com.kreative.keycaps;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			Viewer.main(args);
		} else if (eic(args[0], "--help", "help")) {
			if (args.length == 1) help();
			else if (args.length == 2) help(args[1]);
			else for (int i = 1; i < args.length; i++) {
				System.err.println(args[i] + ":");
				help(args[i]);
			}
		} else if (eic(args[0], "--LayoutToKKCX", "--toKKCX", "--kkcx", "--xml", "LayoutToKKCX", "toKKCX", "kkcx", "xml")) {
			LayoutToKKCX.main(tail(args));
		} else if (eic(args[0], "--LayoutToPNG", "--toPNG", "--png", "LayoutToPNG", "toPNG", "png")) {
			LayoutToPNG.main(tail(args));
		} else if (eic(args[0], "--LayoutToSVG", "--toSVG", "--svg", "LayoutToSVG", "toSVG", "svg")) {
			LayoutToSVG.main(tail(args));
		} else if (eic(args[0], "--LayoutToText", "--toText", "--text", "--txt", "LayoutToText", "toText", "text", "txt")) {
			LayoutToText.main(tail(args));
		} else if (eic(args[0], "--PopArt", "PopArt")) {
			PopArt.main(tail(args));
		} else if (eic(args[0], "--RoundTripTest", "RoundTripTest")) {
			RoundTripTest.main(tail(args));
		} else if (eic(args[0], "--ShapeDebug", "ShapeDebug")) {
			ShapeDebug.main(tail(args));
		} else if (eic(args[0], "--Viewer", "--view", "Viewer", "view")) {
			Viewer.main(tail(args));
		} else if (eic(args[0], "--Kbitter", "Kbitter")) {
			Kbitter.main(tail(args));
		} else if (eic(args[0], "--Kcapper", "Kcapper")) {
			Kcapper.main(tail(args));
		} else if (eic(args[0], "--KKCXPositionTest", "--PositionTest", "KKCXPositionTest", "PositionTest")) {
			KKCXPositionTest.main(tail(args));
		} else {
			Viewer.main(args);
		}
	}
	
	public static void help(String what) {
		if (what == null) {
			help();
		} else if (eic(what, "--help", "help")) {
			System.err.println("What yer lookin' at.");
		} else if (eic(what, "--LayoutToKKCX", "--toKKCX", "--kkcx", "LayoutToKKCX", "toKKCX", "kkcx")) {
			LayoutToKKCX.help();
		} else if (eic(what, "--LayoutToPNG", "--toPNG", "--png", "LayoutToPNG", "toPNG", "png")) {
			LayoutToPNG.help();
		} else if (eic(what, "--LayoutToSVG", "--toSVG", "--svg", "LayoutToSVG", "toSVG", "svg")) {
			LayoutToSVG.help();
		} else if (eic(what, "--LayoutToText", "--toText", "--text", "--txt", "LayoutToText", "ToText", "text", "txt")) {
			LayoutToText.help();
		} else if (eic(what, "--PopArt", "PopArt")) {
			PopArt.help();
		} else if (eic(what, "--RoundTripTest", "RoundTripTest")) {
			RoundTripTest.help();
		} else if (eic(what, "--ShapeDebug", "ShapeDebug")) {
			ShapeDebug.help();
		} else if (eic(what, "--Viewer", "--view", "Viewer", "view")) {
			Viewer.help();
		} else if (eic(what, "--Kbitter", "Kbitter")) {
			System.err.println("No help available for " + what + ".");
		} else if (eic(what, "--Kcapper", "Kcapper")) {
			System.err.println("No help available for " + what + ".");
		} else if (eic(what, "--KKCXPositionTest", "--PositionTest", "KKCXPositionTest", "PositionTest")) {
			System.err.println("No help available for " + what + ".");
		} else {
			help();
		}
	}
	
	public static void help() {
		System.err.println("  LayoutToKKCX      Convert text format to KKCX");
		System.err.println("  LayoutToPNG       Generate PNG of keycap layout");
		System.err.println("  LayoutToSVG       Generate SVG of keycap layout");
		System.err.println("  LayoutToText      Convert KKCX to text format");
		System.err.println("  PopArt            Generate SVG of color palette");
		System.err.println("  RoundTripTest     Verify integrity of conversion");
		System.err.println("  ShapeDebug        Generate SVG of keycap shape");
		System.err.println("  Viewer            View keycap layout in window");
		System.err.println("  Kbitter           Convert kbit resource to PNG");
		System.err.println("  Kcapper           Generate SVG of KCAP resource");
		System.err.println("  KKCXPositionTest  Print normalized x,y coordinates");
	}
	
	private static boolean eic(String a, String... matches) {
		for (String match : matches) {
			if (match.equalsIgnoreCase(a)) {
				return true;
			}
		}
		return false;
	}
	
	private static String[] tail(String[] a) {
		String[] b = new String[a.length - 1];
		for (int i = 1; i < a.length; i++) b[i - 1] = a[i];
		return b;
	}
	
	private Main() {}
}
