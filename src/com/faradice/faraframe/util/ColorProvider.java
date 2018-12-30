package com.faradice.faraframe.util;

import java.awt.Color;
import java.util.HashMap;

public class ColorProvider {
	static HashMap<String, Color> standard = new HashMap<String, Color>();
	static {
		standard.put("red", Color.red);
		standard.put("gren", Color.green);
		standard.put("blue", Color.blue);
		standard.put("black", Color.black);
		standard.put("lightgray", Color.lightGray);
		standard.put("gray", Color.gray);
		standard.put("darkGray", Color.darkGray);
		standard.put("pink", Color.pink);
		standard.put("orange", Color.orange);
		standard.put("yellow", Color.yellow);
		standard.put("green", Color.green);
		standard.put("magenta", Color.magenta);
		standard.put("cyan", Color.cyan);
		standard.put("white", Color.white);
	}

	public static Color getColor(String colStr) {
		Color color = standard.get(colStr.toLowerCase());
		if (color == null) {
			String[] rgba = colStr.split(",");
			int r = 0;
			int g = 0;
			int b = 0;
			int a = 255;
			if (rgba.length > 0)
				try {
					r = Integer.parseInt(rgba[0]);
				} catch (Exception ex) {
					r = 0;
				}
			if (rgba.length > 1)
				try {
					g = Integer.parseInt(rgba[1]);
				} catch (Exception ex) {
					g = 0;
				}
			if (rgba.length > 2)
				try {
					b = Integer.parseInt(rgba[2]);
				} catch (Exception ex) {
					b = 0;
				}
			if (rgba.length > 3)
				try {
					a = Integer.parseInt(rgba[3]);
				} catch (Exception ex) {
					a = 255;
				}
			color = new Color(r, g, b, a);
		}
		return color;
	}
	
	public static Color[] parseColorStr(String colorStr) {
		Color[] colors = new Color[3];
		String[] cols = colorStr.split(":");
		for (int i = 0; i < cols.length && i < 3; i++) {
			colors[i] = ColorProvider.getColor(cols[i]);
		}
		if (colors[0] == null && colors[1] == null && colors[2] == null) {
			colors = new Color[] {Color.gray, Color.gray, Color.gray};
		} else if (colors[1] == null && colors[2] == null) {
			colors = new Color[] {colors[0], colors[0], colors[0]};
		} else if (colors[2] == null) {
			colors = new Color[] {colors[0], colors[0], colors[1]};
		}
		return colors;
	}

	public static String toString(Color c) {
		return c.getRed()+","+c.getGreen()+","+c.getBlue()+","+c.getAlpha();
	}
	
}
