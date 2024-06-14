package com.kreative.keycaps;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class KKCXReader {
	public static KeyCapLayout read(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		KeyCapLayout kcl = parse(file.getName(), in);
		in.close();
		return kcl;
	}
	
	public static KeyCapLayout parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new KKCXEntityResolver());
			builder.setErrorHandler(new KKCXErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static KeyCapLayout parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("keyCapLayout")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseKeyCapLayout(child);
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			throw new IOException("Empty document.");
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static KeyCapLayout parseKeyCapLayout(Node node) throws IOException {
		KeyCapLayout kcl = new KeyCapLayout();
		parseAttributes(
			node.getAttributes(), kcl.getPropertyMap(),
			"name", "kbdType", "gestalt", "vs", "cc", "lc", "lh", "a"
		);
		Point2D.Float loc = new Point2D.Float();
		float keyCapSize = KeyCapUnits.U;
		for (Node child : getChildren(node)) {
			String ctype = child.getNodeName();
			if (ctype.equalsIgnoreCase("p")) {
				parseProperty(child.getAttributes(), kcl.getPropertyMap());
			} else if (ctype.equalsIgnoreCase("k")) {
				KeyCap cap = parseKeyCap(child, loc.x, loc.y, keyCapSize);
				KeyCapPosition pos = cap.getPosition();
				KeyCapShape shape = cap.getShape();
				keyCapSize = pos.getKeyCapSize();
				loc.y = pos.getY(keyCapSize);
				loc.x = pos.getX(keyCapSize) + shape.getAdvanceWidth(keyCapSize);
				kcl.add(cap);
			} else {
				throw new IOException("Unknown element: " + ctype);
			}
		}
		return kcl;
	}
	
	private static KeyCap parseKeyCap(Node node, float dx, float dy, float ds) throws IOException {
		// Parse position.
		NamedNodeMap attr = node.getAttributes();
		KeyCapPosition pos = parsePosition(attr, dx, dy, ds);
		
		// Parse shape.
		KeyCapShape shape = KeyCapShape.DEFAULT;
		String ss = parseString(attr, "shape");
		if (ss != null && ss.length() > 0) {
			try {
				shape = KeyCapShape.parse(ss, pos.getKeyCapSize());
			} catch (KeyCapParserException e) {
				System.err.println("Warning: Invalid shape: " + ss);
			}
		}
		
		// Parse type.
		KeyCapLegend.Type type = null;
		String ts = parseString(attr, "type");
		if (ts != null && ts.length() > 0) {
			type = KeyCapLegend.Type.forName(ts);
		}
		
		// Parse remaining attributes.
		KeyCap cap = new KeyCap(pos, shape, new KeyCapLegend());
		parseAttributes(
			attr, cap.getPropertyMap(),
			"usb", "vs", "cc", "lc", "lh", "a"
		);
		
		// Parse child elements.
		List<KeyCapLegendItem> items = new ArrayList<KeyCapLegendItem>();
		Map<String,KeyCapLegendItem> kwitems = new HashMap<String,KeyCapLegendItem>();
		for (Node child : getChildren(node)) {
			String ctype = child.getNodeName();
			if (ctype.equalsIgnoreCase("p")) {
				parseProperty(child.getAttributes(), cap.getPropertyMap());
			} else if (ctype.equalsIgnoreCase("l")) {
				KeyCapLegendItem value = parseLegendItem(child);
				NamedNodeMap cattr = child.getAttributes();
				String key = parseString(cattr, "type");
				if (key != null && key.length() > 0) {
					kwitems.put(key, value);
				} else {
					items.add(value);
				}
			} else {
				throw new IOException("Unknown element: " + ctype);
			}
		}
		
		// Parse text content (only relevant if there are no <l> child elements).
		if (items.isEmpty() && kwitems.isEmpty()) {
			String text = textContent(node);
			if (text != null && text.length() > 0) {
				items.add(KeyCapLegendItem.text(text));
			}
		}
		
		// Add legend items.
		cap.getLegend().addAll(type, items);
		cap.getLegend().putAll(kwitems);
		return cap;
	}
	
	private static KeyCapPosition parsePosition(NamedNodeMap attr, float x, float y, float size) {
		float xsize = size;
		float ysize = size;
		String xs = parseString(attr, "x");
		if (xs != null && xs.length() > 0) {
			try {
				KeyCapParser p = new KeyCapParser(xs);
				if (p.hasNextFloat()) x = p.nextFloat();
				if (p.hasNextUnit()) xsize = p.nextUnit(xsize);
				p.expectEnd();
			} catch (KeyCapParserException e) {
				System.err.println("Warning: Invalid x position: " + xs);
			}
		}
		String ys = parseString(attr, "y");
		if (ys != null && ys.length() > 0) {
			try {
				KeyCapParser p = new KeyCapParser(ys);
				if (p.hasNextFloat()) y = p.nextFloat();
				if (p.hasNextUnit()) ysize = p.nextUnit(ysize);
				p.expectEnd();
			} catch (KeyCapParserException e) {
				System.err.println("Warning: Invalid y position: " + ys);
			}
		}
		if (xsize % ysize == 0) return new KeyCapPosition(x, y * (xsize / ysize), xsize);
		if (ysize % xsize == 0) return new KeyCapPosition(x * (ysize / xsize), y, ysize);
		size = KeyCapUnits.minimalUnit(xsize * ysize, x * ysize, y * xsize);
		return new KeyCapPosition(x * size / xsize, y * size / ysize, size);
	}
	
	private static KeyCapLegendItem parseLegendItem(Node node) throws IOException {
		KeyCapLegendItem item;
		NamedNodeMap attr = node.getAttributes();
		String path = parseString(attr, "path");
		if (path != null && path.length() > 0) {
			item = KeyCapLegendItem.path(path);
		} else {
			String text = textContent(node);
			if (text != null && text.length() > 0) {
				item = KeyCapLegendItem.text(text);
			} else {
				return null;
			}
		}
		parseAttributes(
			attr, item.getPropertyMap(),
			"lc", "lh", "a"
		);
		for (Node child : getChildren(node)) {
			String ctype = child.getNodeName();
			if (ctype.equalsIgnoreCase("p")) {
				parseProperty(child.getAttributes(), item.getPropertyMap());
			} else {
				throw new IOException("Unknown element: " + ctype);
			}
		}
		return item;
	}
	
	private static void parseAttributes(NamedNodeMap attr, PropertyMap props, String... keys) {
		if (attr == null) return;
		for (String k : keys) {
			String v = parseString(attr, k);
			if (v == null) continue;
			props.put(k, v);
		}
	}
	
	private static void parseProperty(NamedNodeMap attr, PropertyMap props) {
		if (attr == null) return;
		String k = parseString(attr, "k");
		if (k == null) return;
		String v = parseString(attr, "v");
		if (v == null) return;
		props.put(k, v);
	}
	
	private static String textContent(Node node) {
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		return text.trim();
	}
	
	private static String parseString(NamedNodeMap attr, String key) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		return text.trim();
	}
	
	private static List<Node> getChildren(Node node) {
		List<Node> list = new ArrayList<Node>();
		if (node != null) {
			NodeList children = node.getChildNodes();
			if (children != null) {
				int count = children.getLength();
				for (int i = 0; i < count; i++) {
					Node child = children.item(i);
					if (child != null) {
						String type = child.getNodeName();
						if (type.equalsIgnoreCase("#text") || type.equalsIgnoreCase("#comment")) {
							continue;
						} else {
							list.add(child);
						}
					}
				}
			}
		}
		return list;
	}
	
	private static class KKCXEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("KreativeKeyCaps") || systemId.contains("kkcx.dtd")) {
				return new InputSource(KKCXReader.class.getResourceAsStream("kkcx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class KKCXErrorHandler implements ErrorHandler {
		private final String name;
		public KKCXErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to parse " + name + ": ");
			System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to parse " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to parse " + name + ": ");
			System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
	
	private KKCXReader() {}
}
