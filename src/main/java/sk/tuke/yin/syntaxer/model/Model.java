package sk.tuke.yin.syntaxer.model;

public interface Model {

    void addToken(String token, String type);

    Iterable<String> getTokens(String type);

    void setValue(String token, String type);

    String getValue(String type);

	void addObject(String type, Object object);

	Iterable<Object> getObject(String type);

}