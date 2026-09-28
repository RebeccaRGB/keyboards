package com.kreative.keycaps.fritzing;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.kreative.keycaps.KeyCap;
import com.kreative.keycaps.KeyCapLayout;
import com.kreative.keycaps.KeyCapUnits;
import com.kreative.keycaps.LayoutConverter;
import com.kreative.keycaps.Padding;
import com.kreative.keycaps.ShapeUtilities;

public class LayoutToFritzing extends LayoutConverter {
	// Fritzing units per millimeter. Why this value‽
	private static final double FUPMM = 3.543307086614173228;
	
	// Fritzing units per keyboard unit.
	private static final float FUPU = 67.5f;
	
	// Keyswitch Metrics
	private static final float MX_PCB_X_OFFSET = -28.2974f;
	private static final float MX_PCB_Y_OFFSET = -28.2974f;
	private static final float MX_BBD_X_OFFSET = -40.7742f;
	private static final float MX_BBD_Y_OFFSET = -42.74885f;
	private static final float MX_SCH_X_OFFSET = -21.2074f;
	private static final float MX_SCH_Y_OFFSET = -8.47885f;
	
	// Stabilizer Metrics
	private static final float SHORT_STAB_LENGTH = 84.6f; // 0.94in
	private static final float SMALL_HOLE_OFFSET = -7.40355f;
	private static final float LARGE_HOLE_OFFSET = -9.0866f;
	private static final float SMALL_HOLE_Y_OFFSET = 24.75f; // 0.275in
	private static final float LARGE_HOLE_Y_OFFSET = 29.25f; // 0.325in
	
	// Diode Metrics
	private static final float DIODE_PCB_X_OFFSET = -17.19f;
	private static final float DIODE_PCB_Y_OFFSET = -4.5f;
	private static final double[][] DIODE_PCB_TRANSFORM = {
		null,
		{0, -1, 0, 1, 0, 0, 12.69, 21.69, 1},
		{-1, 0, 0, 0, -1, 0, 34.38, 9, 1},
		{0, 1, 0, -1, 0, 0, 21.69, -12.69, 1}
	};
	private static final float DIODE_BBD_X_OFFSET = -18.67435f;
	private static final float DIODE_BBD_Y_OFFSET = -4.5f;
	private static final double[][] DIODE_BBD_TRANSFORM = {
		null,
		{0, -1, 0, 1, 0, 0, 14.1744, 23.1744, 1},
		{-1, 0, 0, 0, -1, 0, 37.3487, 9, 1},
		{0, 1, 0, -1, 0, 0, 23.1744, -14.1744, 1}
	};
	private static final float DIODE_SCH_X_OFFSET = -5.23435f;
	private static final float DIODE_SCH_Y_OFFSET = -13.76125f;
	private static final double[][] DIODE_SCH_TRANSFORM = {
		{0, 1, 0, -1, 0, 0, 18.9956, 8.52692, 1},
		null,
		{0, -1, 0, 1, 0, 0, -8.52692, 18.9956, 1},
		{-1, 0, 0, 0, -1, 0, 10.4687, 27.5225, 1}
	};
	
	private static final String MX_ID_REF = "Cherry_MX_Simple_89826b091064a71c26c4f904b201a97c_29";
	
	private static final String[] RESOURCES = {
		"part.Cherry_MX_Simple_89826b091064a71c26c4f904b201a97c_29.fzp",
		"svg.breadboard.Cherry_MX_Simple_8772ce5a527117f2bb59918f83fdaf08_3_breadboard.svg",
		"svg.icon.Cherry_MX_Simple_8772ce5a527117f2bb59918f83fdaf08_3_icon.svg",
		"svg.pcb.Cherry_MX_Simple_8772ce5a527117f2bb59918f83fdaf08_3_pcb.svg",
		"svg.schematic.Cherry_MX_Simple_8772ce5a527117f2bb59918f83fdaf08_3_schematic.svg"
	};
	
	private Padding pcbBorderMM = new Padding(2.54f, 2.54f, 2.54f, 2.54f);
	private int modelIndex = 85514968;
	private int mxIndex = 1;
	private int holeIndex = 1;
	private int diodeIndex = 1;
	private int diodeAlignX = 0;
	private int diodeAlignY = -1;
	private int diodeRotate = 0;
	private float shortStabThreshold = 3;
	private int horizontalStabOrientation = 1;
	private int verticalStabOrientation = 1;
	
