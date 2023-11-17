package com.kreative.keycaps;

public class KeyCapParser {
	private final String s;
	private final int n;
	private int i, j;
	
	public KeyCapParser(String s) {
		this.s = s;
		this.n = s.length();
		this.i = this.j = 0;
	}
	
	private int skipSpaces(int i) {
		while (i < n) {
			int ch = s.codePointAt(i);
			if (!Character.isWhitespace(ch)) break;
			i += Character.charCount(ch);
		}
		return i;
	}
	
	private int skipLetters(int i) {
		while (i < n) {
			int ch = s.codePointAt(i);
			if (!Character.isLetter(ch)) break;
			i += Character.charCount(ch);
		}
		return i;
	}
	
	private int skipDigits(int i) {
		while (i < n) {
			int ch = s.codePointAt(i);
			if (!Character.isDigit(ch)) break;
			i += Character.charCount(ch);
		}
		return i;
	}
	
	private int skipDigits(int i, int radix) {
		while (i < n) {
			int ch = s.codePointAt(i);
			if (Character.digit(ch, radix) < 0) break;
			i += Character.charCount(ch);
		}
		return i;
	}
	
	private int skipChar(int i, int... chars) {
		if (i < n) {
			int ch = s.codePointAt(i);
			for (int want : chars) {
				if (ch == want) {
					i += Character.charCount(ch);
					break;
				}
			}
		}
		return i;
	}
	
	private int skipQuote(int i, int lquo, int rquo, int esc) {
		if (i < n) {
			int ch = s.codePointAt(i);
			if (ch == lquo) {
				i += Character.charCount(ch);
				while (i < n) {
					ch = s.codePointAt(i);
					i += Character.charCount(ch);
					if (ch == rquo) break;
					if (ch == esc && i < n) {
						ch = s.codePointAt(i);
						i += Character.charCount(ch);
					}
				}
			}
		}
		return i;
	}
	
	private int skipFloat(int i) {
		int i1 = skipChar(i, '+', '-');
		int i2 = skipDigits(i1);
		int i3 = skipChar(i2, '.');
		int i4 = skipDigits(i3);
		if (i4 == i3 && i2 == i1) return i;
		int i5 = skipChar(i4, 'E', 'e');
		if (i5 == i4) return i4;
		int i6 = skipChar(i5, '+', '-');
		int i7 = skipDigits(i6);
		if (i7 == i6) return i4;
		return i7;
	}
	
	private int skipCoded(int i) {
		int i1 = skipChar(i, '$');
		if (i1 == i) return i;
		int i2 = skipDigits(i1, 16);
		if (i2 == i1) return i1;
		while (true) {
			int i3 = skipChar(i2, '+');
			if (i3 == i2) return i2;
			int i4 = skipDigits(i3, 16);
			if (i4 == i3) return i2;
			i2 = i4;
		}
	}
	
	private int skipUnit(int i) {
		int i1 = skipLetters(i);
		if (i1 != i) return i1;
		i1 = skipChar(i, '/');
		if (i1 == i) return i;
		int i2 = skipSpaces(i1);
		int i3 = skipFloat(i2);
		if (i3 == i2) return i;
		return i3;
	}
	
	public boolean hasNext() {
		return (j = i = skipSpaces(i)) < n;
	}
	
	public boolean hasNextID() {
		return (j = skipLetters(i = skipSpaces(i))) != i;
	}
	
	public boolean hasNextInt() {
		return (j = skipDigits(i = skipSpaces(i))) != i;
	}
	
	public boolean hasNextInt(int radix) {
		return (j = skipDigits((i = skipSpaces(i)), radix)) != i;
	}
	
	public boolean hasNextChar(int... chars) {
		return (j = skipChar((i = skipSpaces(i)), chars)) != i;
	}
	
	public boolean hasNextQuote(int quote) {
		return (j = skipQuote((i = skipSpaces(i)), quote, quote, '\\')) != i;
	}
	
	public boolean hasNextFloat() {
		return (j = skipFloat(i = skipSpaces(i))) != i;
	}
	
	public boolean hasNextCoded() {
		return (j = skipCoded(i = skipSpaces(i))) != i;
	}
	
	public boolean hasNextUnit() {
		return (j = skipUnit(i = skipSpaces(i))) != i;
	}
	
	public String next() {
		String next = s.substring(i, j);
		i = j;
		return next;
	}
	
	public int nextInt() {
		return Integer.parseInt(next());
	}
	
	public int nextInt(int radix) {
		return Integer.parseInt(next(), radix);
	}
	
	public String nextQuote(int quote) {
		String s = next(), q = String.valueOf(Character.toChars(quote));
		if (s.startsWith(q)) s = s.substring(q.length(), s.length());
		if (s.endsWith(q)) s = s.substring(0, s.length() - q.length());
		return StringUtilities.unescape(s);
	}
	
	public float nextFloat() {
		return Float.parseFloat(next());
	}
	
	public double nextDouble() {
		return Double.parseDouble(next());
	}
	
	public String nextCoded() {
		return StringUtilities.fromCodes(next());
	}
	
	public float nextUnit(float def) {
		return KeyCapUnits.parseUnit(next(), def);
	}
	
	public KeyCapParserException expected(String e) {
		String f = ((i < n) ? s.substring(i, i + 1) : "end of text");
		String m = "Expected " + e + " but found " + f + " at " + i;
		return new KeyCapParserException(m, s, i);
	}
	
	public void expectEnd() {
		if (hasNext()) throw expected("end of text");
	}
}
