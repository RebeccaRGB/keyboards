package com.kreative.keycaps;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyCapEngraver {
	public static class TextBox {
		public final float x;
		public final float y;
		public final float width;
		public final float height;
		public final float lineHeight;
		public final Anchor anchor;
		public final KeyCapLegendItem item;
		public final String text;
		public final String path;
		protected TextBox(Rectangle2D r, float lineHeight, Anchor anchor, KeyCapLegendItem item) {
			this.x = (float)r.getX();
			this.y = (float)r.getY();
			this.width = (float)r.getWidth();
			this.height = (float)r.getHeight();
			this.lineHeight = lineHeight;
			this.anchor = anchor;
			this.item = item;
			this.text = (item != null) ? item.getText() : null;
			this.path = (item != null) ? item.getPath() : null;
		}
	}
	
	protected static class TextBoxGen {
		private final Set<String> keys;
		private final String[] mustNotContain;
		private final String[] mustContain;
		private final boolean front;
		private final Anchor da;
		private final float dsw;
		private final float dsh;
		private final float lhd;
		private final Anchor ta;
		protected TextBoxGen(
			Collection<String> keys,
			Collection<String> mustNotContain,
			Collection<String> mustContain,
			boolean front, Anchor da,
			float dsw, float dsh,
			float lhd, Anchor ta
		) {
			this.keys = new HashSet<String>(keys);
			this.mustNotContain = (
				(mustNotContain == null || mustNotContain.isEmpty()) ? null :
				mustNotContain.toArray(new String[mustNotContain.size()])
			);
			this.mustContain = (
				(mustContain == null || mustContain.isEmpty()) ? null :
				mustContain.toArray(new String[mustContain.size()])
			);
			this.front = front;
			this.da = da;
			this.dsw = dsw;
			this.dsh = dsh;
			this.lhd = lhd;
			this.ta = ta;
		}
		protected TextBox makeBox(
			Rectangle2D cbox, Rectangle2D tbox, Rectangle2D fbox, float tlh, float flh,
			KeyCapLegend legend, String key, KeyCapLegendItem item
		) {
			if (item == null) return null;
			if (!keys.contains(key)) return null;
			if (mustNotContain != null && legend.containsAny(mustNotContain)) return null;
			if (mustContain != null && !legend.containsAny(mustContain)) return null;
			Rectangle2D box = (front ? fbox : tbox);
			if (box == null) return null;
			if (da != null) box = da.divide(box, dsw, dsh);
			return new TextBox(box, (front ? flh : tlh) / lhd, ta, item);
		}
	}
	
	protected final KeyCapMold mold;
	protected final float moldScale;
	protected final float keyCapSize;
	protected final List<TextBoxGen> gens;
	
	public KeyCapEngraver(KeyCapMold mold, float moldScale, float keyCapSize) {
		this.mold = mold;
		this.moldScale = moldScale;
		this.keyCapSize = keyCapSize;
		this.gens = new ArrayList<TextBoxGen>();
		this.makeGens();
	}
	
	protected void T(String key, Anchor da, float dsw, float dsh, float lhd, Anchor ta) {
		gens.add(new TextBoxGen(Arrays.asList(key), null, null, false, da, dsw, dsh, lhd, ta));
	}
	
	protected void TMNC(String key, Collection<String> mnc, Anchor da, float dsw, float dsh, float lhd, Anchor ta) {
		gens.add(new TextBoxGen(Arrays.asList(key), mnc, null, false, da, dsw, dsh, lhd, ta));
	}
	
	protected void TMC(String key, Collection<String> mc, Anchor da, float dsw, float dsh, float lhd, Anchor ta) {
		gens.add(new TextBoxGen(Arrays.asList(key), null, mc, false, da, dsw, dsh, lhd, ta));
	}
	
	protected void F(String key, Anchor da, float dsw, float dsh, float lhd, Anchor ta) {
		gens.add(new TextBoxGen(Arrays.asList(key), null, null, true, da, dsw, dsh, lhd, ta));
	}
	
	protected void FMNC(String key, Collection<String> mnc, Anchor da, float dsw, float dsh, float lhd, Anchor ta) {
		gens.add(new TextBoxGen(Arrays.asList(key), mnc, null, true, da, dsw, dsh, lhd, ta));
	}
	
	protected void FMC(String key, Collection<String> mc, Anchor da, float dsw, float dsh, float lhd, Anchor ta) {
		gens.add(new TextBoxGen(Arrays.asList(key), null, mc, true, da, dsw, dsh, lhd, ta));
	}
	
	protected void makeGens() {
		Collection<String> altFn = Arrays.asList(KeyCapLegend.KEY_ALT_FUNCTION);
		Collection<String> alt = Arrays.asList(
			KeyCapLegend.KEY_ALT_LETTER,
			KeyCapLegend.KEY_ALT_UNSHIFTED,
			KeyCapLegend.KEY_ALT_SHIFTED
		);
		Collection<String> frAltFn = Arrays.asList(KeyCapLegend.KEY_FRONT_ALT_FUNCTION);
		Collection<String> frAlt = Arrays.asList(
			KeyCapLegend.KEY_FRONT_ALT_LETTER,
			KeyCapLegend.KEY_FRONT_ALT_UNSHIFTED,
			KeyCapLegend.KEY_FRONT_ALT_SHIFTED
		);
		// condition ---- key ---- condition -- divide anchor -- dsw - dsh - lhd - text anchor //
		TMNC(KeyCapLegend.KEY_FUNCTION, altFn, null,             1,    1,    3,    Anchor.CENTER);
		TMC (KeyCapLegend.KEY_FUNCTION, altFn, Anchor.SOUTH,     1,    0.5f, 3,    Anchor.CENTER);
		T   (KeyCapLegend.KEY_ALT_FUNCTION,    Anchor.NORTH,     1,    0.5f, 3,    Anchor.CENTER);
		TMNC(KeyCapLegend.KEY_LETTER,     alt, null,             1,    1,    2,    Anchor.CENTER);
		TMC (KeyCapLegend.KEY_LETTER,     alt, Anchor.NORTHWEST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		T   (KeyCapLegend.KEY_ALT_LETTER,      Anchor.NORTHEAST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		TMNC(KeyCapLegend.KEY_UNSHIFTED,  alt, Anchor.SOUTH,     1,    0.5f, 2.5f, Anchor.CENTER);
		TMC (KeyCapLegend.KEY_UNSHIFTED,  alt, Anchor.SOUTHWEST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		T   (KeyCapLegend.KEY_ALT_UNSHIFTED,   Anchor.SOUTHEAST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		TMNC(KeyCapLegend.KEY_SHIFTED,    alt, Anchor.NORTH,     1,    0.5f, 2.5f, Anchor.CENTER);
		TMC (KeyCapLegend.KEY_SHIFTED,    alt, Anchor.NORTHWEST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		T   (KeyCapLegend.KEY_ALT_SHIFTED,     Anchor.NORTHEAST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		T   (KeyCapLegend.KEY_NUMPAD,          Anchor.NORTH,     1,    0.6f, 2,    Anchor.CENTER);
		T   (KeyCapLegend.KEY_NUMPAD_FUNCTION, Anchor.SOUTH,     1,    0.4f, 3,    Anchor.CENTER);
		// condition -------- key -------- condition -- divide anchor -- dsw - dsh - lhd - text anchor //
		FMNC(KeyCapLegend.KEY_FRONT_FUNCTION, frAltFn, null,             1,    1,    3,    Anchor.CENTER);
		FMC (KeyCapLegend.KEY_FRONT_FUNCTION, frAltFn, Anchor.SOUTH,     1,    0.5f, 3,    Anchor.CENTER);
		F   (KeyCapLegend.KEY_FRONT_ALT_FUNCTION,      Anchor.NORTH,     1,    0.5f, 3,    Anchor.CENTER);
		FMNC(KeyCapLegend.KEY_FRONT_LETTER,     frAlt, null,             1,    1,    2,    Anchor.CENTER);
		FMC (KeyCapLegend.KEY_FRONT_LETTER,     frAlt, Anchor.NORTHWEST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		F   (KeyCapLegend.KEY_FRONT_ALT_LETTER,        Anchor.NORTHEAST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		FMNC(KeyCapLegend.KEY_FRONT_UNSHIFTED,  frAlt, Anchor.SOUTH,     1,    0.5f, 2.5f, Anchor.CENTER);
		FMC (KeyCapLegend.KEY_FRONT_UNSHIFTED,  frAlt, Anchor.SOUTHWEST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		F   (KeyCapLegend.KEY_FRONT_ALT_UNSHIFTED,     Anchor.SOUTHEAST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		FMNC(KeyCapLegend.KEY_FRONT_SHIFTED,    frAlt, Anchor.NORTH,     1,    0.5f, 2.5f, Anchor.CENTER);
		FMC (KeyCapLegend.KEY_FRONT_SHIFTED,    frAlt, Anchor.NORTHWEST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		F   (KeyCapLegend.KEY_FRONT_ALT_SHIFTED,       Anchor.NORTHEAST, 0.5f, 0.5f, 2.5f, Anchor.CENTER);
		F   (KeyCapLegend.KEY_FRONT_NUMPAD,            Anchor.NORTH,     1,    0.6f, 2,    Anchor.CENTER);
		F   (KeyCapLegend.KEY_FRONT_NUMPAD_FUNCTION,   Anchor.SOUTH,     1,    0.4f, 3,    Anchor.CENTER);
	}
	
	public TextBox[] makeBoxes(KeyCapShape shape, KeyCapLegend legend) {
		Shape cs = shape.toAWTShape(keyCapSize);
		Shape ts = mold.createTopTextArea(cs);
		Shape fs = mold.createFrontTextArea(cs);
		Rectangle2D cbox = ShapeUtilities.getWidestRect(cs, null);
		Rectangle2D tbox = ShapeUtilities.getWidestRect(ts, null);
		Rectangle2D fbox = ShapeUtilities.getWidestRect(fs, null);
		float tlh = (tbox != null) ? (keyCapSize - (float)(cbox.getHeight()-tbox.getHeight())) : 0;
		float flh = (fbox != null) ? ((float)fbox.getHeight()) : 0;
		ArrayList<TextBox> boxes = new ArrayList<TextBox>();
		String[] keys = legend.keySet().toArray(new String[legend.size()]);
		Arrays.sort(keys, KEY_COMPARATOR);
		for (String key : keys) {
			KeyCapLegendItem item = legend.get(key);
			if (item == null) continue;
			for (TextBoxGen gen : gens) {
				TextBox box = gen.makeBox(cbox, tbox, fbox, tlh, flh, legend, key, item);
				if (box != null) boxes.add(box);
			}
		}
		return boxes.toArray(new TextBox[boxes.size()]);
	}
	
	private static final List<String> SPECIAL_KEYS = Arrays.asList(
		KeyCapLegend.KEY_FUNCTION,
		KeyCapLegend.KEY_ALT_FUNCTION,
		KeyCapLegend.KEY_LETTER,
		KeyCapLegend.KEY_UNSHIFTED,
		KeyCapLegend.KEY_SHIFTED,
		KeyCapLegend.KEY_ALT_LETTER,
		KeyCapLegend.KEY_ALT_UNSHIFTED,
		KeyCapLegend.KEY_ALT_SHIFTED,
		KeyCapLegend.KEY_NUMPAD,
		KeyCapLegend.KEY_NUMPAD_FUNCTION,
		KeyCapLegend.KEY_FRONT_FUNCTION,
		KeyCapLegend.KEY_FRONT_ALT_FUNCTION,
		KeyCapLegend.KEY_FRONT_LETTER,
		KeyCapLegend.KEY_FRONT_UNSHIFTED,
		KeyCapLegend.KEY_FRONT_SHIFTED,
		KeyCapLegend.KEY_FRONT_ALT_LETTER,
		KeyCapLegend.KEY_FRONT_ALT_UNSHIFTED,
		KeyCapLegend.KEY_FRONT_ALT_SHIFTED,
		KeyCapLegend.KEY_FRONT_NUMPAD,
		KeyCapLegend.KEY_FRONT_NUMPAD_FUNCTION
	);
	
	protected static final Comparator<String> KEY_COMPARATOR = new Comparator<String>() {
		public int compare(String a, String b) {
			int i = SPECIAL_KEYS.indexOf(a);
			int j = SPECIAL_KEYS.indexOf(b);
			return (i != j) ? (i - j) : a.compareTo(b);
		}
	};
}
