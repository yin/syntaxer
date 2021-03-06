package sk.tuke.yin.syntaxer.backend;

import sk.tuke.yin.syntaxer.backend.VelocityBackend.EditorStrategy;

import com.google.common.collect.ImmutableMap;

public class EditorRepository {
    private static EditorRepository instance;
    private final ImmutableMap<String, EditorStrategy> editors;

    private EditorRepository() {
        editors = new ImmutableMap.Builder<String, EditorStrategy>()
                .put("kate", new KateEditorStrategy())
                .put("gedit",
                        new GeneralEditorStrategy("gedit", "Gedit", "gtksourceview.vm", "gedit.xml"))
                .build();
    }

    public static EditorRepository instance() {
        if (instance == null) {
            instance = new EditorRepository();
        }
        return instance;
    }

    public EditorStrategy getEditorForId(String editorId) throws IllegalArgumentException {
        EditorStrategy cfg = editors.get(editorId);
        if (cfg != null) {
            return cfg;
        } else {
            throw new IllegalArgumentException("Unknown editor: " + editorId);
        }
    }
}
