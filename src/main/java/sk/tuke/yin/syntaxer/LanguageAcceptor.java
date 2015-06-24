package sk.tuke.yin.syntaxer;

import yajco.model.Language;

public interface LanguageAcceptor {
    public static enum LiteralType {
        NUMBER, STRING, CHAR, OTHER;
    }
    
    public void acceptLanguage(Language language);

    public void acceptKeyword(String keyword);

    public void acceptOperator(String operator);

    public void acceptLiteral(String regexp, LiteralType type);

    public void acceptComment(String regexp);

    public void rejectedComment(String regexp);
}