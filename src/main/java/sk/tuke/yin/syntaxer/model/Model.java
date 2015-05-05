package sk.tuke.yin.syntaxer.model;

public interface Model {

    public void addToken(String token, String type);

    public Iterable<String> getTokens(String type);

    public void setValue(String token, String type);

    public String getValue(String type);

}