package com.kreative.keycaps;

public class KeyCapParserException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	
	private final String text;
	private final int pos;
	
	KeyCapParserException(String message, String text, int pos) {
		super(message);
		this.text = text;
		this.pos = pos;
	}
	
	public String getText() { return text; }
	public int getPosition() { return pos; }
}
