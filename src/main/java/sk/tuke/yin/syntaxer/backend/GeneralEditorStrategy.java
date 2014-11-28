package sk.tuke.yin.syntaxer.backend;

import sk.tuke.yin.syntaxer.backend.VelocityBackend.EditorStrategy;

public class GeneralEditorStrategy implements EditorStrategy {
    private final String editorId;
    private final String editorName;
    private final String outputSuffix;
    private final String templateFile;
    
    public GeneralEditorStrategy(String editorId, String editorName, String templateFile, String outputSuffix) {
        this.editorId = editorId;
        this.editorName = editorName;
        this.outputSuffix = outputSuffix;
        this.templateFile = templateFile;
    }
    
    @Override
    public String getEditorId() {
        return editorId;
    }

    @Override
    public String getEditorName() {
        return editorName;
    }

    @Override
    public String getOutputSuffix() {
        return outputSuffix;
    }

    @Override
    public String getTemplateFile() {
        return templateFile;
    }

    @Override
    public String transformRegexp(String regexp, String type) {
        return regexp;
    }
}
