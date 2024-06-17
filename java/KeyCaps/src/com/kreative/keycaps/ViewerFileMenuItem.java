package com.kreative.keycaps;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ViewerFileMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	
	private final File file;
	
	private ViewerFileMenuItem(File file, ActionListener listener) {
		this.file = file;
		String name = file.getName();
		int o = name.lastIndexOf(".");
		if (o > 0) name = name.substring(0, o);
		this.setText(name);
		this.addActionListener(listener);
	}
	
	public File getFile() {
		return this.file;
	}
	
	public static JMenuItem create(File file, ActionListener listener) {
		if (file.isDirectory()) {
			JMenu menu = new JMenu(file.getName());
			File[] children = file.listFiles();
			Arrays.sort(children, FILE_COMPARATOR);
			for (File child : children) {
				if (child.getName().startsWith(".")) continue;
				if (child.getName().endsWith("\r")) continue;
				menu.add(create(child, listener));
			}
			return menu;
		} else {
			return new ViewerFileMenuItem(file, listener);
		}
	}
	
	public static JMenuItem create(File file, FileFilter filter, ActionListener listener) {
		if (file.isDirectory()) {
			JMenu menu = new JMenu(file.getName());
			File[] children = file.listFiles(filter);
			Arrays.sort(children, FILE_COMPARATOR);
			for (File child : children) {
				if (child.getName().startsWith(".")) continue;
				if (child.getName().endsWith("\r")) continue;
				menu.add(create(child, filter, listener));
			}
			return menu;
		} else {
			return new ViewerFileMenuItem(file, listener);
		}
	}
	
	public static JMenuItem create(File file, FilenameFilter filter, ActionListener listener) {
		if (file.isDirectory()) {
			JMenu menu = new JMenu(file.getName());
			File[] children = file.listFiles(filter);
			Arrays.sort(children, FILE_COMPARATOR);
			for (File child : children) {
				if (child.getName().startsWith(".")) continue;
				if (child.getName().endsWith("\r")) continue;
				menu.add(create(child, filter, listener));
			}
			return menu;
		} else {
			return new ViewerFileMenuItem(file, listener);
		}
	}
	
	private static final Comparator<File> FILE_COMPARATOR = new Comparator<File>() {
		public int compare(File a, File b) {
			return a.getName().compareToIgnoreCase(b.getName());
		}
	};
}
