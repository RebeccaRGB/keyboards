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

public class LayoutToSVG {
	public static void main(String[] args) throws IOException {
		boolean parseOpts = true;
		File outputFile = null;
		boolean test = false;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parseOpts && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parseOpts = false;
				} else if (arg.equals("-i")) {
					KeyCapLayout kcl = read(System.in);
					if (outputFile == null) {
						write(System.out, kcl, test);
					} else if (outputFile.isDirectory()) {
						String outputName = "out.svg";
						OutputStream out = new FileOutputStream(new File(outputFile, outputName));
						write(out, kcl, test);
						out.close();
					} else {
						OutputStream out = new FileOutputStream(outputFile);
						write(out, kcl, test);
						out.close();
						outputFile = null;
					}
				} else if (arg.equals("-f") && argi < args.length) {
					File inputFile = new File(args[argi++]);
					InputStream in = new FileInputStream(inputFile);
					KeyCapLayout kcl = read(in);
					in.close();
					if (outputFile == null) {
						write(System.out, kcl, test);
					} else if (outputFile.isDirectory()) {
						String outputName = inputFile.getName().replaceFirst("[.][Tt][Xx][Tt]$", ".svg");
						OutputStream out = new FileOutputStream(new File(outputFile, outputName));
						write(out, kcl, test);
						out.close();
					} else {
						OutputStream out = new FileOutputStream(outputFile);
						write(out, kcl, test);
						out.close();
						outputFile = null;
					}
				} else if (arg.equals("-o") && argi < args.length) {
					outputFile = new File(args[argi++]);
				} else if (arg.equals("-t")) {
					test = true;
				} else if (arg.equals("-T")) {
					test = false;
				} else {
					System.err.println("Unknown option: " + arg);
					return;
				}
			} else {
				File inputFile = new File(arg);
				InputStream in = new FileInputStream(inputFile);
				KeyCapLayout kcl = read(in);
				in.close();
				if (outputFile == null) {
					write(System.out, kcl, test);
				} else if (outputFile.isDirectory()) {
					String outputName = inputFile.getName().replaceFirst("[.][Tt][Xx][Tt]$", ".svg");
					OutputStream out = new FileOutputStream(new File(outputFile, outputName));
					write(out, kcl, test);
					out.close();
				} else {
					OutputStream out = new FileOutputStream(outputFile);
					write(out, kcl, test);
					out.close();
					outputFile = null;
				}
			}
		}
	}
	
	private static KeyCapLayout read(InputStream in) throws IOException {
		KeyCapLayout kcl = new KeyCapLayout();
		StringBuffer contents = new StringBuffer();
		Scanner scanner = new Scanner(in, "UTF-8");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) continue;
			contents.append(line);
			contents.append("\n");
		}
		scanner.close();
		kcl.parse(contents.toString());
		return kcl;
	}
	
	private static void write(OutputStream os, KeyCapLayout kcl, boolean test) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		out.println(kcl.toSVG(new IconKeyCapMold(), 1, KeyCapEngraver.DEFAULT, 48));
		if (test) {
			out.println("<!-- toString():");
			String s = kcl.toString();
			out.println(s);
			out.println("-->");
			out.println("<!-- toNormalizedString():");
			String n = kcl.toNormalizedString();
			out.println(n);
			out.println("-->");
			out.println("<!-- toMinimizedString():");
			String m = kcl.toMinimizedString();
			out.println(m);
			out.println("-->");
			out.println("<!-- Tests:");
			KeyCapLayout skcl = new KeyCapLayout(); skcl.parse(s);
			check(out, skcl.toString(), s, "parse(s).toString() != s");
			check(out, skcl.toNormalizedString(), n, "parse(s).toNormalizedString() != n");
			check(out, skcl.toMinimizedString(), m, "parse(s).toMinimizedString() != m");
			KeyCapLayout nkcl = new KeyCapLayout(); nkcl.parse(n);
			check(out, nkcl.toNormalizedString(), n, "parse(n).toNormalizedString() != n");
			check(out, nkcl.toMinimizedString(), m, "parse(n).toMinimizedString() != m");
			KeyCapLayout mkcl = new KeyCapLayout(); mkcl.parse(m);
			check(out, mkcl.toNormalizedString(), n, "parse(m).toNormalizedString() != n");
			check(out, mkcl.toMinimizedString(), m, "parse(m).toMinimizedString() != m");
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
}
