package com.kreative.keycaps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class LayoutConverter {
	protected static final int HELP = -1;
	protected static final int EXIT = -2;
	
	protected boolean parseOpts = true;
	protected File output = null;
	
	protected final void mainImpl(String[] args, String suffix) {
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
					readInputWriteOutput(null, suffix);
				} else if (arg.equals("-f") && argi < args.length) {
					File input = new File(args[argi++]);
					readInputWriteOutput(input, suffix);
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
				readInputWriteOutput(input, suffix);
			}
		}
	}
	
	protected abstract int parseArg(String[] args, String arg, int argi);
	protected abstract void write(OutputStream os, KeyCapLayout layout) throws IOException;
	protected abstract void helpImpl();
	
	// Read from the specified input and write to the specified output.
	protected final void readInputWriteOutput(File input, String suffix) {
		try {
			if (input == null) {
				KeyCapLayout layout = KeyCapReader.read("input", System.in);
				writeOutput(input, suffix, layout);
			} else if (input.isDirectory()) {
				readDirWriteOutput(input, suffix);
			} else {
				KeyCapLayout layout = KeyCapReader.read(input);
				writeOutput(input, suffix, layout);
			}
		} catch (IOException e) {
			System.err.println("Could not read from input: " + e);
		}
	}
	
	// Write a single layout (read above from a single file) to the specified output.
	protected final void writeOutput(File input, String suffix, KeyCapLayout layout) {
		try {
			if (output == null) {
				write(System.out, layout);
			} else if (output.isDirectory()) {
				String name = outputFileName(input, suffix);
				OutputStream out = new FileOutputStream(new File(output, name));
				write(out, layout);
				out.close();
			} else {
				OutputStream out = new FileOutputStream(output);
				write(out, layout);
				out.close();
				output = null;
			}
		} catch (IOException e) {
			System.err.println("Could not write to output: " + e);
		}
	}
	
	// Read multiple layouts from an input directory and write them to the specified output.
	protected final void readDirWriteOutput(File input, String suffix) {
		try {
			if (output == null) {
				readDirWriteStream(input, System.out);
			} else if (output.isDirectory()) {
				readDirWriteDir(input, suffix, output);
			} else {
				OutputStream out = new FileOutputStream(output);
				readDirWriteStream(input, out);
				out.close();
				output = null;
			}
		} catch (IOException e) {
			System.err.println("Could not write to output: " + e);
		}
	}
	
	// Read multiple layouts from an input directory and write them to an output stream.
	protected final void readDirWriteStream(File input, OutputStream os) {
		for (File child : input.listFiles()) {
			if (child.getName().startsWith(".") || child.getName().endsWith("\r")) {
				continue;
			} else if (child.isDirectory()) {
				readDirWriteStream(child, os);
			} else {
				try {
					KeyCapLayout layout = KeyCapReader.read(child);
					try {
						write(os, layout);
					} catch (IOException e) {
						System.err.println("Could not write to output: " + e);
					}
				} catch (IOException e) {
					System.err.println("Could not read from input: " + e);
				}
			}
		}
	}
	
	// Read multiple layouts from an input directory and write them to an output directory.
	protected final void readDirWriteDir(File input, String suffix, File output) {
		for (File child : input.listFiles()) {
			if (child.getName().startsWith(".") || child.getName().endsWith("\r")) {
				continue;
			} else if (child.isDirectory()) {
				File outputChild = new File(output, child.getName());
				if (!outputChild.exists()) outputChild.mkdir();
				readDirWriteDir(child, suffix, outputChild);
			} else {
				try {
					KeyCapLayout layout = KeyCapReader.read(child);
					try {
						String name = outputFileName(child, suffix);
						OutputStream out = new FileOutputStream(new File(output, name));
						write(out, layout);
						out.close();
					} catch (IOException e) {
						System.err.println("Could not write to output: " + e);
					}
				} catch (IOException e) {
					System.err.println("Could not read from input: " + e);
				}
			}
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
}
