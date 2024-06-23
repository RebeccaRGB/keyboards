package com.kreative.keycaps;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public final class LayoutToText extends LayoutConverter {
	public static enum Format {
		STANDARD{public String format(KeyCapLayout layout){return layout.toString();}},
		NORMALIZED{public String format(KeyCapLayout layout){return layout.toNormalizedString();}},
		MINIMIZED{public String format(KeyCapLayout layout){return layout.toMinimizedString();}};
		public abstract String format(KeyCapLayout layout);
	};
	
	public static void main(String[] args) throws IOException {
		new LayoutToText().mainImpl(args, ".txt");
	}
	
	protected Format format = Format.STANDARD;
	
	protected final int parseArg(String[] args, String arg, int argi) throws IOException {
		if (arg.equals("-s")) {
			format = Format.STANDARD;
		} else if (arg.equals("-n")) {
			format = Format.NORMALIZED;
		} else if (arg.equals("-m")) {
			format = Format.MINIMIZED;
		} else {
			return HELP;
		}
		return argi;
	}
	
	protected final void write(OutputStream os, KeyCapLayout layout) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		out.println(format.format(layout));
	}
	
	protected final void helpImpl() {
		help();
	}
	
	public static void help() {
		System.err.println("  -s            Specify standard form");
		System.err.println("  -n            Specify normalized form");
		System.err.println("  -m            Specify minimized form");
		System.err.println("  -i            Specify standard input");
		System.err.println("  -f <path>     Specify input file");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  -p            Specify standard output");
		System.err.println("  --            Treat remaining arguments as input files");
	}
	
	private LayoutToText() {}
}
