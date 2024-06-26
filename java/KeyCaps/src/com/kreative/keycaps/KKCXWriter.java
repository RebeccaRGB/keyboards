package com.kreative.keycaps;

import static com.kreative.keycaps.KeyCapUnits.unitToString;
import static com.kreative.keycaps.KeyCapUnits.valueToString;
import static com.kreative.keycaps.StringUtilities.xmlSpecialChars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class KKCXWriter {
	public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String DOCTYPE_DECLARATION = "<!DOCTYPE keyCapLayout PUBLIC \"-//Kreative//DTD KreativeKeyCaps 1.0//EN\" \"http://www.kreativekorp.com/dtd/kkcx.dtd\">";
	private static final String[] LAYOUT_ATTRIBUTES = {"name", "kbdType", "gestalt", "vs", "cc", "lc", "lh", "a"};
	private static final String[] KEYCAP_ATTRIBUTES = {"usb", "vs", "cc", "lc", "lh", "a"};
	private static final String[] LEGEND_ATTRIBUTES = {"lc", "lh", "a"};
	
	public static void write(File file, KeyCapLayout kcl) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(pw, kcl);
		pw.flush();
		pw.close();
		fos.close();
	}
	
	public static void write(PrintWriter out, KeyCapLayout kcl) {
		out.println(XML_DECLARATION);
		out.println(DOCTYPE_DECLARATION);
		out.print("<keyCapLayout");
		writeAttributes(out, kcl.getPropertyMap(), LAYOUT_ATTRIBUTES);
		out.println(">");
		writeProperties(out, "\t", kcl.getPropertyMap(), LAYOUT_ATTRIBUTES);
		boolean first = true;
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(kcl);
		for (KeyCap cap : kcl) {
			writeKeyCap(out, "\t", cap, (first || !cap.isAt(x, y, keyCapSize)));
			KeyCapPosition pos = cap.getPosition();
			KeyCapShape shape = cap.getShape();
			keyCapSize = pos.getKeyCapSize();
			y = pos.getY(keyCapSize);
			x = pos.getX(keyCapSize) + shape.getAdvanceWidth(keyCapSize);
			first = false;
		}
		out.println("</keyCapLayout>");
	}
	
	private static void writeKeyCap(PrintWriter out, String prefix, KeyCap cap, boolean includePos) {
		out.print(prefix);
		out.print("<k");
		if (includePos) {
			KeyCapPosition pos = cap.getPosition();
			float keyCapSize = pos.getKeyCapSize();
			String xs = valueToString(pos.getX(keyCapSize)) + unitToString(keyCapSize);
			String ys = valueToString(pos.getY(keyCapSize)) + unitToString(keyCapSize);
			out.print(" x=\""); out.print(xmlSpecialChars(xs)); out.print("\"");
			out.print(" y=\""); out.print(xmlSpecialChars(ys)); out.print("\"");
		}
		String shape = cap.getShape().toString();
		if (shape.length() > 0) {
			out.print(" shape=\"");
			out.print(xmlSpecialChars(shape));
			out.print("\"");
		}
		KeyCapLegend legend = cap.getLegend();
		KeyCapLegend.Type type = legend.getExplicitType();
		if (type == null) {
			type = legend.getImpliedType();
		} else {
			out.print(" type=\"");
			out.print(xmlSpecialChars(type.toString()));
			out.print("\"");
		}
		PropertyMap props = new PropertyMap();
		props.putAll(cap.getPropertyMap());
		props.putAll(legend.getPropertyMap());
		writeAttributes(out, props, KEYCAP_ATTRIBUTES);
		if (inlineKeyCapChildren(cap)) {
			out.print(">");
			writeProperties(out, null, props, KEYCAP_ATTRIBUTES);
			List<KeyCapLegendItem> items = legend.getItemList(type);
			if (items != null) {
				String text = singleKeyCapTextChild(items);
				if (text != null) {
					out.print(xmlSpecialChars(text));
				} else {
					for (KeyCapLegendItem item : items) {
						writeLegendItem(out, null, null, item);
					}
				}
			} else {
				for (String key : sorted(legend.keySet())) {
					writeLegendItem(out, null, key, legend.get(key));
				}
			}
			out.println("</k>");
		} else {
			out.println(">");
			String cprefix = prefix + "\t";
			writeProperties(out, cprefix, props, KEYCAP_ATTRIBUTES);
			List<KeyCapLegendItem> items = legend.getItemList(type);
			if (items != null) {
				for (KeyCapLegendItem item : items) {
					writeLegendItem(out, cprefix, null, item);
				}
			} else {
				for (String key : sorted(legend.keySet())) {
					writeLegendItem(out, cprefix, key, legend.get(key));
				}
			}
			out.print(prefix);
			out.println("</k>");
		}
	}
	
	private static boolean inlineKeyCapChildren(KeyCap cap) {
		KeyCapLegend legend = cap.getLegend();
		if (legend.size() > 4) return false;
		HashSet<String> propertyKeys = new HashSet<String>();
		propertyKeys.addAll(cap.getPropertyMap().keySet());
		propertyKeys.addAll(legend.getPropertyMap().keySet());
		propertyKeys.removeAll(Arrays.asList(KEYCAP_ATTRIBUTES));
		if (propertyKeys.size() > 0) return false;
		for (KeyCapLegendItem item : legend.values()) {
			if (!inlineLegendItemChildren(item)) return false;
			if (legend.size() > 1) {
				String path = item.getRawPath();
				if (path != null && path.length() > 0) return false;
			}
		}
		return true;
	}
	
	private static String singleKeyCapTextChild(List<KeyCapLegendItem> items) {
		if (items.size() == 1) {
			for (KeyCapLegendItem item : items) {
				if (item != null) {
					String path = item.getRawPath();
					if (path != null && path.length() > 0) return null;
					String text = item.getRawText();
					if (text != null && text.length() > 0) return text;
				}
			}
		}
		return null;
	}
	
	private static void writeLegendItem(PrintWriter out, String prefix, String key, KeyCapLegendItem value) {
		if (prefix != null) out.print(prefix);
		out.print("<l");
		if (key != null && key.length() > 0) {
			out.print(" type=\"");
			out.print(xmlSpecialChars(key));
			out.print("\"");
		}
		if (value == null) {
			out.print("></l>");
			if (prefix != null) out.println();
			return;
		}
		String path = value.getRawPath();
		if (path != null && path.length() > 0) {
			out.print(" path=\"");
			out.print(xmlSpecialChars(path));
			out.print("\"");
		}
		writeAttributes(out, value.getPropertyMap(), LEGEND_ATTRIBUTES);
		if (prefix == null || inlineLegendItemChildren(value)) {
			out.print(">");
			writeProperties(out, null, value.getPropertyMap(), LEGEND_ATTRIBUTES);
			String text = value.getRawText();
			if (text != null && text.length() > 0) {
				out.print(xmlSpecialChars(text));
			}
			out.print("</l>");
			if (prefix != null) out.println();
		} else {
			out.println(">");
			String cprefix = prefix + "\t";
			writeProperties(out, cprefix, value.getPropertyMap(), LEGEND_ATTRIBUTES);
			String text = value.getRawText();
			if (text != null && text.length() > 0) {
				out.print(cprefix);
				out.println(xmlSpecialChars(text));
			}
			out.print(prefix);
			out.println("</l>");
		}
	}
	
	private static boolean inlineLegendItemChildren(KeyCapLegendItem item) {
		HashSet<String> propertyKeys = new HashSet<String>();
		propertyKeys.addAll(item.getPropertyMap().keySet());
		propertyKeys.removeAll(Arrays.asList(LEGEND_ATTRIBUTES));
		return propertyKeys.isEmpty();
	}
	
	private static void writeAttributes(PrintWriter out, PropertyMap props, String... keys) {
		for (String key : keys) {
			String value = props.getString(key);
			if (value != null) {
				out.print(" ");
				out.print(key);
				out.print("=\"");
				out.print(xmlSpecialChars(value));
				out.print("\"");
			}
		}
	}
	
	private static void writeProperties(PrintWriter out, String prefix, PropertyMap props, String... keys) {
		List<String> attributeKeys = Arrays.asList(keys);
		for (String key : sorted(props.keySet())) {
			if (attributeKeys.contains(key)) continue;
			String value = props.getString(key);
			if (value != null) {
				if (prefix != null) out.print(prefix);
				out.print("<p k=\"");
				out.print(xmlSpecialChars(key));
				out.print("\" v=\"");
				out.print(xmlSpecialChars(value));
				out.print("\"/>");
				if (prefix != null) out.println();
			}
		}
	}
	
	private static String[] sorted(Collection<String> c) {
		String[] a = c.toArray(new String[c.size()]);
		Arrays.sort(a);
		return a;
	}
	
	private KKCXWriter() {}
}
