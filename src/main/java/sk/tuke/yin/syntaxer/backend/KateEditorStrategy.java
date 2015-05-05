package sk.tuke.yin.syntaxer.backend;

public class KateEditorStrategy extends GeneralEditorStrategy {
    private static final String ID = "kate";
    private static final String NAME = "Kate";
    private static final String TEMPLATE = "kate.vm";
    private static final String SUFFIX = "kate.xml";
    
    public KateEditorStrategy() {
        super(ID, NAME, TEMPLATE, SUFFIX);
    }

    @Override
    protected String transformRegexp(String regexp, String type) {
        return regexp.replaceAll("\\\\n\\)?\\$?$", "\\$");
    }
}
