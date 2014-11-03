package sk.tuke.yin.syntaxer.model;

import java.util.HashMap;

public class ColorMapping {
	private HashMap<String, Color> colors = new HashMap<String, Color>();
	
	public Color get(String type) {
		return colors.get(type);
	}
}
