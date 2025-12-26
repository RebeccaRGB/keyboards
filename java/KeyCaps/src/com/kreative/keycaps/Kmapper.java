package com.kreative.keycaps;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the contents of a Mac OS Classic KMAP resource.
 */
public class Kmapper {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File file = new File(arg);
			InputStream in = new FileInputStream(file);
			Kmap kmap = new Kmap(new DataInputStream(in));
			in.close();
			System.out.println(kmap);
		}
	}
	
	public static final class Kmap {
		public final short id;
		public final short version;
		public final byte[] map;
		public final List<KmapException> exceptions;
		public Kmap(DataInput in) throws IOException {
			this.id = in.readShort();
			this.version = in.readShort();
			this.map = new byte[128]; in.readFully(this.map);
			this.exceptions = new ArrayList<KmapException>();
			int n = in.readUnsignedShort();
			for (int i = 0; i < n; i++) {
				this.exceptions.add(new KmapException(in));
			}
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(id);
			sb.append(" ");
			sb.append(version);
			for (int i = 0; i < 128; i++) {
				String h = "00" + Integer.toHexString(map[i]).toUpperCase();
				sb.append(((i & 15) == 0) ? "\n" : " ");
				sb.append(h.substring(h.length() - 2));
			}
			for (KmapException e : exceptions) {
				sb.append("\n");
				sb.append(e);
			}
			return sb.toString();
		}
	}
	
	public static final class KmapException {
		public final byte keyCode;
		public final byte flags;
		public final byte[] data;
		public KmapException(DataInput in) throws IOException {
			this.keyCode = in.readByte();
			this.flags = in.readByte();
			int n = in.readUnsignedByte();
			this.data = new byte[n];
			in.readFully(this.data);
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			String h = "00" + Integer.toHexString(keyCode).toUpperCase();
			String b = "00000000" + Integer.toBinaryString(flags);
			sb.append(h.substring(h.length() - 2));
			sb.append(" ");
			sb.append(b.substring(b.length() - 8));
			for (byte db : data) {
				String dh = "00" + Integer.toHexString(db).toUpperCase();
				sb.append(" ");
				sb.append(dh.substring(dh.length() - 2));
			}
			return sb.toString();
		}
	}
}
