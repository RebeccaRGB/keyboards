package com.kreative.keycaps;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public final class LayoutToKKCX extends LayoutConverter {
	public static void main(String[] args) throws IOException {
		new LayoutToKKCX().mainImpl(args, ".kkcx");
	}
	
	protected final int parseArg(String[] args, String arg, int argi) throws IOException {
		return HELP;
	}
	
	protected final void write(OutputStream os, KeyCapLayout layout) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		KKCXWriter.write(out, layout);
	}
	
	protected final void helpImpl() {
		help();
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
