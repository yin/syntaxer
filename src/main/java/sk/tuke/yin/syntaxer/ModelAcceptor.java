package sk.tuke.yin.syntaxer;

import sk.tuke.yin.syntaxer.model.SimpleModel;
import yajco.model.Language;

public class ModelAcceptor implements LanguageAcceptor {
    private final SimpleModel model;

    public ModelAcceptor(SimpleModel model) {
        this.model = model;
    }

    @Override
    public void acceptLanguage(Language language) {
        model.setValue("languageName", language.getName());
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
    public void acceptComment(String regexp) {
        if (!regexp.trim().isEmpty()) {
            model.addToken("comments", regexp);
            HighlightingCompiler._log("ACCEPTED comment: " + regexp);
        } else {
            
        }
    }

    @Override
    public void rejectedComment(String regexp) {
        HighlightingCompiler._log("Rejected comment: " + regexp);
    }
}