	public static void main(String[] args) {
		new LayoutToFritzing().mainImpl(args, ".fzz");
	}
	
	protected final int parseArg(String[] args, String arg, int argi) {
		if (arg.equals("-b") && argi < args.length) {
			arg = args[argi++];
			try { pcbBorderMM = parsePaddingMM(arg); }
			catch (NumberFormatException e) {
				System.err.println("Invalid length: " + arg);
				return EXIT;
			}
		} else if (arg.equals("-d") && argi < args.length) {
			arg = args[argi++];
			try { setDiodePlacement(arg); }
			catch (IllegalArgumentException e) {
				System.err.println("Invalid diode placement: " + arg);
				return EXIT;
			}
		} else if (arg.equals("-s") && argi < args.length) {
			arg = args[argi++];
			try { shortStabThreshold = Float.parseFloat(arg); }
			catch (NumberFormatException e) {
				System.err.println("Invalid length: " + arg);
				return EXIT;
			}
		} else if (arg.equals("-H")) {
			horizontalStabOrientation = 1;
		} else if (arg.equals("-h")) {
			horizontalStabOrientation = -1;
		} else if (arg.equals("-V")) {
			verticalStabOrientation = 1;
		} else if (arg.equals("-v")) {
			verticalStabOrientation = -1;
		} else {
			return HELP;
		}
		return argi;
	}
	
	protected final void setDiodePlacement(String a) {
		if (a.equalsIgnoreCase("al")) { diodeAlignX = 0; diodeAlignY = -1; diodeRotate = 0; return; }
		if (a.equalsIgnoreCase("ar")) { diodeAlignX = 0; diodeAlignY = -1; diodeRotate = 2; return; }
		if (a.equalsIgnoreCase("bl")) { diodeAlignX = 0; diodeAlignY = +1; diodeRotate = 0; return; }
		if (a.equalsIgnoreCase("br")) { diodeAlignX = 0; diodeAlignY = +1; diodeRotate = 2; return; }
		if (a.equalsIgnoreCase("lb")) { diodeAlignX = -1; diodeAlignY = 0; diodeRotate = 1; return; }
		if (a.equalsIgnoreCase("la")) { diodeAlignX = -1; diodeAlignY = 0; diodeRotate = 3; return; }
		if (a.equalsIgnoreCase("rb")) { diodeAlignX = +1; diodeAlignY = 0; diodeRotate = 1; return; }
		if (a.equalsIgnoreCase("ra")) { diodeAlignX = +1; diodeAlignY = 0; diodeRotate = 3; return; }
		throw new IllegalArgumentException("Invalid diode placement: " + a);
	}
	
