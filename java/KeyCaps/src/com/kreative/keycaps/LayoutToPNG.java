package com.kreative.keycaps;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

public final class LayoutToPNG extends LayoutConverter {
	public static void main(String[] args) throws IOException {
		new LayoutToPNG().mainImpl(args, ".png");
	}
	
	protected KeyCapMold mold = new IconKeyCapMold();
	protected float moldScale = 1;
	protected float size = 48;
	
	protected final int parseArg(String[] args, String arg, int argi) throws IOException {
		if (arg.equals("-m") && argi < args.length) {
			arg = args[argi++];
			mold = KeyCapMold.forName(arg);
			if (mold == null) {
				System.err.println("Unknown mold: " + arg);
				return EXIT;
			}
		} else if (arg.equals("-s") && argi < args.length) {
			arg = args[argi++];
			try { moldScale = Float.parseFloat(arg); }
			catch (NumberFormatException e) {
				System.err.println("Invalid scale: " + arg);
				return EXIT;
			}
		} else if (arg.equals("-u") && argi < args.length) {
			arg = args[argi++];
			try { size = Float.parseFloat(arg); }
			catch (NumberFormatException e) {
				System.err.println("Invalid size: " + arg);
				return EXIT;
			}
		} else {
			return HELP;
		}
		return argi;
	}
	
	protected final void write(OutputStream os, KeyCapLayout layout) throws IOException {
		AWTRenderer renderer = new AWTRenderer(mold, moldScale, size, null);
		Rectangle2D bounds = renderer.getBounds(layout);
		int w = (int)Math.round(bounds.getWidth());
		int h = (int)Math.round(bounds.getHeight());
		bounds = new Rectangle(0, 0, w, h);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.paint(g, layout, bounds, false);
		g.dispose();
		ImageIO.write(image, "png", os);
	}
	
	protected final void helpImpl() {
		help();
	}
	
	public static void help() {
		System.err.println("  -m <str>      Specify mold; default IconKeyCapMold");
		System.err.println("  -s <num>      Specify mold scaling; default 1");
		System.err.println("  -u <num>      Specify size; default 48");
		System.err.println("  -i            Specify standard input");
		System.err.println("  -f <path>     Specify input file");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  -p            Specify standard output");
		System.err.println("  --            Treat remaining arguments as input files");
	}
	
	private LayoutToPNG() {}
}
