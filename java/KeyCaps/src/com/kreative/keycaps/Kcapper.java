package com.kreative.keycaps;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Reads the contents of a Mac OS Classic KCAP resource and renders the represented layout as an SVG file.
 * Using KbitKeyCapMold, the resulting image will appear similar to the Key Caps desk accessory.
 */
public class Kcapper {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File file = new File(arg);
			int id = 0;
			try { id = Integer.parseInt(file.getName().split("\\D")[0]); }
			catch (Exception e) {}
			InputStream in = new FileInputStream(file);
			String svg = kcapToSVG(in, new KbitKeyCapMold(), 1, id);
			System.out.println(svg.trim());
			in.close();
		}
	}
	
	public static String kcapToSVG(InputStream input, KeyCapMold mold, float scale, int id) throws IOException {
		StringBuffer defs = new StringBuffer();
		StringBuffer keyboard = new StringBuffer();
		DataInputStream in = new DataInputStream(input);
		int kt = in.readShort();
		int kl = in.readShort();
		int kb = in.readShort();
		int kr = in.readShort();
		int tt = in.readShort();
		int tl = in.readShort();
		int tb = in.readShort();
		int tr = in.readShort();
		int sc = in.readShort();
		for (int i = 0; i < sc; i++) {
			List<Point> points = new ArrayList<Point>();
			points.add(new Point(0, 0));
			int pc = in.readShort() + 1;
			for (int j = 0; j < pc; j++) {
				int y = in.readShort();
				int x = in.readShort();
				points.add(new Point(x, y));
			}
			if (mold == null) {
				Shape shape = ShapeUtilities.contract(makeKeyShape(points), 0.5f);
				String path = ShapeUtilities.toSVGPath(shape, null, 1000);
				defs.append("<path id=\"shape" + i + "\" d=\"" + path + "\" fill=\"white\" stroke=\"black\"/>\n");
			} else {
				AffineTransform tx = AffineTransform.getScaleInstance(1/scale, 1/scale);
				Shape shape = tx.createTransformedShape(makeKeyShape(points));
				String paths = mold.createLayeredObject(shape, null, null).toSVG("", "");
				defs.append("<g id=\"shape" + i + "\">\n" + paths + "\n</g>\n");
			}
			int kc = in.readShort() + 1;
			for (int k = 0; k < kc; k++) {
				int code = in.readUnsignedShort();
				int oy = in.readShort();
				int ox = in.readShort();
				for (Point p : points) {
					p.y += oy;
					p.x += ox;
				}
				keyboard.append("<g class=\"key keyCode" + code + "\">\n");
				String tx = "translate(" + points.get(0).x + " " + points.get(0).y + ")";
				if (mold != null && scale != 1) tx += " scale(" + scale + " " + scale + ")";
				keyboard.append("<use xlink:href=\"#shape" + i + "\" transform=\"" + tx + "\"/>\n");
				if (KEY_LABELS[code & 0x7F] != null) {
					Shape shape = makeKeyShape(points);
					Rectangle r = shape.getBounds();
					float tx0 = r.x, tx1 = r.x + r.width;
					float ty = Math.max(r.y + r.height - 5, r.y + r.height * 0.5f + 2);
					while (!shape.contains(tx0, ty)) tx0++;
					while (!shape.contains(tx1, ty)) tx1--;
					String[] lines = KEY_LABELS[code & 0x7F].split(" ");
					for (int l = 0, n = lines.length; l < n; l++) {
						keyboard.append("<text x=\"" + ((tx0+tx1+1)/2) + "\" y=\"" + (ty-(n-l-1)*7) + "\" text-anchor=\"middle\" font-family=\"Arial\" font-size=\"6\">" + lines[l] + "</text>\n");
					}
				}
				keyboard.append("</g>\n");
			}
		}
		StringBuffer svg = new StringBuffer();
		svg.append("<?xml version=\"1.0\"?>\n");
		svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\" width=\"" + (kr-kl)*2 + "\" height=\"" + (kb-kt)*2 + "\" viewBox=\"0 0 " + (kr-kl) + " " + (kb-kt) + "\">\n");
		svg.append("<defs>\n");
		svg.append(defs.toString());
		svg.append("</defs>\n");
		Shape textAreaShape = ShapeUtilities.contract(makeRect(tl, tt, tr, tb), 1);
		String textAreaPath = ShapeUtilities.toSVGPath(textAreaShape, null, 1000);
		svg.append("<path class=\"textarea\" d=\"" + textAreaPath + "\" fill=\"white\" stroke=\"black\" stroke-width=\"2\"/>\n");
		String name = keyboardName(id);
		if (name != null) {
			svg.append("<text class=\"keyboardName\" x=\"" + (tl+4) + "\" y=\"" + ((tt+tb)/2+4) + "\" font-family=\"Arial\" font-size=\"12\">" + name + "</text>");
		}
		svg.append("<g class=\"keyboard keyboardId" + id + "\">\n");
		svg.append(keyboard.toString());
		svg.append("</g>\n");
		svg.append("</svg>\n");
		return svg.toString();
	}
	
	private static Rectangle makeRect(int x0, int y0, int x1, int y1) {
		return new Rectangle(
			Math.min(x0,x1), Math.min(y0,y1),
			Math.abs(x0-x1), Math.abs(y0-y1)
		);
	}
	
	private static Rectangle makeRect(Point p0, Point p1) {
		return makeRect(p0.x, p0.y, p1.x, p1.y);
	}
	
	private static Shape makeKeyShape(List<Point> points) {
		Area a = new Area();
		for (int i = 1, n = points.size(); i < n; i++) {
			Rectangle r = makeRect(points.get(i-1), points.get(i));
			a.add(new Area(r));
		}
		return a;
	}
	
	private static final String[] KEY_LABELS = {
		"A", "S", "D", "F", "H", "G", "Z", "X", "C", "V", "&#177; &#167;", "B",
		"Q", "W", "E", "R", "Y", "T", "! 1", "@ 2", "# 3", "$ 4", "^ 6", "% 5",
		"+ =", "( 9", "&amp; 7", "_ -", "* 8", ") 0", "} ]", "O", "U", "{ [",
		"I", "P", "return", "L", "J", "\" \'", "K", ": ;", "| \\", "&lt; ,",
		"? /", "N", "M", "&gt; .", "tab", null, "~ `", "delete", "enter", "esc",
		"$36", "&#8984;", "shift", "caps lock", "option", "control", "shift",
		"option", "control", "fn", "F17", ".", "&#8594; *", "*", "$44", "+",
		"&#8592; +", "clear", "&#8595; ,", "$49", "$4A", "/", "enter", "&#8593; /",
		"-", "F18", "F19", "=", "0", "1", "2", "3", "4", "5", "6", "7", "F20",
		"8", "9", "| &#165;", "_", ",", "F5", "F6", "F7", "F3", "F8", "F9",
		"eisu", "F11", "kana", "F13", "F16", "F14", "$6C", "F10", "$6E", "F12",
		"$70", "F15", "help", "home", "page up", "&#8998;", "F4", "end", "F2",
		"page down", "F1", "&#8592;", "&#8594;", "&#8595;", "&#8593;", "&#9665;"
	};
	
	public static String keyboardName(int id) {
		switch (id) {
			// This only starts matching the 'kbd ' gestalt selector at about 20.
			case 1: return "Apple Standard Keyboard, ANSI"; // gestalt id 5
			case 2: return "Apple Extended Keyboard, ANSI"; // gestalt id 4
			case 3: return "Macintosh 512K Keyboard, ANSI"; // gestalt id 1 (2 with keypad)
			case 4: return "Apple Standard Keyboard, ISO"; // gestalt id 8
			case 5: return "Apple Extended Keyboard, ISO"; // gestalt id 9
			case 6: return "Macintosh Portable Keyboard, ANSI"; // gestalt id 6
			case 7: return "Macintosh Portable Keyboard, ISO"; // gestalt id 7
			case 8: return "Apple Keyboard II, ANSI"; // gestalt id 10
			case 9: return "Apple Keyboard II, ISO"; // gestalt id 11
			case 10: return null; // unknown; appears identical to 12
			case 11: return "Macintosh Plus Keyboard"; // gestalt id 3
			case 12: return "Macintosh PowerBook Keyboard, ANSI"; // gestalt id 12
			case 13: return "Macintosh PowerBook Keyboard, ISO"; // gestalt id 13
			case 14: return "Apple Adjustable Keyboard, Keypad"; // gestalt id 14
			case 16: return "Apple Adjustable Keyboard, ANSI"; // gestalt id 15
			case 17: return "Apple Adjustable Keyboard, ISO"; // gestalt id 16
			// ????: return "Apple Adjustable Keyboard, JIS"; // gestalt id 17
			case 20: return "PowerBook Extended Keyboard, ISO";
			case 21: return "PowerBook Extended Keyboard, JIS";
			case 24: return "PowerBook Extended Keyboard, ANSI";
			case 27: return "PS/2 Keyboard";
			case 28: return "PowerBook Subnote Keyboard, ANSI";
			case 29: return "PowerBook Subnote Keyboard, ISO";
			case 30: return "PowerBook Subnote Keyboard, JIS";
			case 31: return "Apple Pro Keyboard with F16, ANSI";
			case 32: return "Apple Pro Keyboard with F16, ISO";
			case 33: return "Apple Pro Keyboard with F16, JIS";
			case 34: return "Apple USB Pro Keyboard with F16, ANSI";
			case 35: return "Apple USB Pro Keyboard with F16, ISO";
			case 36: return "Apple USB Pro Keyboard with F16, JIS";
			case 37: return "PowerBook Internal USB Keyboard, ANSI";
			case 38: return "PowerBook Internal USB Keyboard, ISO";
			case 39: return "PowerBook Internal USB Keyboard, JIS";
			case 40: return "Third-Party Keyboard, ANSI"; // Mac OS X Tiger and later
			case 41: return "Third-Party Keyboard, ISO"; // Mac OS X Tiger and later
			case 42: return "Third-Party Keyboard, JIS"; // Mac OS X Tiger and later
			case 195: return "PowerBook G3 Keyboard, ANSI";
			case 196: return "PowerBook G3 Keyboard, ISO";
			case 197: return "PowerBook G3 Keyboard, JIS";
			case 198: return "Apple USB Keyboard, ANSI";
			case 199: return "Apple USB Keyboard, ISO";
			case 200: return "Apple USB Keyboard, JIS";
			case 201: return "PowerBook 1999 Keyboard, JIS";
			case 202: return "PowerBook and iBook Keyboard, ANSI";
			case 203: return "PowerBook and iBook Keyboard, ISO";
			case 204: return "Apple USB Pro Keyboard, ANSI";
			case 205: return "Apple USB Pro Keyboard, ISO";
			case 206: return "Apple USB Pro Keyboard, JIS";
			case 207: return "PowerBook and iBook Keyboard, JIS";
			case 259: return "Macintosh 512K Keyboard, ISO"; // gestalt id 1 (2 with keypad)
			default: return null;
		}
	}
}
