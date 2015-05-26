package sk.tuke.yin.syntaxer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.annotation.processing.Filer;

import sk.tuke.yin.syntaxer.backend.EditorRepository;
import sk.tuke.yin.syntaxer.backend.VelocityBackend;
import sk.tuke.yin.syntaxer.backend.VelocityBackend.BackendBuilder.WriteTo;
import sk.tuke.yin.syntaxer.model.ColorMapping;
import sk.tuke.yin.syntaxer.model.HighlightingModel;
import sk.tuke.yin.syntaxer.model.HighlightingModel.LiteralType;
import yajco.generator.parsergen.CompilerGenerator;
import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.Notation;
import yajco.model.NotationPart;
import yajco.model.Property;
import yajco.model.SkipDef;
import yajco.model.TokenDef;
import yajco.model.TokenPart;
import yajco.model.pattern.ConceptPattern;
import yajco.model.pattern.NotationPattern;
import yajco.model.pattern.PropertyPattern;
import yajco.model.pattern.impl.Identifier;
import yajco.model.pattern.impl.Operator;

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
        ColorMapping colors = new ColorMapping();
        HighlightingModel model = new HighlightingModel();
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
        String id = System.getenv(EDITOR_ID_VAR);
        if (id == null) {
            id = DEFAULT_EDITOR;
        }
        return id;
    }

    private Path getOutputPath(HighlightingModel model) {
        String languageName = model.getLanguageName();
        String suffix = EditorRepository.instance().getEditorForId(getEditorId()).getOutputSuffix();
        return Paths.get(languageName + '.' + suffix);
    }

    public static interface LanguageAcceptor {
        public void acceptLanguage(Language language);
        
        public void acceptKeyword(String keyword);

        public void acceptOperator(String operator);
        
        public void acceptLiteral(String operator, LiteralType type);

        public void acceptComment(String regexp);

        public void acceptNonCommentSkipSymbol(String regexp);
    }

    public static class ModelAcceptor implements LanguageAcceptor {
        private final HighlightingModel model;

        public ModelAcceptor(HighlightingModel model) {
            this.model = model;
        }

        @Override
        public void acceptLanguage(Language language) {
            model.setLanguageName(language.getName());
        }

        @Override
        public void acceptKeyword(String keyword) {
            model.addKeyword(keyword);
            System.out.println("ACCEPTED keyword: " + keyword);
        }

        @Override
        public void acceptOperator(String operator) {
            model.addOperator(operator);
            System.out.println("ACCEPTED operator: " + operator);
        }

        @Override
        public void acceptLiteral(String literal, LiteralType type) {
            model.addLiteral(literal, type);
            System.out.println("ACCEPTED literal: " + literal);
        }

        @Override
        public void acceptComment(String regexp) {
            if (!regexp.trim().isEmpty()) {
                model.addComment(regexp);
                System.out.println("ACCEPTED comment: " + regexp);
            } //tODO yin: Log warning here
        }

        @Override
        public void acceptNonCommentSkipSymbol(String regexp) {
            System.out.println("IGNORED symbol: " + regexp);
        }
    }

    public static class LanguageVisitor {

        private static final String OPERATOR_REGEXP = "^((?!\\\\s)[^\\s])+$";
        private static final String KEYWORD_REGEXP = "^[a-zA-Z_]+[a-zA-Z0-9_\\-]*$";
        private static final String LITERAL_REGEXP = "([0-9]+|[0-9]*\\.[0-9]+|0x[0-9a-fA-F]+)";
        private final LanguageAcceptor acceptor;
        private Language language;
        
        enum LanguagePart {
            // Unmarked - KEYWORDs, LITERALs etc.
            UNDETERMINED,
            // ConceptPattern's
            OPERATOR;
        }
        
        public LanguageVisitor(LanguageAcceptor acceptor) {
            this.acceptor = acceptor;
        }

        public void visit(Language language) {
            this.language = language;
            for (Concept concept : language.getConcepts()) {
                acceptor.acceptLanguage(language);
                this.visit(concept);
            }
            for (SkipDef skip : language.getSkips()) {
                visit(skip);
            }
        }

        private void visit(SkipDef skip) {
           String regexp = skip.getRegexp(); 
           if (regexp.length() > 0) {
               // TODO yin: This is very crude way of detecting non-whitespace characters in regexp
               if (!regexp.matches("^(\\[|\\[)?(\\\\s|\\\\t|\\\\n|\\\\r)")) {
                   acceptor.acceptComment(regexp);
               } else {
                   acceptor.acceptNonCommentSkipSymbol(regexp);
               }
           }
        }

        public void visit(Concept concept) {
            System.out.println(">> Concept: " + concept.getName());
            // TODO: Consider using EnumSet, or rule it out completely
            LanguagePart languagePart = LanguagePart.UNDETERMINED;
            for (ConceptPattern conceptPattern: concept.getPatterns()) {
                LanguagePart newLanguagePart = visit(conceptPattern);
                if (languagePart == LanguagePart.UNDETERMINED ) {
                    languagePart = newLanguagePart;
                }
            }
            for (Property property : concept.getAbstractSyntax()) {
                visit(property);
            }
            for (Notation notation : concept.getConcreteSyntax()) {
                visit(notation, languagePart);
            }
        }

        // Concept children - ConceptPattern, Property, Notation
        private LanguagePart visit(ConceptPattern pattern) {
            System.out.println(">>>  ConceptPattern: " + pattern.toString());
            // Ignore for highlighting: Enum, Parentheses
            if (pattern instanceof Operator ) {
                return LanguagePart.OPERATOR;
            }
            return LanguagePart.UNDETERMINED;
        }
        
        private void visit(Property property) {
            System.out.println(">>> Property: " + property.getName() + " type:" + property.getType());
            for (PropertyPattern propPattern : property.getPatterns()) {
                if (propPattern instanceof Identifier) {
                    Identifier identifier = (Identifier) propPattern;
                    visit(identifier);
                } else {
                    visit(propPattern);
                }
            }
        }

        private void visit(Notation notation, LanguagePart languagePart) {
            System.out.println(">>> Notation: " + notation);
            for (NotationPart notationPart : notation.getParts()) {
                visit(notationPart, languagePart);
            }
            for (NotationPattern notationPattern : notation.getPatterns()) {
                visit(notationPattern);
            }
        }

        // Concept grandchildren - PropertyPattern, NotationPart, NotationPattern
        private void visit(PropertyPattern propertyPattern) {
            System.out.println(">>>> ?(PropertyPattern): " + propertyPattern.toString());
        }

        private void visit(Identifier identifier) {
            System.out.println(">>>> Identifier(PropertyPattern): unique:" + identifier.getUnique());
        }

        private void visit(NotationPart notationPart, LanguagePart languagePart) {
            System.out.println(">>>> NotationPart: " + notationPart);
            if (notationPart instanceof TokenPart) {
                visit((TokenPart) notationPart, languagePart);
            }
        }

        private void visit(NotationPattern notationPattern) {
            System.out.println(">>>> NotationPattern" + notationPattern.toString());
        }
        
        // grandchildren: NotationPart's - TokenPart - TODO: LocalVariablePart, PropertyReferencePart
        private void visit(TokenPart tokenPart, LanguagePart languagePart) {
            if (languagePart == LanguagePart.UNDETERMINED) {
                String token = tokenPart.getToken(), regexp;
                if ((regexp = ifMatchesGetRegexp(token, KEYWORD_REGEXP)) != null) {
                    acceptor.acceptKeyword(regexp);
                } else if ((regexp = ifMatchesGetRegexp(token, LITERAL_REGEXP)) != null) {
                    // The token might be an identifier string
                    
                }
                
            } else if (languagePart == LanguagePart.OPERATOR) {
                String token = tokenPart.getToken(), regexp;
                if ((regexp = ifMatchesGetRegexp(token, OPERATOR_REGEXP)) != null) {
                    acceptor.acceptOperator(regexp);
                }
            }
        }
        
        // Utils
        private String ifMatchesGetRegexp(String token, String superRegexp) {
            String tokenRegexp = token;
            TokenDef tokenDef = language.getToken(token);
            if (tokenDef != null) {
                tokenRegexp = tokenDef.getRegexp();
            }
            return tokenRegexp.matches(superRegexp) ? token : null;
        }
    }
}