	protected final void write(OutputStream os, KeyCapLayout layout) throws IOException {
		Rectangle2D.Float boundsMM = layout.getBounds(KeyCapUnits.MM);
		float boardXmm = boundsMM.x - pcbBorderMM.left;
		float boardYmm = boundsMM.y - pcbBorderMM.top;
		float boardWmm = boundsMM.width + pcbBorderMM.left + pcbBorderMM.right;
		float boardHmm = boundsMM.height + pcbBorderMM.top + pcbBorderMM.bottom;
		
		StringBuffer board = new StringBuffer();
		board.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		board.append("<module fritzingVersion=\"0.9.6b..\">\n");
		board.append("<boards>\n");
		board.append("<board"
				+ " moduleId=\"TwoLayerRectanglePCBModuleID\""
				+ " title=\"Rectangular PCB - Resizable\""
				+ " instance=\"PCB1\""
				+ " width=\"" + (boardWmm / 10f) + "cm\""
				+ " height=\"" + (boardHmm / 10f) + "cm\"/>\n");
		board.append("</boards>\n");
		board.append("<instances>\n");
		board.append("<instance"
				+ " moduleIdRef=\"TwoLayerRectanglePCBModuleID\""
				+ " modelIndex=\"5783\""
				+ " path=\":/resources/parts/core/rectangle_pcb_two_layers.fzp\">\n");
		board.append("<property name=\"layers\" value=\"2\"/>\n");
		board.append("<property name=\"width\" value=\"" + boardWmm + "\"/>\n");
		board.append("<property name=\"height\" value=\"" + boardHmm + "\"/>\n");
		board.append("<title>PCB1</title>\n");
		board.append("<views>\n");
		board.append("<pcbView layer=\"board\">\n");
		board.append(instanceGeometry(1.5, boardXmm * FUPMM, boardYmm * FUPMM, null));
		board.append("</pcbView>\n");
		board.append("</views>\n");
		board.append("</instance>\n");
		for (KeyCap k : layout) {
			Point2D.Float pos = k.getPosition().getLocation(FUPU);
			Shape shape = k.getShape().toAWTShape(FUPU);
			shape = ShapeUtilities.translate(shape, pos.x, -pos.y);
			Rectangle2D rect = ShapeUtilities.getLargestRect(shape, null);
			if (rect == null || rect.isEmpty()) continue;
			// Keyswitch
			board.append("<instance"
					+ " moduleIdRef=\"" + MX_ID_REF + "\""
					+ " modelIndex=\"" + (modelIndex++) + "\""
					+ " path=\":/resources/parts/contrib/" + MX_ID_REF + ".fzp\">\n");
			board.append("<title>MX" + (mxIndex++) + "</title>\n");
			board.append("<views>\n");
			board.append("<pcbView layer=\"copper0\">\n");
			board.append(instanceGeometry(5.5,
					rect.getCenterX() + MX_PCB_X_OFFSET,
					rect.getCenterY() + MX_PCB_Y_OFFSET, null));
			board.append("</pcbView>\n");
			board.append("<breadboardView layer=\"breadboard\">\n");
			board.append(instanceGeometry(2.5,
					rect.getCenterX() + MX_BBD_X_OFFSET,
					rect.getCenterY() + MX_BBD_Y_OFFSET, null));
			board.append("</breadboardView>\n");
			board.append("<schematicView layer=\"schematic\">\n");
			board.append(instanceGeometry(2.5,
					rect.getCenterX() + MX_SCH_X_OFFSET,
					rect.getCenterY() + MX_SCH_Y_OFFSET, null));
			board.append("</schematicView>\n");
			board.append("</views>\n");
			board.append("</instance>\n");
			// Stabilizer
			if (rect.getWidth() >= rect.getHeight() && rect.getWidth() >= FUPU * 2) {
				// Horizontal Stabilizer
				double sl = stabLength(rect.getWidth());
				double xl = rect.getCenterX() - sl/2;
				double xr = rect.getCenterX() + sl/2;
				double ys = rect.getCenterY() - SMALL_HOLE_Y_OFFSET * horizontalStabOrientation;
				double yl = rect.getCenterY() + LARGE_HOLE_Y_OFFSET * horizontalStabOrientation;
				board.append(holeInstance(3.05, xl + SMALL_HOLE_OFFSET, ys + SMALL_HOLE_OFFSET));
				board.append(holeInstance(3.05, xr + SMALL_HOLE_OFFSET, ys + SMALL_HOLE_OFFSET));
				board.append(holeInstance(4, xl + LARGE_HOLE_OFFSET, yl + LARGE_HOLE_OFFSET));
				board.append(holeInstance(4, xr + LARGE_HOLE_OFFSET, yl + LARGE_HOLE_OFFSET));
			}
			if (rect.getHeight() > rect.getWidth() && rect.getHeight() >= FUPU * 2) {
				// Vertical Stabilizer
				double sl = stabLength(rect.getHeight());
				double ya = rect.getCenterY() - sl/2;
				double yb = rect.getCenterY() + sl/2;
				double xs = rect.getCenterX() - SMALL_HOLE_Y_OFFSET * verticalStabOrientation;
				double xl = rect.getCenterX() + LARGE_HOLE_Y_OFFSET * verticalStabOrientation;
				board.append(holeInstance(3.05, xs + SMALL_HOLE_OFFSET, ya + SMALL_HOLE_OFFSET));
				board.append(holeInstance(3.05, xs + SMALL_HOLE_OFFSET, yb + SMALL_HOLE_OFFSET));
				board.append(holeInstance(4, xl + LARGE_HOLE_OFFSET, ya + LARGE_HOLE_OFFSET));
				board.append(holeInstance(4, xl + LARGE_HOLE_OFFSET, yb + LARGE_HOLE_OFFSET));
			}
			// Diode
			double dpx = rect.getCenterX() + (rect.getWidth() * diodeAlignX / 2);
			double dpy = rect.getCenterY() + (rect.getHeight() * diodeAlignY / 2);
			board.append("<instance"
					+ " moduleIdRef=\"3254CBFC44diode\""
					+ " modelIndex=\"" + (modelIndex++) + "\""
					+ " path=\":/resources/parts/core/diode_1N4001_300mil.fzp\">\n");
			board.append("<property name=\"part number\" value=\"1N4001\"/>\n");
			board.append("<title>D" + (diodeIndex++) + "</title>\n");
			board.append("<views>\n");
			board.append("<pcbView layer=\"copper0\">\n");
			board.append(instanceGeometry(5.5,
					dpx + DIODE_PCB_X_OFFSET,
					dpy + DIODE_PCB_Y_OFFSET,
					DIODE_PCB_TRANSFORM[diodeRotate & 3]));
			board.append("</pcbView>\n");
			board.append("<breadboardView layer=\"breadboard\">\n");
			board.append(instanceGeometry(2.5,
					dpx + DIODE_BBD_X_OFFSET,
					dpy + DIODE_BBD_Y_OFFSET,
					DIODE_BBD_TRANSFORM[diodeRotate & 3]));
			board.append("</breadboardView>\n");
			board.append("<schematicView layer=\"schematic\">\n");
			board.append(instanceGeometry(2.5,
					dpx + DIODE_SCH_X_OFFSET,
					dpy + DIODE_SCH_Y_OFFSET,
					DIODE_SCH_TRANSFORM[diodeRotate & 3]));
			board.append("</schematicView>\n");
			board.append("</views>\n");
			board.append("</instance>\n");
		}
		board.append("</instances>\n");
		board.append("</module>\n");
		
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.putNextEntry(new ZipEntry("board.fz"));
		zos.write(board.toString().getBytes("UTF-8"));
		zos.closeEntry();
		byte[] buf = new byte[65536];
		for (String resource : RESOURCES) {
			zos.putNextEntry(new ZipEntry(resource));
			InputStream in = LayoutToFritzing.class.getResourceAsStream(resource);
			zos.write(buf, 0, in.read(buf));
			in.close();
			zos.closeEntry();
		}
		zos.finish();
	}
	
