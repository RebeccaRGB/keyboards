package com.kreative.keycaps;

import java.util.HashMap;
import java.util.Map;

public class SVGPathDefs {
	private final Map<String,String> pathIDs = new HashMap<String,String>();
	private final StringBuffer defs = new StringBuffer();
	
	public String getPathID(String path) {
		String id = pathIDs.get(path);
		if (id == null) {
			pathIDs.put(path, (id = "path" + pathIDs.size()));
			defs.append("<path id=\"" + id + "\" d=\"" + path + "\"/>\n");
		}
		return id;
	}
	
	public String toString() {
		return defs.toString();
	}
}
