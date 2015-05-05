package sk.tuke.yin.syntaxer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.annotation.processing.Filer;

import sk.tuke.yin.syntaxer.HighlightingGenerator.LanguageAcceptor.LiteralType;
import sk.tuke.yin.syntaxer.backend.EditorRepository;
import sk.tuke.yin.syntaxer.backend.VelocityBackend;
import sk.tuke.yin.syntaxer.backend.VelocityBackend.BackendBuilder.WriteTo;
import sk.tuke.yin.syntaxer.model.SimpleModel;
import yajco.generator.parsergen.CompilerGenerator;
import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.LocalVariablePart;
import yajco.model.Notation;
import yajco.model.NotationPart;
import yajco.model.Property;
import yajco.model.PropertyReferencePart;
import yajco.model.SkipDef;
import yajco.model.TokenDef;
import yajco.model.TokenPart;
import yajco.model.pattern.ConceptPattern;
import yajco.model.pattern.NotationPartPattern;
import yajco.model.pattern.NotationPattern;
import yajco.model.pattern.PropertyPattern;
import yajco.model.pattern.impl.Operator;
import yajco.model.pattern.impl.References;

public class HighlightingGenerator implements CompilerGenerator {

    private static final String DEFAULT_EDITOR = "kate";
    private static final String EDITOR_ID_VAR = "editor";

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

    public static interface LanguageAcceptor {
        public static enum LiteralType {
            NUMBER, STRING, CHAR, OTHER;
        }
        
        public void acceptLanguage(Language language);

        public void acceptKeyword(String keyword);

        public void acceptOperator(String operator);

        public void acceptLiteral(String regexp, LiteralType type);

        public void acceptComment(String regexp);

        public void rejectedComment(String regexp);
    }

    public static class ModelAcceptor implements LanguageAcceptor {
        private final SimpleModel model;

        public ModelAcceptor(SimpleModel model) {
            this.model = model;
        }

        @Override
        public void acceptLanguage(Language language) {
            model.setValue("languageName", language.getName());
        }

        @Override
        public void acceptKeyword(String keyword) {
            model.addToken("keywords", keyword);
            _log("ACCEPTED keyword: " + keyword);
        }

        @Override
        public void acceptOperator(String operator) {
            model.addToken("operators", operator);
            _log("ACCEPTED operator: " + operator);
        }

        @Override
        public void acceptLiteral(String literal, LiteralType type) {
            model.addToken("literals." + type.name().toLowerCase(), literal);
            _log("ACCEPTED literal: " + literal);
        }

        @Override
        public void acceptComment(String regexp) {
            if (!regexp.trim().isEmpty()) {
                model.addToken("comments", regexp);
                _log("ACCEPTED comment: " + regexp);
            }
        }

        @Override
        public void rejectedComment(String regexp) {
            _log("Rejected comment: " + regexp);
        }
    }

    public static class LanguageVisitor {

        private static final String WHITESPACE_REGEXP = "^(\\[|\\[)?( |\\\\s|\\\\t|\\\\n|\\\\r)";
        private static final String OPERATOR_REGEXP = "^((?!\\\\s)[^\\s])+$";
        private static final String KEYWORD_REGEXP = "^[a-zA-Z0-9]+$";
        private static final String NUMBER_LITERAL_REGEXP = "([0-9]+|[0-9]*\\.[0-9]+|0x[0-9a-fA-F]+)";
        private static final String STRING_LITERAL_REGEXP = "\"[^\"]*\"";
        private final LanguageAcceptor acceptor;
        private VisitorState state = new VisitorState();

        public LanguageVisitor(LanguageAcceptor acceptor) {
            this.acceptor = acceptor;
        }

        public void visit(Language language) {
            state.setLanguage(language);
            // Detect comments
            for (SkipDef skip : language.getSkips()) {
                visit(skip);
            }
            // Detect language parts
            for (Concept concept : language.getConcepts()) {
                acceptor.acceptLanguage(language);
                this.visit(concept);
            }
        }

        private void visit(SkipDef skip) {
            _log("> SkipDef: " + skip.toString());
            String regexp = skip.getRegexp();
            if (regexp.length() > 0) {
                // TODO yin: This is very crude way of detecting non-whitespace characters in regexp
                if (!regexp.matches(WHITESPACE_REGEXP)) {
                    acceptor.acceptComment(regexp);
                } else {
                    acceptor.rejectedComment(regexp);
                }
            }
        }

        private void visit(Concept concept) {
            _log("> Concept: " + concept.getName());
            state.openConcept(concept);
            for (ConceptPattern conceptPattern : concept.getPatterns()) {
                visit(conceptPattern);
            }
            for (Property property : concept.getAbstractSyntax()) {
                visit(property);
            }
            for (Notation notation : concept.getConcreteSyntax()) {
                visit(notation);
            }
            state.closeConcept();
        }