	protected final void helpImpl() {
		help();
	}
	
	public static void help() {
		System.err.println("  -b <len>      Specify amount of padding around key matrix; default 0.1in");
		System.err.println("  -d <str>      Specify placement and orientation of diodes;");
		System.err.println("                first letter determines placement relative to switch,");
		System.err.println("                second letter determines orientation of cathode; one of:");
		System.err.println("                    al, ar, bl, br, la, lb, ra, rb");
		System.err.println("                (a = above, b = below, l = left, r = right)");
		System.err.println("  -h            Mount horizontal stabilizers with larger holes on top");
		System.err.println("  -H            Mount horizontal stabilizers with larger holes on bottom");
		System.err.println("  -v            Mount vertical stabilizers with larger holes to the left");
		System.err.println("  -V            Mount vertical stabilizers with larger holes to the right");
		System.err.println("  -i            Specify standard input");
		System.err.println("  -f <path>     Specify input file");
		System.err.println("  -o <path>     Specify output file");
		System.err.println("  -p            Specify standard output");
		System.err.println("  --            Treat remaining arguments as input files");
	}
	
	private LayoutToFritzing() {}
	
	private double stabLength(double length) {
		if (length < FUPU * shortStabThreshold) return SHORT_STAB_LENGTH;
		if ((length -= FUPU) < SHORT_STAB_LENGTH) return SHORT_STAB_LENGTH;
		return length;
	}
	
