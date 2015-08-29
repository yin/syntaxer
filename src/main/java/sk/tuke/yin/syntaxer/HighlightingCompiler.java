package sk.tuke.yin.syntaxer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.annotation.processing.Filer;

import sk.tuke.yin.syntaxer.backend.EditorRepository;
import sk.tuke.yin.syntaxer.backend.VelocityBackend;
import sk.tuke.yin.syntaxer.backend.VelocityBackend.BackendBuilder.WriteTo;
import sk.tuke.yin.syntaxer.model.SimpleModel;
import yajco.generator.parsergen.CompilerGenerator;
import yajco.model.Language;

public class HighlightingCompiler implements CompilerGenerator {

    private static final String DEFAULT_EDITOR = "kate";
    private static final String EDITOR_ID_VAR = "editor";
    private static final String GLOBS_ID_VAR = "globs";
	private static final String MIMETYPES_ID_VAR = "mimetypes";

    //    private static final String EDITOR_ID_KEY = "syntaxer.editor";

    @Override
    public void generateFiles(Language language, Filer filer, Properties properties) {
        processLanguage(language);
    }

    @Override
    public void generateFiles(Language language, Filer filer, Properties properties,
            String parserClassName) {
        processLanguage(language);
    }

    private void processLanguage(Language language) {
        SimpleModel model = new SimpleModel();
        ModelAcceptor acceptor = new ModelAcceptor(model);
        LanguageVisitor visitor = new LanguageVisitor(acceptor);
        visitor.visit(language);
        postProcessModel(acceptor);
        try {
            //TODO yin: make target editor an argument
            VelocityBackend backend = new VelocityBackend.BackendBuilder()
                    .writeTo(WriteTo.FILE, getOutputPath(model)).targetEditor(getEditorId())
                    .build();
            backend.write(model);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write output.", e);
        }
    }

    private void postProcessModel(ModelAcceptor acceptor) {
        String globs = System.getProperty(GLOBS_ID_VAR);
        if (globs != null) {
            acceptor.acceptGlobs(globs);
        }
        String mimetypes = System.getProperty(MIMETYPES_ID_VAR);
        if (mimetypes != null) {
            acceptor.acceptMimetypes(mimetypes);
        }
	}

	private static String getEditorId() {
        String id = System.getProperty(EDITOR_ID_VAR);
        if (id == null) {
            id = DEFAULT_EDITOR;
        }
        return id;
    }

    private Path getOutputPath(SimpleModel model) {
        String languageName = model.getValue("languageName");
        String suffix = EditorRepository.instance().getEditorForId(getEditorId()).getOutputSuffix();
        return Paths.get(languageName + '.' + suffix);
    }

    static void _log(Object msg) {
        System.out.println(msg);
    }

    static void _warn(Object msg) {
        System.out.println(msg);
    }
}