        private void visit(ConceptPattern pattern) {
            _log(">>  ConceptPattern(@Operator): " + pattern.toString());
            if (pattern instanceof Operator) {
                state.setOperator(true);
            } else {
                // Ignore for highlighting: Enum, Parentheses
            }
        }

        private void visit(Property property) {
            _log(">> Property (fields+constructor.params): " + property.getName() + " type:"
                    + property.getType());
            for (PropertyPattern propPattern : property.getPatterns()) {
                visit(propPattern);
            }
        }

        private void visit(PropertyPattern propertyPattern) {
            _log(">>> PropertyPattern(@Identifier): " + propertyPattern.toString());
        }

        private void visit(Notation notation) {
            _log(">> Notation(constructor): " + notation + notation.12);
            for (NotationPart notationPart : notation.getParts()) {
                visit(notationPart);
            }
            for (NotationPattern notationPattern : notation.getPatterns()) {
                visit(notationPattern);
            }
        }

        private void visit(NotationPart notationPart) {
            _log(">>> NotationPart: " + notationPart);
            if (notationPart instanceof TokenPart) {
                visit((TokenPart) notationPart);
            } else if (notationPart instanceof PropertyReferencePart) {
                // name declarations
                visit((PropertyReferencePart) notationPart);
            } else if (notationPart instanceof LocalVariablePart) {
                // name references
                visit((LocalVariablePart) notationPart);
            }
        }

        private void visit(TokenPart tokenPart) {
            _log(">>>> TokenPart: " + tokenPart.toString());

            String token = tokenPart.getToken(), regexp;
            if (state.isOperator()) {
                if ((regexp = ifMatchesGetRegexp(token, OPERATOR_REGEXP)) != null) {
                    acceptor.acceptOperator(regexp);
                }
            } else {
                if ((regexp = ifMatchesGetRegexp(token, KEYWORD_REGEXP)) != null) {
                    acceptor.acceptKeyword(regexp);
                } else if ((regexp = ifMatchesGetRegexp(token, NUMBER_LITERAL_REGEXP)) != null) {
                    acceptor.acceptLiteral(regexp, LiteralType.NUMBER);
                } else if ((regexp = ifMatchesGetRegexp(token, STRING_LITERAL_REGEXP)) != null) {
                    acceptor.acceptLiteral(regexp, LiteralType.STRING);
                } //TODO yin: STRING, etc.
            }
        }

        private void visit(PropertyReferencePart propertyReference) {
            _log(">>>> PropertyReferencePart: " + propertyReference.toString());
            for (NotationPartPattern notationPartPatttern : propertyReference.getPatterns()) {
                visit(notationPartPatttern);
            }
        }

        private void visit(LocalVariablePart localVariablePart) {
            _log(">>>> LocalVariablePart: " + localVariablePart.toString());
            for (NotationPartPattern notationPartPatttern : localVariablePart.getPatterns()) {
                visit(notationPartPatttern);
            }
        }

        private void visit(NotationPartPattern notationPartPatttern) {
            _log(">>>>> NotationPartPattern " + notationPartPatttern);
            if (notationPartPatttern instanceof References) {
                References ref = (References) notationPartPatttern;
                _log("      ref.property = " + ref.getProperty().getName());
            }
        }

        private void visit(NotationPattern notationPattern) {
            _log(">>> NotationPattern(@Factory) " + notationPattern.toString());
        }

        // Utils
        private String ifMatchesGetRegexp(String token, String superRegexp) {
            String tokenRegexp = token;
            TokenDef tokenDef = state.getLanguage().getToken(token);
            if (tokenDef != null && tokenDef.getRegexp() != null) {
                tokenRegexp = tokenDef.getRegexp();
            }
            return tokenRegexp.matches(superRegexp) ? tokenRegexp : null;
        }

        static class VisitorState {
            private VisitorState previous;
            private Language language;
            private Concept concept;
            private boolean operator;

            public VisitorState getPrevious() {
                return previous;
            }

            public void setPrevious(VisitorState previous) {
                this.previous = previous;
            }

            public Language getLanguage() {
                return language;
            }

            public void setLanguage(Language language) {
                this.language = language;
            }

            public Concept getConcept() {
                return concept;
            }

            public void openConcept(Concept concept) {
                this.concept = concept;
            }

            public void closeConcept() {
                concept = null;
                operator = false;
            }

            public boolean isOperator() {
                return operator;
            }

            public void setOperator(boolean operator) {
                this.operator = operator;
            }
        }
    }

    private static void _log(Object msg) {
        System.out.println(msg);
    }
}