	private String holeInstance(double size, double x, double y) {
		StringBuffer sb = new StringBuffer();
		sb.append("<instance"
				+ " moduleIdRef=\"HoleModuleID\""
				+ " modelIndex=\"" + (modelIndex++) + "\""
				+ " path=\":/resources/parts/core/hole.fzp\">\n");
		sb.append("<property name=\"hole size\" value=\"" + size + "mm,0.0mm\"/>\n");
		sb.append("<title>Hole" + (holeIndex++) + "</title>\n");
		sb.append("<views>\n");
		sb.append("<pcbView layer=\"copper0\">\n");
		sb.append(instanceGeometry(5.5, x, y, null));
		sb.append("</pcbView>\n");
		sb.append("<breadboardView layer=\"copper0\">\n");
		sb.append(instanceGeometry(5.5, x, y, null));
		sb.append("</breadboardView>\n");
		sb.append("<schematicView layer=\"copper0\">\n");
		sb.append(instanceGeometry(5.5, x, y, null));
		sb.append("</schematicView>\n");
		sb.append("</views>\n");
		sb.append("</instance>\n");
		return sb.toString();
	}
	
	private static String instanceGeometry(double z, double x, double y, double[] tx) {
		StringBuffer sb = new StringBuffer();
		sb.append("<geometry z=\"");
		sb.append(z);
		sb.append("\" x=\"");
		sb.append(x);
		sb.append("\" y=\"");
		sb.append(y);
		if (tx == null) {
			sb.append("\"/>\n");
		} else {
			sb.append("\">\n<transform m11=\"");
			sb.append(tx[0]);
			sb.append("\" m12=\"");
			sb.append(tx[1]);
			sb.append("\" m13=\"");
			sb.append(tx[2]);
			sb.append("\" m21=\"");
			sb.append(tx[3]);
			sb.append("\" m22=\"");
			sb.append(tx[4]);
			sb.append("\" m23=\"");
			sb.append(tx[5]);
			sb.append("\" m31=\"");
			sb.append(tx[6]);
			sb.append("\" m32=\"");
			sb.append(tx[7]);
			sb.append("\" m33=\"");
			sb.append(tx[8]);
			sb.append("\"/>\n</geometry>\n");
		}
		return sb.toString();
	}
	
	private static final String NUM = "([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?)";
	private static final String UNIT = "(?<unit>(cm|in|mm|pc|pt|px|Q|u|v|w))";
	private static final Pattern LEN = Pattern.compile("^\\s*" + NUM + "\\s*" + UNIT + "\\s*$");
	
	private static float parseLengthMM(String s) {
		Matcher m = LEN.matcher(s);
		if (m.matches()) {
			float num = Float.parseFloat(m.group(1));
			String unit = m.group("unit");
			if (unit.equals("cm")) return num * 10f;
			if (unit.equals("in")) return num * 25.4f;
			if (unit.equals("mm")) return num * 1f;
			if (unit.equals("pc")) return num * 4.2333333333333333f;
			if (unit.equals("pt")) return num * 0.35277777777777777f;
			if (unit.equals("px")) return num * 0.2645833333333333f;
			if (unit.equals("Q")) return num * 0.25f;
			if (unit.equals("u")) return num * 19.05f;
			if (unit.equals("v")) return num * 4.7625f;
			if (unit.equals("w")) return num * 0.9525f;
		}
		throw new NumberFormatException(s);
	}
	
	private static Padding parsePaddingMM(String s) {
		float top, left, bottom, right;
		String[] vs = s.trim().split("(\\s|,)+");
		switch (vs.length) {
			case 1:
				top = left = bottom = right = parseLengthMM(vs[0]);
				break;
			case 2:
				top = bottom = parseLengthMM(vs[0]);
				left = right = parseLengthMM(vs[1]);
				break;
			case 3:
				top = parseLengthMM(vs[0]);
				left = right = parseLengthMM(vs[1]);
				bottom = parseLengthMM(vs[2]);
				break;
			case 4:
				top = parseLengthMM(vs[0]);
				right = parseLengthMM(vs[1]);
				bottom = parseLengthMM(vs[2]);
				left = parseLengthMM(vs[3]);
				break;
			default:
				throw new NumberFormatException(s);
		}
		return new Padding(top, left, bottom, right);
	}
}
