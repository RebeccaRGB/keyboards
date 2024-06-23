package com.kreative.keycaps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class LayoutConverter {
	protected static final int HELP = -1;
	protected static final int EXIT = -2;
	
	protected boolean parseOpts = true;
	protected File output = null;
	
	protected final void mainImpl(String[] args, String suffix) throws IOException {
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parseOpts && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parseOpts = false;
				} else if (arg.equals("--help")) {
					helpImpl();
					return;
				} else if (arg.equals("-i")) {
					KeyCapLayout layout = read(System.in);
					if (layout == null) return;
					int res = write(null, output, suffix, layout);
					if (res == 0xE) return;
					if (res == 0xF) output = null;
				} else if (arg.equals("-f") && argi < args.length) {
					File input = new File(args[argi++]);
					KeyCapLayout layout = read(input);
					if (layout == null) return;
					int res = write(input, output, suffix, layout);
					if (res == 0xE) return;
					if (res == 0xF) output = null;
				} else if (arg.equals("-o") && argi < args.length) {
					output = new File(args[argi++]);
				} else if (arg.equals("-p")) {
					output = null;
				} else {
					int res = parseArg(args, arg, argi);
					if (res >= argi) { argi = res; continue; }
					if (res >= HELP) helpImpl();
					return;
				}
			} else {
				File input = new File(arg);
				KeyCapLayout layout = read(input);
				if (layout == null) return;
				int res = write(input, output, suffix, layout);
				if (res == 0xE) return;
				if (res == 0xF) output = null;
			}
		}
	}
	
	protected abstract int parseArg(String[] args, String arg, int argi) throws IOException;
	protected abstract void write(OutputStream os, KeyCapLayout layout) throws IOException;
	protected abstract void helpImpl();
	
	protected final int write(File input, File output, String suffix, KeyCapLayout layout) {
		try {
			if (output == null) {
				write(System.out, layout);
				return 0x5;
			} else if (output.isDirectory()) {
				String name = outputFileName(input, suffix);
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
	
	protected static String outputFileName(File input, String extension) {
		if (input == null) return "out" + extension;
		String inputName = input.getName();
		int offset = inputName.lastIndexOf(".");
		if (offset <= 0) return inputName + extension;
		if (inputName.substring(offset).equalsIgnoreCase(extension)) return inputName + extension;
		return inputName.substring(0, offset) + extension;
	}
	
	protected static KeyCapLayout read(InputStream in) {
		try {
			return KeyCapReader.read("input", in);
		} catch (IOException e) {
			System.err.println("Could not parse input: " + e);
			return null;
		}
	}
	
	protected static KeyCapLayout read(File input) {
		try {
			return KeyCapReader.read(input);
		} catch (IOException e) {
			System.err.println("Could not read from input: " + e);
			return null;
		}
	}
}
