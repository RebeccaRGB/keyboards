package com.kreative.keycaps;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Reads the contents of a kbit resource from the Key Caps desk accessory and converts it to a PNG image.
 */
public final class Kbitter {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File file = new File(arg);
			File out = new File(file.getParentFile(), file.getName() + ".png");
			InputStream in = new FileInputStream(file);
			BufferedImage img = kbitToImage(in);
			ImageIO.write(img, "png", out);
			in.close();
		}
	}
	
	public static BufferedImage kbitToImage(InputStream input) throws IOException {
		DataInputStream in = new DataInputStream(input);
		in.readShort();
		int y0 = in.readShort();
		int x0 = in.readShort();
		int y1 = in.readShort();
		int x1 = in.readShort();
		int depth = in.readShort();
		int mask = ((1 << depth) - 1);
		BufferedImage img = new BufferedImage(x1-x0, y1-y0, BufferedImage.TYPE_INT_ARGB);
		byte[] buf = new byte[(((x1-x0)*depth+15)/16)*2];
		int[] rgb = new int[x1-x0];
		for (int i = 0, y = y0; y < y1; y++, i++) {
			in.readFully(buf);
			for (int j = 0, x = x0; x < x1; x++, j++) {
				int q = (j * depth) >> 3, r = (j * depth) & 7;
				int k = (buf[q] >> (8 - depth - r)) & mask;
				rgb[j] = -1 - ((255 * k / mask) * 0x010101);
			}
			img.setRGB(0, i, x1-x0, 1, rgb, 0, 0);
		}
		return img;
	}
	
	private Kbitter() {}
}
