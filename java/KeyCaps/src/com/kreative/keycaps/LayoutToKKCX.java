package com.kreative.keycaps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public final class LayoutToKKCX {
	public static void main(String[] args) throws IOException {
		boolean parseOpts = true;
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
				} else if (arg.equals("-i")) {
					KeyCapLayout layout = read(System.in);
					if (layout == null) return;
					int res = write(null, output, layout);
					if (res == 0xE) return;
					if (res == 0xF) output = null;
				} else if (arg.equals("-f") && argi < args.length) {
					File input = new File(args[argi++]);
					KeyCapLayout layout = read(input);
					if (layout == null) return;
					int res = write(input, output, layout);
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
				int res = write(input, output, layout);
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
	
	private static int write(File input, File output, KeyCapLayout layout) {
		try {
			if (output == null) {
				write(System.out, layout);
				return 0x5;
			} else if (output.isDirectory()) {
				String name = outputFileName(input, ".kkcx");
				OutputStream out = new FileOutputStream(new File(output, name));
				write(out, layout);
				out.close();
				return 0xD;
			} else {
				OutputStream out = new FileOutputStream(output);
				write(out, layout);
				out.close();
				return 0xF;
			}
		} catch (IOException e) {
			System.err.println("Could not write to output: " + e);
			return 0xE;
		}
	}
	
	private static void write(OutputStream os, KeyCapLayout layout) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		KKCXWriter.write(out, layout);
	}
	
	public static void help() {
		System.err.println("  -i            Specify standard input");
		System.err.println("  -f <path>     Specify input file");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  -p            Specify standard output");
		System.err.println("  --            Treat remaining arguments as input files");
	}
	
	private LayoutToKKCX() {}
}
