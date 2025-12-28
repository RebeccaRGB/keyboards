package com.kreative.keycaps;

import static com.kreative.keycaps.StringUtilities.quote;
import static com.kreative.keycaps.StringUtilities.toCodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCapLegendItem {
	public static KeyCapLegendItem parse(KeyCapParser p) {
		if (p.hasNextID()) return text(p.next());
		if (p.hasNextQuote('\'')) return text(p.nextQuote('\''));
		if (p.hasNextQuote('\"')) return text(p.nextQuote('\"'));
		if (p.hasNextQuote('`')) return path(p.nextQuote('`'));
		if (p.hasNextCoded()) return text(p.nextCoded());
		throw p.expected("legend item");
	}
	
	public static KeyCapLegendItem parse(String s, boolean fallback) {
		try {
			KeyCapParser p = new KeyCapParser(s);
			KeyCapLegendItem item = parse(p);
			p.expectEnd();
			return item;
		} catch (IllegalArgumentException e) {
			return (fallback ? text(s) : null);
		}
	}
	
	private static final Pattern PATH_TAG = Pattern.compile("<path\\s+d\\s*=\\s*(\'[^\']*\'|\"[^\"]*\")\\s*/?>");
	private static final KeyCapLegendPaths PATH_MAP = new KeyCapLegendPaths();
	
	public static KeyCapLegendItem text(String text) {
		if (text == null || text.length() == 0) return null;
		Matcher m = PATH_TAG.matcher(text);
		if (m.matches()) {
			String path = m.group(1).substring(1, m.group(1).length() - 1).trim();
			return (path.length() == 0) ? null : new KeyCapLegendItem(text, null, null, path);
		}
		// These characters are substituted with ones that look nicer.
		if (text.equals("-")) return new KeyCapLegendItem(text, null, "\u2212", null);
		// These characters are automatically converted to paths.
		if (text.codePointCount(0, text.length()) == 1) {
			String path = PATH_MAP.get(text.codePointAt(0));
			if (path != null) return new KeyCapLegendItem(text, null, null, path);
		}
		// All other characters are taken verbatim.
		return new KeyCapLegendItem(text, null, text, null);
	}
	
	public static KeyCapLegendItem path(String path) {
		if (path == null || path.trim().length() == 0) return null;
		return new KeyCapLegendItem(null, path, null, path.trim());
	}
	
	private final PropertyMap props;
	private final String rawText;
	private final String rawPath;
	private final String text;
	private final String path;
	
	private KeyCapLegendItem(String rawText, String rawPath, String text, String path) {
		this.props = new PropertyMap();
		this.rawText = rawText;
		this.rawPath = rawPath;
		this.text = text;
		this.path = path;
	}
	
	public PropertyMap getPropertyMap() { return this.props; }
	public String getRawText() { return this.rawText; }
	public String getRawPath() { return this.rawPath; }
	public String getText() { return this.text; }
	public String getPath() { return this.path; }
	
	public boolean isImpliedLetterOrSymbol() {
		if (text == null || text.length() == 0) return false;
		int cpc = text.codePointCount(0, text.length());
		if (cpc != 1) return false;
		int ch = text.codePointAt(0);
		// These ranges typically label function or modifier keys, not letter or symbol keys.
		if (ch >= 0x2190 && ch <= 0x21FF) return false; // Arrows
		if (ch >= 0x2300 && ch <= 0x23FF) return false; // Miscellaneous Technical
		if (ch >= 0xF810 && ch <= 0xF813) return false; // Linux PUA keyboard symbols
		if (ch >= 0xF820 && ch <= 0xF827) return false; // Kreative PUA keyboard symbols
		if (ch >= 0xFFC18 && ch <= 0xFFC1B) return false; // SLC Appendix keyboard symbols
		if (ch >= 0xFFCE0 && ch <= 0xFFCEF) return false; // SLC Appendix keyboard symbols
		return true;
	}
	
	public String toString() {
		return format(rawText, rawPath);
	}
	
	private static String format(String text, String path) {
		if (path != null && path.length() > 0) return quote(path, '`');
		if (text == null || text.length() == 0) return "$";
		if (text.codePointCount(0, text.length()) == 1) {
			int ch = text.codePointAt(0);
			if (ch < 0x20 || ch >= 0x7F) {
				return toCodes(text);
			}
		}
		if (!text.contains("\'")) return quote(text, '\'');
		if (!text.contains("\"")) return quote(text, '\"');
		return quote(text, '\'');
	}
}
