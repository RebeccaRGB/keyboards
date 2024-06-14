package com.kreative.keycaps;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class KKCXPositionTest {
	public static void main(String[] args) throws IOException {
		StringBuffer xml = new StringBuffer();
		xml.append(KKCXWriter.XML_DECLARATION);
		xml.append(KKCXWriter.DOCTYPE_DECLARATION);
		xml.append("<keyCapLayout>");
		for (int i = 1; i < args.length; i += 2) {
			xml.append("<k x=\"");
			xml.append(args[i-1]);
			xml.append("\" y=\"");
			xml.append(args[i-0]);
			xml.append("\"></k>");
		}
		xml.append("</keyCapLayout>");
		byte[] data = xml.toString().getBytes("utf-8");
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		KeyCapLayout kcl = KKCXReader.parse("input", in);
		for (KeyCap cap : kcl) System.out.println(cap.getPosition());
	}
	
	private KKCXPositionTest() {}
}
