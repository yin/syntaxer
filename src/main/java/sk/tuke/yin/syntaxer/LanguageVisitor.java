package sk.tuke.yin.syntaxer;

import sk.tuke.yin.syntaxer.LanguageAcceptor.LiteralType;
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
import yajco.model.pattern.impl.Identifier;
import yajco.model.pattern.impl.Operator;
import yajco.model.pattern.impl.References;

public class LanguageVisitor {
    private static final String WHITESPACE_REGEXP = "^(\\[|\\[)?( |\\\\s|\\\\t|\\\\n|\\\\r)";
    private static final String BLOCK_COMMENT_REGEXP = "[^n]$";
    private static final String OPERATOR_REGEXP = "^((?!\\\\s)[^\\s])+$";
    private static final String KEYWORD_REGEXP = "^[a-zA-Z_]+[a-zA-Z0-9_\\-]*$";
    private static final String NUMBER_LITERAL_REGEXP = "([0-9]+|[0-9]*\\.[0-9]+|0x[0-9a-fA-F]+)";
    private static final String STRING_LITERAL_REGEXP = "\"[^\"]*\"";

    private final LanguageAcceptor acceptor;
    private LanguageVisitor.VisitorState state = new VisitorState();

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
        HighlightingCompiler._log("> SkipDef: " + skip.toString());
        String regexp = skip.getRegexp();
        if (regexp.length() > 0) {
            // TODO yin: This is very crude way of detecting non-whitespace characters in regexp
            if (!regexp.matches(WHITESPACE_REGEXP)) {
                // TODO yin: Fixed size prefixes and sufixes are not a good idea
                if (regexp.matches(BLOCK_COMMENT_REGEXP) && regexp.length() > 4) {
                    int len = regexp.length();
                    String sufix = regexp.substring(len - 2, len);
                    acceptor.acceptBlockComment(regexp.substring(0, 2), sufix);
                } else {
                    int prefixLen = Math.min(regexp.indexOf("."), regexp.indexOf('['));
                    acceptor.acceptLineComment(regexp.substring(0, prefixLen));
                }
            } else {
                acceptor.acceptWhitespace(regexp);
            }
        }
    }

    private void visit(Concept concept) {
        HighlightingCompiler._log("> Concept: " + concept.getName());
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

    // Concept children - ConceptPattern, Property, Notation
    private void visit(ConceptPattern pattern) {
        HighlightingCompiler._log(">>  ConceptPattern(@Operator): " + pattern.toString());
        if (pattern instanceof Operator) {
            state.setOperator(true);
        } else {
            // Ignore for highlighting: Enum, Parentheses
        }
    }

    private void visit(Property property) {
        HighlightingCompiler._log(">> Property (fields+constructor.params): " + property.getName()
                + " type:" + property.getType());
        for (PropertyPattern propPattern : property.getPatterns()) {
            if (propPattern instanceof Identifier) {
                Identifier identifier = (Identifier) propPattern;
                visit(identifier);
            } else {
                visit(propPattern);
            }
        }
    }

    private void visit(Notation notation) {
        HighlightingCompiler._log(">> Notation(constructor): " + notation);
        for (NotationPart notationPart : notation.getParts()) {
            visit(notationPart);
        }
        for (NotationPattern notationPattern : notation.getPatterns()) {
            visit(notationPattern);
        }
    }
    
    // Concept grandchildren - PropertyPattern, NotationPart, NotationPattern
    private void visit(PropertyPattern propertyPattern) {
        HighlightingCompiler._log(">>> PropertyPattern(@Identifier): " + propertyPattern.toString());
    }

    private void visit(Identifier identifier) {
        HighlightingCompiler._log(">>>> Identifier(PropertyPattern): unique:" + identifier.getUnique());
    }


    private void visit(NotationPart notationPart) {
        HighlightingCompiler._log(">>> NotationPart: " + notationPart);
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

    private void visit(NotationPattern notationPattern) {
        HighlightingCompiler._log(">>> NotationPattern(@Factory) " + notationPattern.toString());
    }

    private void visit(TokenPart tokenPart) {
        HighlightingCompiler._log(">>>> TokenPart: " + tokenPart.toString());

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
        HighlightingCompiler._log(">>>> PropertyReferencePart: " + propertyReference.toString());
        for (NotationPartPattern notationPartPatttern : propertyReference.getPatterns()) {
            visit(notationPartPatttern);
        }
    }

    private void visit(LocalVariablePart localVariablePart) {
        HighlightingCompiler._log(">>>> LocalVariablePart: " + localVariablePart.toString());
        for (NotationPartPattern notationPartPatttern : localVariablePart.getPatterns()) {
            visit(notationPartPatttern);
        }
    }

    private void visit(NotationPartPattern notationPartPatttern) {
        HighlightingCompiler._log(">>>>> NotationPartPattern " + notationPartPatttern);
        if (notationPartPatttern instanceof References) {
            References ref = (References) notationPartPatttern;
            HighlightingCompiler._log("      ref.property = " + ref.getProperty().getName());
        }
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
        private LanguageVisitor.VisitorState previous;
        private Language language;
        private Concept concept;
        private boolean operator;

        public LanguageVisitor.VisitorState getPrevious() {
            return previous;
        }

        public void setPrevious(LanguageVisitor.VisitorState previous) {
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