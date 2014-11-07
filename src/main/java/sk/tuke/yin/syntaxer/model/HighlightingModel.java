package sk.tuke.yin.syntaxer.model;

import java.util.ArrayList;
import java.util.List;

public class HighlightingModel {
	private List<String> keywords = new ArrayList<String>();
	private List<String> operators = new ArrayList<String>();
	private List<String> functions = new ArrayList<String>();
	private String name;
	
	public void addKeyword(String token) {
		keywords.add(token);
	}

	public Iterable<? extends Object> getKeywords() {
		return keywords;
	}

	public void setLanguageName(String name) {
		this.name = name;
	}

	public String getLanguageName() {
		return name;
	}
}
