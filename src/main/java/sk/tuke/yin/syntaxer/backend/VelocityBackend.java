package sk.tuke.yin.syntaxer.backend;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import sk.tuke.yin.syntaxer.model.HighlightingModel;
import sk.tuke.yin.syntaxer.model.HighlightingModel.LiteralType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class VelocityBackend {
    private final EditorStrategy strategy;
    private final Writer writer;
    
    public static interface EditorStrategy {
        public String getEditorId();
        public String getEditorName();
        public String getTemplateFile();
        public String getOutputSuffix();
        public String transformRegexp(String regexp, String type);
    }

    public VelocityBackend(EditorStrategy strategy, Writer writer) {
        this.strategy = strategy;
        this.writer = writer;
    }

    public VelocityContext modelToContext(HighlightingModel model) {
        VelocityContext context = new VelocityContext();
        context.put("name", model.getLanguageName());
        putToContext("keywords", model.getKeywords(), context);
        putToContext("operators", model.getOperators(), context);
        putToContext("literalStrings", model.getLiterals(LiteralType.STRING), context);
        putToContext("literalChars", model.getLiterals(LiteralType.CHAR), context);
        putToContext("literalNumbers", model.getLiterals(LiteralType.NUMBER), context);
        putToContext("literalOthers", model.getLiterals(LiteralType.OTHER), context);
        putToContext("comments", model.getComments(), context);
        
        return context;
    }

    private void putToContext(String type, Iterable<String> items, VelocityContext context) {
        Builder<String> builder = ImmutableList.builder();
        for (String item : items) {
                builder.add(strategy.transformRegexp(item, type));
        }
        context.put(type, builder.build());
    }

    public void write(HighlightingModel model) throws IOException {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();

        VelocityContext context = modelToContext(model);
        Template template = ve.getTemplate(strategy.getTemplateFile());
        template.merge(context, writer);
        template.process();
        writer.close();
    }

    public static class BackendBuilder {
        WriteTo writeTo;
        Object[] writeToCustom;
        Path customFile;
        String editorId;

        public enum WriteTo {
            STDOUT, STRING, FILE, CUSTOM;
        }
        
        public BackendBuilder() {
        }

        public BackendBuilder writeTo(WriteTo writeTo, Object... custom) {
            this.writeTo = writeTo;
            this.writeToCustom = custom;
            return this;
        }

        public BackendBuilder targetEditor(String editorId) {
            this.editorId = editorId;
            return this;
        }

        public VelocityBackend build() throws IOException, IllegalArgumentException,
                IllegalStateException {
            EditorStrategy strategy = null;
            Writer writer = null;
            if (editorId == null) {
                throw new IllegalStateException("Target editor not specified.");
            } else {
                strategy = EditorRepository.instance().getEditorForId(editorId);
                if (strategy == null) {
                    throw new IllegalArgumentException("Unsupported Target Editor was specified.");
                }
            }
            if (writeTo == null) {
                throw new IllegalStateException("Output writer not specified.");
            } else {
                switch (writeTo) {
                case STDOUT:
                    writer = new OutputStreamWriter(System.out);
                    break;
                case STRING:
                    writer = new StringWriter();
                    break;
                case FILE:
                    if (writeToCustom.length == 1 && writeToCustom[0] instanceof Path) {
                        Path path = (Path) writeToCustom[0];
                        //TODO yin: Make a Writer provider, so it will be instantiated as late as possible
                        System.out.println(">>>>>>>>>>>>>>>> <<<<<<<<<<<<<<<<< >>>> " + path.toFile().toString());
                        writer = new FileWriter(path.toFile());
                    } else {
                        throw new IllegalArgumentException("File path not specified. Path can be "
                                + "specified as single optional argument to writeTo() call.");
                    }
                    break;
                case CUSTOM:
                    try {
                        if (writeToCustom.length > 1) {
                            throw new IllegalStateException("More than one optional arguments"
                                    + "supplied as custom writer.");
                        }
                        writer = (Writer) writeToCustom[0];
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Can not use custom Writer. Either"
                                + "not supplied or not a Writer.", e);
                    }
                    break;
                }
            }
            return new VelocityBackend(strategy, writer);
        }
    }
}
