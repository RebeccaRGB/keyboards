package com.kreative.keycaps;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

public final class LayoutToInkbox extends LayoutConverter {
	private static final int IMAGE_SIZE = 188 * 2;
	private static final int TEXT_INSET = 13 * 5;
	private static final int PATH_INSET = 13;
	private static final String DEFAULT_OUTPUT = "A";
	
	public static void main(String[] args) {
		new LayoutToInkbox().mainImpl(args, ".zip");
	}
	
	protected final int parseArg(String[] args, String arg, int argi) {
		return HELP;
	}
	
	protected final void write(OutputStream os, KeyCapLayout layout) throws IOException {
		BufferedImage masterImage = createMasterImage(layout);
		int bx = (masterImage.getWidth() < 2000) ? 5787 : 23;
		HashMap<String,BufferedImage> images = new HashMap<String,BufferedImage>();
		StringBuffer profile = new StringBuffer();
		
		profile.append("{");
		String layoutName = layout.getPropertyMap().getString("name");
		if (layoutName == null) layoutName = "myLayout";
		layoutName = layoutName.replace("\\", "\\\\");
		layoutName = layoutName.replace("\"", "\\\"");
		profile.append("\"layoutName\":\"" + layoutName + "\",");
		profile.append("\"fonts\":[],");
		profile.append("\"globalOffsetX\":0,");
		profile.append("\"globalOffsetY\":0,");
		profile.append("\"backgroundImgPath\":\"\",");
		profile.append("\"backgroundIsVideo\":false,");
		profile.append("\"backgroundColor\":\"#FF00FFFF\",");
		profile.append("\"shaderGlobal\":-1,");
		profile.append("\"pressShaderGlobal\":-1,");
		profile.append("\"shaders\":[],");
		profile.append("\"keys\":[");
		
		int keyID = 0;
		boolean first = true;
		for (KeyCap k : layout) {
			Point2D.Float pos = k.getPosition().getLocation(IMAGE_SIZE);
			Shape shape = k.getShape().toAWTShape(IMAGE_SIZE);
			shape = ShapeUtilities.contract(shape, PATH_INSET);
			shape = ShapeUtilities.translate(shape, pos.x, -pos.y);
			String outputValue = outputValue(k.getPropertyMap().getInteger("usb"));
			int rectID = 0;
			for (;;) {
				Rectangle2D rect = ShapeUtilities.getWidestRect(shape, null);
				if (rect == null || rect.isEmpty()) break;
				
				int ix = (int)Math.round(rect.getMinX());
				int iy = (int)Math.round(masterImage.getHeight() + rect.getMinY());
				int iw = (int)Math.round(rect.getMaxX()) - ix;
				int ih = (int)Math.round(masterImage.getHeight() + rect.getMaxY()) - iy;
				int[] buf = new int[iw * ih];
				masterImage.getRGB(ix, iy, iw, ih, buf, 0, iw);
				boolean isRect = allEqual(buf);
				
				int rx = (int)Math.round((bx + rect.getMinX()) / 2);
				int ry = (int)Math.round((2253 + rect.getMinY()) / 2);
				int rw = (int)Math.round((bx + rect.getMaxX()) / 2) - rx;
				int rh = (int)Math.round((2253 + rect.getMaxY()) / 2) - ry;
				
				profile.append(first ? "{" : ",{");
				profile.append("\"Z\":1,");
				profile.append("\"X\":" + rx + ",");
				profile.append("\"Y\":" + ry + ",");
				profile.append("\"W\":" + rw + ",");
				profile.append("\"H\":" + rh + ",");
				profile.append("\"isRect\":" + isRect + ",");
				profile.append("\"RGBA\":\"" + rgbToString(buf[0]) + "\",");
				profile.append("\"outputType\":\"HID\",");
				profile.append("\"outputValue\":\"" + outputValue + "\",");
				profile.append("\"affectedByCapsLock\":false,");
				profile.append("\"useImage\":" + !isRect + ",");
				if (isRect) {
					profile.append("\"text\":\"\",");
				} else {
					BufferedImage image = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
					image.setRGB(0, 0, iw, ih, buf, 0, iw);
					String name = "k" + keyID + "r" + rectID + ".png";
					images.put(name, image);
					profile.append("\"text\":\"" + name + "\",");
				}
				profile.append("\"font\":-1,");
				profile.append("\"shader\":-1,");
				profile.append("\"pressShader\":-1,");
				profile.append("\"modType\":[\"HID\",\"HID\",\"HID\",\"HID\",\"HID\"],");
				profile.append("\"modText\":[\"\",\"\",\"\",\"\",\"\"],");
				profile.append("\"modShader\":[-1,-1,-1,-1,-1],");
				profile.append("\"modOutputValue\":[\"\",\"\",\"\",\"\",\"\"],");
				profile.append("\"pluginEvent\":-1");
				profile.append("}");
				
				shape = ShapeUtilities.subtract(shape, rect);
				shape = ShapeUtilities.simplify(shape, null);
				first = false;
				rectID++;
			}
			keyID++;
		}
		
		profile.append("],");
		profile.append("\"freeObj\":[],");
		profile.append("\"plugins\":[]");
		profile.append("}");
		
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.putNextEntry(new ZipEntry("fonts/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("images/"));
		zos.closeEntry();
		for (Map.Entry<String,BufferedImage> e : images.entrySet()) {
			zos.putNextEntry(new ZipEntry("images/" + e.getKey()));
			ImageIO.write(e.getValue(), "png", zos);
			zos.closeEntry();
		}
		zos.putNextEntry(new ZipEntry("plugins/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("shaders/"));
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("layout.prof"));
		zos.write(profile.toString().getBytes("UTF-8"));
		zos.closeEntry();
		zos.finish();
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
	
	private LayoutToInkbox() {}
	
	private static BufferedImage createMasterImage(KeyCapLayout layout) {
		FlatKeyCapMold mold = new FlatKeyCapMold(0, TEXT_INSET);
		AWTRenderer renderer = new AWTRenderer(mold, 1, IMAGE_SIZE, null, false);
		Rectangle2D bounds = renderer.getBounds(layout);
		int w = (int)Math.round(bounds.getWidth());
		int h = (int)Math.round(bounds.getHeight());
		bounds = new Rectangle(0, 0, w, h);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.paint(g, layout, bounds, false);
		g.dispose();
		return image;
	}
	
	private static boolean allEqual(int[] a) {
		for (int i = 1; i < a.length; i++) {
			if (a[i] != a[0]) {
				return false;
			}
		}
		return true;
	}
	
	private static String rgbToString(int rgb) {
		String h = "00000000" + Integer.toHexString(rgb).toUpperCase();
		return "#" + h.substring(h.length() - 6) + h.substring(h.length() - 8, h.length() - 6);
	}
	
	private static String outputValue(Integer usb) {
		if (usb != null) {
			int i = usb.intValue();
			if (i >= 0 && i < USB_KEYS.length && USB_KEYS[i] != null) return USB_KEYS[i];
			int j = usb.intValue() - 0xE0;
			if (j >= 0 && j < USB_MODS.length && USB_MODS[j] != null) return USB_MODS[j];
		}
		return DEFAULT_OUTPUT;
	}
	
	private static final String[] USB_KEYS = {
		null, "FN", null, null,
		"A","B","C","D","E","F","G","H","I","J","K","L","M",
		"N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
		"1","2","3","4","5","6","7","8","9","0",
		"ENTER","ESCAPE","BACKSPACE","TAB","SPACE","MINUS","EQUAL",
		"LEFT_BRACKET","RIGHT_BRACKET","BACKSLASH","NONUS_HASH",
		"SEMICOLON","QUOTE","GRAVE","COMMA","DOT","SLASH","CAPS_LOCK",
		"F1","F2","F3","F4","F5","F6","F7","F8","F9","F10","F11","F12",
		"PRINT_SCREEN","SCROLL_LOCK","PAUSE","INSERT","HOME","PAGE_UP",
		"DELETE","END","PAGE_DOWN","RIGHT","LEFT","DOWN","UP","NUM_LOCK",
		"KP_SLASH","KP_ASTERISK","KP_MINUS","KP_PLUS","KP_ENTER",
		"KP_1","KP_2","KP_3","KP_4","KP_5","KP_6","KP_7","KP_8","KP_9","KP_0",
		"KP_DOT","NONUS_BACKSLASH","KC_APPLICATION","KB_POWER","KP_EQUAL",
		"F13","F14","F15","F16","F17","F18","F19","F20","F21","F22","F23","F24",
		"EXECUTE","HELP","MENU","SELECT","STOP",
		"AGAIN","UNDO","CUT","COPY","PASTE","FIND",
		"KB_MUTE","KB_VOLUME_UP","KB_VOLUME_DOWN",
		"LOCKING_CAPS_LOCK","LOCKING_NUM_LOCK","LOCKING_SCROLL_LOCK",
		"KP_COMMA","KP_EQUAL",
		"INTERNATIONAL_1","INTERNATIONAL_2","INTERNATIONAL_3",
		"INTERNATIONAL_4","INTERNATIONAL_5","INTERNATIONAL_6",
		"INTERNATIONAL_7","INTERNATIONAL_8","INTERNATIONAL_9",
		"LANGUAGE_1","LANGUAGE_2","LANGUAGE_3",
		"LANGUAGE_4","LANGUAGE_5","LANGUAGE_6",
		"LANGUAGE_7","LANGUAGE_8","LANGUAGE_9",
		"ALTERNATE_ERASE","SYSTEM_REQUEST","CANCEL","CLEAR","PRIOR",
		"RETURN","SEPARATOR","OUT","OPER","CLEAR_AGAIN","CRSEL","EXSEL"
	};
	
	private static final String[] USB_MODS = {
		"LEFT_CTRL","LEFT_SHIFT","LEFT_ALT","LEFT_GUI",
		"RIGHT_CTRL","RIGHT_SHIFT","RIGHT_ALT","RIGHT_GUI"
	};
}
