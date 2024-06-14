package com.kreative.keycaps;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class KeyCapReader {
	public static KeyCapLayout read(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		KeyCapLayout kcl = read(file.getName(), in);
		in.close();
		return kcl;
	}
	
	public static KeyCapLayout read(String name, InputStream in) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		Scanner scanner = new Scanner(bin, "UTF-8");
		for (;;) {
			bin.mark(640);
			if (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.length() == 0) {
					continue;
				} else if (line.startsWith("<")) {
					bin.reset();
					KeyCapLayout kcl = KKCXReader.parse(name, bin);
					scanner.close();
					bin.close();
					return kcl;
				} else {
					bin.reset();
					KeyCapLayout kcl = KeyCapReader.parse(bin);
					scanner.close();
					bin.close();
					return kcl;
				}
			} else {
				scanner.close();
				bin.close();
				throw new EOFException();
			}
		}
	}
	
	public static KeyCapLayout parse(InputStream in) throws IOException {
		try {
			StringBuffer contents = new StringBuffer();
			Scanner scanner = new Scanner(in, "UTF-8");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.length() == 0 || line.startsWith("#")) continue;
				contents.append(line);
				contents.append("\n");
			}
			scanner.close();
			KeyCapLayout layout = new KeyCapLayout();
			layout.parse(contents.toString());
			return layout;
		} catch (IllegalArgumentException e) {
			throw new IOException(e);
		}
	}
	
	private KeyCapReader() {}
}
