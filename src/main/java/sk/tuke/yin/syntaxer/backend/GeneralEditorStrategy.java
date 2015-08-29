package sk.tuke.yin.syntaxer.backend;

import org.apache.velocity.VelocityContext;

import sk.tuke.yin.syntaxer.backend.VelocityBackend.EditorStrategy;
import sk.tuke.yin.syntaxer.model.Model;
import sk.tuke.yin.syntaxer.model.SimpleModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/** Provides basic functionality for most Editor strategies. */
public class GeneralEditorStrategy implements EditorStrategy {
    private static final String[] TOKEN_TYPES = new String[] { "keywords", "operators",
            "literalStrings", "literalChars", "literal.NUMBER", "literalOthers", "linecomments" };

    private static final String[] VALUES = new String[] { "languageId", "languageName", "globs", "mimetypes" };

    private static final String[] OBJECTS = new String[] { "blockcomments" };

    private final String editorId;
    private final String editorName;
    private final String outputSuffix;
    private final String templateFile;

    public GeneralEditorStrategy(String editorId, String editorName, String templateFile,
            String outputSuffix) {
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

    protected String transformRegexp(String regexp, String type) {
        return regexp;
    }

    @Override
    public VelocityContext getContext(Model model) {
        VelocityContext context = new VelocityContext();
        for (String type : VALUES) {
            setValue(type, model, context);
        }
        for (String type : TOKEN_TYPES) {
            setTokens(type, model, context);
        }
        for (String type : OBJECTS) {
            setObject(type, model, context);
        }
        return context;
    }

    private void setValue(String type, Model model, VelocityContext context) {
        context.put(type, model.getValue(type));
    }

    private void setTokens(String type, Model model, VelocityContext context) {
        Iterable<String> items = model.getTokens(type);
        Builder<String> builder = ImmutableList.builder();
        for (String item : items) {
            builder.add(transformRegexp(item, type));
        }
        context.put(type, builder.build());
    }

    private void setObject(String type, Model model, VelocityContext context) {
        Iterable<Object> item = model.getObject(type);
        context.put(type, item);
        System.out.println(type+"--------");
        for(Object i:item){
        System.out.println(type+"--------"+i.getClass().getName()+" "+i.toString());
        }
     }
}
