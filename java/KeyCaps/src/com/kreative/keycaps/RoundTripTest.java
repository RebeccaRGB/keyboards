package com.kreative.keycaps;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public final class RoundTripTest {
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
				String name = outputFileName(input, ".log");
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
		out.println("toString():");
		String s = layout.toString();
		out.println(s);
		out.println("---");
		out.println("toNormalizedString():");
		String n = layout.toNormalizedString();
		out.println(n);
		out.println("---");
		out.println("toMinimizedString():");
		String m = layout.toMinimizedString();
		out.println(m);
		out.println("---");
		out.println("toKKCXString():");
		String x = toKKCXString(layout);
		out.println(x);
		out.println("---");
		out.println("Tests:");
		KeyCapLayout stdLayout = new KeyCapLayout(); stdLayout.parse(s);
		check(out, stdLayout.toString(), s, "parse(s).toString() != s");
		check(out, stdLayout.toNormalizedString(), n, "parse(s).toNormalizedString() != n");
		check(out, stdLayout.toMinimizedString(), m, "parse(s).toMinimizedString() != m");
		check(out, toKKCXString(stdLayout), x, "parse(s).toKKCXString() != x");
		KeyCapLayout normLayout = new KeyCapLayout(); normLayout.parse(n);
		check(out, normLayout.toNormalizedString(), n, "parse(n).toNormalizedString() != n");
		check(out, normLayout.toMinimizedString(), m, "parse(n).toMinimizedString() != m");
		KeyCapLayout minLayout = new KeyCapLayout(); minLayout.parse(m);
		check(out, minLayout.toNormalizedString(), n, "parse(m).toNormalizedString() != n");
		check(out, minLayout.toMinimizedString(), m, "parse(m).toMinimizedString() != m");
		KeyCapLayout xmlLayout = parseKKCX(x);
		check(out, xmlLayout.toString(), s, "parseKKCX(x).toString() != s");
		check(out, xmlLayout.toNormalizedString(), n, "parseKKCX(x).toNormalizedString() != n");
		check(out, xmlLayout.toMinimizedString(), m, "parseKKCX(x).toMinimizedString() != m");
		check(out, toKKCXString(xmlLayout), x, "parseKKCX(x).toKKCXString() != x");
	}
	
	private static String toKKCXString(KeyCapLayout layout) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		KKCXWriter.write(pw, layout);
		pw.flush();
		pw.close();
		return sw.toString().trim();
	}
	
	private static KeyCapLayout parseKKCX(String s) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes("UTF-8"));
		KeyCapLayout kcl = KKCXReader.parse("RoundTripTest", in);
		in.close();
		return kcl;
	}
	
	private static void check(PrintWriter out, String actual, String expected, String failMsg) {
		boolean passes = actual.equals(expected);
		out.println(passes ? "PASS" : ("FAIL: " + failMsg));
		if (!passes) {
			try {
				File ef = File.createTempFile("keycapsRoundTripTestExpected", ".tmp");
				File af = File.createTempFile("keycapsRoundTripTestActual", ".tmp");
				FileOutputStream eo = new FileOutputStream(ef);
				FileOutputStream ao = new FileOutputStream(af);
				OutputStreamWriter ew = new OutputStreamWriter(eo, "UTF-8");
				OutputStreamWriter aw = new OutputStreamWriter(ao, "UTF-8");
				ew.write(expected);
				aw.write(actual);
				ew.flush();
				aw.flush();
				ew.close();
				aw.close();
				String[] cmd = { "diff", ef.getAbsolutePath(), af.getAbsolutePath() };
				Process process = Runtime.getRuntime().exec(cmd);
				Scanner scanner = new Scanner(process.getInputStream());
				while (scanner.hasNextLine()) out.println(scanner.nextLine());
				scanner.close();
				process.waitFor();
				ef.delete();
				af.delete();
			} catch (Exception e) {
				for (String line : expected.split("\n")) {
					out.println("-" + line);
				}
				for (String line : actual.split("\n")) {
					out.println("+" + line);
				}
			}
		}
	}
	
	public static void help() {
		System.err.println("  -i            Specify standard input");
		System.err.println("  -f <path>     Specify input file");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  -p            Specify standard output");
		System.err.println("  --            Treat remaining arguments as input files");
	}
	
	private RoundTripTest() {}
}
