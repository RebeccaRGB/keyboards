package com.kreative.keycaps;

import static com.kreative.keycaps.StringUtilities.quote;
import static com.kreative.keycaps.StringUtilities.toCodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCapLegendItem {
	public static final String TEXT_WINDOWS = "\uF810";
	public static final String TEXT_CONTEXT_MENU = "\uF811";
	public static final String TEXT_OPEN_APPLE = "\uF812";
	public static final String TEXT_SOLID_APPLE = "\uF813";
	public static final String TEXT_ATARI = "\uF820";
	public static final String TEXT_COMMODORE = "\uF821";
	public static final String TEXT_SOLID_AMIGA = "\uF822";
	public static final String TEXT_OPEN_AMIGA = "\uF823";
	public static final String TEXT_LEAP_BACKWARD = "\uF824";
	public static final String TEXT_LEAP_FORWARD = "\uF825";
	public static final String TEXT_GOOGLE_ASSISTANT = "\uF826";
	public static final String TEXT_RASPBERRY_PI = "\uF827";
	public static final String TEXT_LISA_LEFT = "\uDBBF\uDC18";
	public static final String TEXT_LISA_UP = "\uDBBF\uDC19";
	public static final String TEXT_LISA_RIGHT = "\uDBBF\uDC1A";
	public static final String TEXT_LISA_DOWN = "\uDBBF\uDC1B";
	
	public static final String PATH_WINDOWS = "M 0 0 h 0.45 v 0.45 h -0.45 z M 1 0 v 0.45 h -0.45 v -0.45 z M 1 1 h -0.45 v -0.45 h 0.45 z M 0 1 v -0.45 h 0.45 v 0.45 z";
	public static final String PATH_CONTEXT_MENU = "M 0 0 V 1 H 1 V 0 Z M 0.9 0.9 H 0.1 V 0.1 H 0.9 Z M 0.7 0.35 H 0.3 V 0.25 H 0.7 Z M 0.7 0.55 H 0.3 V 0.45 H 0.7 Z M 0.7 0.75 H 0.3 V 0.65 H 0.7 Z";
	public static final String PATH_OPEN_APPLE = "M 0.363 1 C 0.29 1 0.224 0.946 0.162 0.834 c -0.057 -0.1 -0.085 -0.2 -0.085 -0.297 c 0 -0.093 0.024 -0.171 0.07 -0.231 C 0.196 0.242 0.26 0.209 0.336 0.209 c 0.031 0 0.067 0.006 0.108 0.019 c 0.001 0 0.002 0 0.003 0.001 C 0.453 0.181 0.469 0.138 0.495 0.1 c 0.034 -0.049 0.089 -0.083 0.164 -0.099 c 0.021 -0.005 0.044 0.006 0.052 0.027 c 0.004 0.01 0.006 0.02 0.006 0.029 c 0 0.003 0.001 0.007 0.001 0.011 c 0 0.028 -0.007 0.059 -0.02 0.091 C 0.693 0.175 0.685 0.19 0.675 0.205 c 0.054 0 0.102 0.014 0.146 0.043 c 0.025 0.019 0.047 0.04 0.067 0.064 C 0.903 0.332 0.9 0.359 0.881 0.375 C 0.849 0.403 0.834 0.42 0.828 0.43 c -0.02 0.028 -0.029 0.056 -0.029 0.087 c 0 0.035 0.01 0.067 0.03 0.096 c 0.019 0.027 0.039 0.044 0.062 0.051 c 0.011 0.003 0.021 0.012 0.027 0.022 c 0.005 0.01 0.007 0.023 0.003 0.035 C 0.907 0.77 0.882 0.819 0.849 0.87 C 0.792 0.956 0.733 0.998 0.67 0.998 c -0.024 0 -0.053 -0.006 -0.091 -0.02 C 0.545 0.965 0.524 0.963 0.512 0.963 c -0.017 0 -0.038 0.005 -0.06 0.015 C 0.416 0.993 0.388 1 0.363 1 z M 0.336 0.3 c -0.047 0 -0.085 0.02 -0.117 0.061 C 0.185 0.406 0.168 0.465 0.168 0.538 c 0 0.082 0.024 0.167 0.073 0.252 c 0.043 0.078 0.085 0.119 0.122 0.119 c 0.008 0 0.024 -0.002 0.054 -0.015 C 0.483 0.865 0.536 0.865 0.61 0.893 c 0.034 0.012 0.051 0.014 0.06 0.014 c 0.038 0 0.077 -0.047 0.104 -0.087 c 0.02 -0.03 0.036 -0.059 0.047 -0.088 C 0.796 0.716 0.773 0.694 0.754 0.666 c -0.03 -0.044 -0.046 -0.094 -0.046 -0.148 c 0 -0.05 0.015 -0.096 0.044 -0.139 c 0.008 -0.013 0.02 -0.026 0.035 -0.041 C 0.781 0.333 0.775 0.327 0.768 0.322 c -0.042 -0.028 -0.087 -0.038 -0.169 -0.01 C 0.556 0.327 0.527 0.333 0.506 0.333 c -0.011 0 -0.033 -0.002 -0.087 -0.019 C 0.386 0.305 0.358 0.3 0.336 0.3 z M 0.62 0.11 C 0.598 0.121 0.582 0.135 0.57 0.152 C 0.557 0.169 0.548 0.188 0.543 0.209 c 0.009 -0.005 0.02 -0.014 0.032 -0.024 c 0.018 -0.018 0.031 -0.038 0.04 -0.059 C 0.617 0.121 0.619 0.115 0.62 0.11 z";
	public static final String PATH_SOLID_APPLE = "M 0.424 0.249 c 0.041 0.012 0.068 0.018 0.082 0.018 c 0.018 0 0.046 -0.007 0.086 -0.021 s 0.074 -0.021 0.104 -0.021 c 0.048 0 0.091 0.013 0.129 0.039 C 0.845 0.28 0.867 0.3 0.887 0.325 c -0.031 0.026 -0.055 0.05 -0.068 0.071 c -0.026 0.038 -0.04 0.079 -0.04 0.124 c 0 0.05 0.015 0.095 0.042 0.134 c 0.027 0.04 0.06 0.065 0.095 0.076 C 0.901 0.778 0.876 0.828 0.842 0.88 C 0.79 0.959 0.739 0.998 0.687 0.998 c -0.021 0 -0.048 -0.006 -0.084 -0.019 c -0.035 -0.013 -0.065 -0.02 -0.09 -0.02 S 0.459 0.966 0.428 0.98 C 0.396 0.993 0.37 1 0.35 1 C 0.289 1 0.23 0.948 0.172 0.844 C 0.114 0.742 0.084 0.641 0.084 0.542 c 0 -0.092 0.022 -0.167 0.067 -0.225 C 0.196 0.26 0.252 0.231 0.319 0.231 C 0.348 0.231 0.383 0.237 0.424 0.249 z M 0.689 0.014 c 0 0.004 0.001 0.008 0.001 0.012 c 0 0.025 -0.006 0.052 -0.018 0.082 C 0.661 0.137 0.642 0.165 0.617 0.19 C 0.594 0.211 0.573 0.226 0.551 0.233 C 0.538 0.237 0.517 0.241 0.49 0.243 c 0 -0.06 0.016 -0.111 0.046 -0.154 C 0.566 0.046 0.616 0.016 0.686 0 C 0.688 0.005 0.689 0.01 0.689 0.014 z";
	public static final String PATH_COMMODORE = "M 0.571 0.536 h 0.214 L 1 0.75 H 0.571 V 0.536 z M 0.571 0.464 V 0.25 H 1 L 0.786 0.464 H 0.571 z M 0 0.5 C 0 0.223 0.223 0 0.5 0 c 0.025 0 0.048 0.002 0.071 0.006 v 0.217 C 0.548 0.218 0.525 0.214 0.5 0.214 c -0.157 0 -0.286 0.128 -0.286 0.286 S 0.343 0.786 0.5 0.786 c 0.025 0 0.048 -0.004 0.071 -0.009 v 0.217 C 0.548 0.998 0.525 1 0.5 1 C 0.223 1 0 0.777 0 0.5 z";
	public static final String PATH_SOLID_AMIGA = "M 0.654 0.414 l -0.24 0.24 h 0.24 V 0.414 z M -0.039 1 V 0.923 h 0.077 L 0.961 0 v 0.923 h 0.078 V 1 H 0.577 V 0.923 h 0.077 V 0.73 H 0.336 L 0.144 0.923 H 0.23 V 1 H -0.039 z";
	public static final String PATH_OPEN_AMIGA = "M 0.885 0.185 L 0.731 0.338 v 0.585 h 0.154 V 0.185 z M 0.654 0.414 l -0.24 0.24 h 0.24 V 0.414 z M -0.039 1 V 0.923 h 0.077 L 0.961 0 v 0.923 h 0.078 V 1 H 0.577 V 0.923 h 0.077 V 0.73 H 0.336 L 0.144 0.923 h 0.087 V 1 H -0.039 z";
	public static final String PATH_LISA_LEFT = "M 1 0.188 v 0.623 C 1 0.83 0.985 0.844 0.967 0.844 H 0.033 C 0.015 0.844 0 0.83 0 0.812 V 0.656 c 0 -0.018 0.015 -0.033 0.033 -0.033 s 0.033 0.015 0.033 0.033 v 0.123 h 0.868 V 0.222 H 0.066 v 0.123 c 0 0.018 -0.015 0.033 -0.033 0.033 S 0 0.362 0 0.344 V 0.188 C 0 0.17 0.015 0.156 0.033 0.156 h 0.935 C 0.985 0.156 1 0.17 1 0.188 z M 0.595 0.688 c 0.003 0 0.006 0.001 0.009 0.001 c 0.007 0 0.014 -0.002 0.02 -0.007 c 0.008 -0.006 0.013 -0.016 0.013 -0.026 V 0.344 c 0 -0.01 -0.005 -0.02 -0.013 -0.026 C 0.616 0.312 0.605 0.31 0.595 0.313 L 0.024 0.468 C 0.01 0.472 0 0.485 0 0.5 s 0.01 0.028 0.024 0.032 L 0.595 0.688 z";
	public static final String PATH_LISA_UP = "M 1 0.188 v 0.623 c 0 0.018 -0.015 0.033 -0.033 0.033 H 0.033 C 0.015 0.845 0 0.829 0 0.812 V 0.188 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.312 c 0.018 0 0.033 0.016 0.033 0.033 c 0 0.019 -0.015 0.033 -0.033 0.033 H 0.066 v 0.557 h 0.868 V 0.222 H 0.656 c -0.018 0 -0.033 -0.015 -0.033 -0.033 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.312 C 0.985 0.155 1 0.171 1 0.188 z M 0.317 0.675 c 0.006 0.009 0.016 0.014 0.027 0.014 h 0.312 h 0 c 0.018 0 0.033 -0.015 0.033 -0.033 c 0 -0.005 -0.001 -0.011 -0.004 -0.015 L 0.531 0.178 C 0.527 0.165 0.514 0.155 0.5 0.155 s -0.027 0.01 -0.031 0.022 L 0.313 0.646 C 0.31 0.655 0.312 0.666 0.317 0.675 z";
	public static final String PATH_LISA_RIGHT = "M 1 0.188 v 0.156 c 0 0.018 -0.015 0.033 -0.033 0.033 c -0.018 0 -0.032 -0.015 -0.032 -0.033 V 0.222 H 0.065 v 0.557 h 0.869 V 0.656 c 0 -0.018 0.015 -0.033 0.032 -0.033 C 0.985 0.623 1 0.638 1 0.656 v 0.156 C 1 0.83 0.985 0.844 0.967 0.844 H 0.033 C 0.015 0.844 0 0.83 0 0.812 V 0.188 C 0 0.17 0.015 0.156 0.033 0.156 h 0.934 C 0.985 0.156 1 0.17 1 0.188 z M 0.405 0.313 c -0.011 -0.002 -0.021 0 -0.029 0.006 C 0.368 0.324 0.363 0.334 0.363 0.344 v 0.312 c 0 0.01 0.005 0.02 0.013 0.026 c 0.006 0.004 0.013 0.007 0.021 0.007 c 0.003 0 0.006 0 0.009 -0.001 l 0.57 -0.156 C 0.99 0.528 1 0.515 1 0.5 S 0.99 0.472 0.976 0.468 L 0.405 0.313 z";
	public static final String PATH_LISA_DOWN = "M 1 0.188 v 0.623 c 0 0.018 -0.015 0.033 -0.033 0.033 H 0.655 c -0.018 0 -0.032 -0.016 -0.032 -0.033 c 0 -0.019 0.015 -0.033 0.032 -0.033 h 0.279 V 0.222 H 0.065 v 0.557 h 0.279 c 0.018 0 0.032 0.015 0.032 0.033 c 0 0.018 -0.015 0.033 -0.032 0.033 H 0.033 C 0.015 0.845 0 0.829 0 0.812 V 0.188 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.934 C 0.985 0.155 1 0.171 1 0.188 z M 0.683 0.325 C 0.676 0.316 0.666 0.312 0.655 0.312 H 0.345 c -0.011 0 -0.021 0.005 -0.027 0.014 C 0.312 0.334 0.31 0.345 0.313 0.354 l 0.155 0.468 C 0.474 0.835 0.486 0.845 0.5 0.845 s 0.026 -0.01 0.031 -0.022 l 0.155 -0.468 C 0.69 0.345 0.688 0.334 0.683 0.325 z";
	
	private static final String QUOTED_TOKEN_STR = "\'((\\\\.|[^\'])*)\'|\"((\\\\.|[^\"])*)\"|`((\\\\.|[^`])*)`";
	private static final String CODED_TOKEN_STR = "[$]([0-9A-Fa-f]+([+][0-9A-Fa-f]+)*)?";
	public static final String PATTERN_STRING = "([A-Za-z]+)|" + QUOTED_TOKEN_STR + "|" + CODED_TOKEN_STR;
	public static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);
	private static final Pattern PATH_TAG = Pattern.compile("<path\\s+d\\s*=\\s*(\'[^\']*\'|\"[^\"]*\")\\s*/?>");
	
	public static KeyCapLegendItem parse(KeyCapParser p) {
		if (p.hasNextID()) return text(p.next());
		if (p.hasNextQuote('\'')) return text(p.nextQuote('\''));
		if (p.hasNextQuote('\"')) return text(p.nextQuote('\"'));
		if (p.hasNextQuote('`')) return path(p.nextQuote('`'));
		if (p.hasNextCoded()) return text(p.nextCoded());
		throw new IllegalArgumentException();
	}
	
	public static KeyCapLegendItem parse(String s, boolean fallback) {
		try {
			KeyCapParser p = new KeyCapParser(s);
			KeyCapLegendItem item = parse(p);
			if (p.hasNext()) throw new IllegalArgumentException();
			return item;
		} catch (IllegalArgumentException e) {
			return (fallback ? text(s) : null);
		}
	}
	
	public static KeyCapLegendItem text(String text) {
		if (text == null || text.length() == 0) return null;
		Matcher m = PATH_TAG.matcher(text);
		if (m.matches()) {
			String path = m.group(1).substring(1, m.group(1).length() - 1).trim();
			return (path.length() == 0) ? null : new KeyCapLegendItem(text, null, null, path);
		}
		int cpc = text.codePointCount(0, text.length());
		if (cpc != 1) return new KeyCapLegendItem(text, null, text, null);
		switch (text.codePointAt(0)) {
			// These characters are substituted with ones that look nicer.
			case '-': return new KeyCapLegendItem(text, null, "\u2212", null);
			// These private use characters are automatically converted to paths.
			case 0xF810: case 0xFFCE0: return new KeyCapLegendItem(text, null, null, PATH_WINDOWS);
			case 0xF811: case 0xFFCE1: return new KeyCapLegendItem(text, null, null, PATH_CONTEXT_MENU);
			case 0xF812: case 0xFFCE2: return new KeyCapLegendItem(text, null, null, PATH_OPEN_APPLE);
			case 0xF813: case 0xFFCE3: return new KeyCapLegendItem(text, null, null, PATH_SOLID_APPLE);
			case 0xF821: case 0xFFCE5: return new KeyCapLegendItem(text, null, null, PATH_COMMODORE);
			case 0xF822: case 0xFFCE6: return new KeyCapLegendItem(text, null, null, PATH_SOLID_AMIGA);
			case 0xF823: case 0xFFCE7: return new KeyCapLegendItem(text, null, null, PATH_OPEN_AMIGA);
			case 0xFFC18: return new KeyCapLegendItem(text, null, null, PATH_LISA_LEFT);
			case 0xFFC19: return new KeyCapLegendItem(text, null, null, PATH_LISA_UP);
			case 0xFFC1A: return new KeyCapLegendItem(text, null, null, PATH_LISA_RIGHT);
			case 0xFFC1B: return new KeyCapLegendItem(text, null, null, PATH_LISA_DOWN);
			// All other characters are taken verbatim.
			default: return new KeyCapLegendItem(text, null, text, null);
		}
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
		char q = text.contains("\"") ? '\'' : text.contains("\'") ? '\"' : '\'';
		return quote(text, q);
	}
}
