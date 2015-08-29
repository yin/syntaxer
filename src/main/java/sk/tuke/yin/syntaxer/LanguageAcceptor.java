package sk.tuke.yin.syntaxer;

import yajco.model.Language;

public interface LanguageAcceptor {
    static enum LiteralType {
        NUMBER, STRING, CHAR, OTHER;
    }
    
    void acceptLanguage(Language language);

	void acceptGlobs(String glob);

	void acceptMimetypes(String mimetypes);

    void acceptKeyword(String keyword);

    void acceptOperator(String operator);

    void acceptLiteral(String regexp, LiteralType type);

    void acceptWhitespace(String regexp);

    void acceptLineComment(String regexp);

    void acceptBlockComment(String prefix, String sufix);
}