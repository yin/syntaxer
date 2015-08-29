package sk.tuke.yin.syntaxer;

import sk.tuke.yin.syntaxer.model.SimpleModel;
import yajco.model.Language;

public class ModelAcceptor implements LanguageAcceptor {
    private final SimpleModel model;

    public ModelAcceptor(SimpleModel model) {
        this.model = model;
    }

    @Override
    public void acceptGlobs(String globs) {
        model.setValue("globs", globs);
    }

    @Override
    public void acceptMimetypes(String mimetypes) {
        model.setValue("mimetypes", mimetypes);
    }

    @Override
    public void acceptLanguage(Language language) {
        model.setValue("languageName", language.getName());
        model.setValue("languageId", language.getName().replaceAll("\\.", ""));
    }

    @Override
    public void acceptKeyword(String keyword) {
        model.addToken("keywords", keyword);
        HighlightingCompiler._log("ACCEPTED keyword: " + keyword);
    }

    @Override
    public void acceptOperator(String operator) {
        model.addToken("operators", operator);
        HighlightingCompiler._log("ACCEPTED operator: " + operator);
    }

    @Override
    public void acceptLiteral(String literal, LiteralType type) {
        model.addToken("literals." + type.name().toLowerCase(), literal);
        HighlightingCompiler._log("ACCEPTED literal: " + literal);
    }

    @Override
    public void acceptLineComment(String regexp) {
        if (!regexp.trim().isEmpty()) {
            model.addToken("linecomments", regexp);
            HighlightingCompiler._log("ACCEPTED comment: " + regexp);
        } else {
            acceptWhitespace(regexp);
        }
    }

    @Override
    public void acceptWhitespace(String regexp) {
        HighlightingCompiler._log("Rejected comment: " + regexp);
    }

    @Override
    public void acceptBlockComment(String prefix, String sufix) {
        Block block = new Block(prefix, sufix);
		model.addObject("blockcomments", block);
        HighlightingCompiler._log("ACCEPTED blockcomment: " + block);
    }
    
	public static class Block {
    	private String left, right;
    	Block(String left, String right) {
    		this.left = left;
    		this.right = right;
    	}
    	public String getLeft() {
    		return left;
    	}
    	public String getRight() {
    		return right;
    	}
    	public String toString() {
			return new StringBuilder().append(left).append(":").append(right).toString();
		}
    }
}