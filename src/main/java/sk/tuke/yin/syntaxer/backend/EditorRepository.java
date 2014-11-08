package sk.tuke.yin.syntaxer.backend;

import com.google.common.collect.ImmutableMap;

public class EditorRepository {
    private static EditorRepository instance;
    private final ImmutableMap<String, EditorConfig> editors;

    private EditorRepository() {
        editors = new ImmutableMap.Builder<String, EditorConfig>()
                .put("kate", new EditorConfig("kate.vm", "xml", "Kate", "kate"))
                .put("gtksourceview",
                        new EditorConfig("gtksourceview.vm", "xml", "Gedit", "gtksourceview"))
                // .put("codemirror", new EditorConfig("codemirror.vm", "js", "CodeMirror", "codemirror"))
                // .put("notepad++", new EditorConfig("notepadpp.vm", "???", "Notepad++", "notepad++"))
                .build();
    }

    public static EditorRepository instance() {
        if (instance == null) {
            instance = new EditorRepository();
        }
        return instance;
    }

    public EditorConfig getEditorForId(String editorId) throws IllegalArgumentException {
        EditorConfig cfg = editors.get(editorId);
        if (cfg != null) {
            return cfg;
        } else {
            throw new IllegalArgumentException("Unknown editor: " + editorId);
        }
    }

    public static class EditorConfig {
        public final String templateFile;
        public final String outputSuffix;
        public final String editorName;
        public final String editorId;

        public EditorConfig(String templateFile, String outputSuffix, String editorName,
                String editorId) {
            this.templateFile = templateFile;
            this.outputSuffix = outputSuffix;
            this.editorName = editorName;
            this.editorId = editorId;
        }

        public String getTemplateFile() {
            return templateFile;
        }

        public String getOutputSuffix() {
            return outputSuffix;
        }

        public String getEditorName() {
            return editorName;
        }

        public String getEditorId() {
            return editorId;
        }
    }
}
