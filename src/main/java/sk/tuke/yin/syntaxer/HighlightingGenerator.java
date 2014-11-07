package sk.tuke.yin.syntaxer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.annotation.processing.Filer;

import sk.tuke.yin.syntaxer.backend.VelocityBackend;
import sk.tuke.yin.syntaxer.model.ColorMapping;
import sk.tuke.yin.syntaxer.model.HighlightingModel;
import yajco.generator.parsergen.CompilerGenerator;
import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.Notation;
import yajco.model.NotationPart;
import yajco.model.Property;
import yajco.model.TokenDef;
import yajco.model.TokenPart;
import yajco.model.pattern.NotationPattern;
import yajco.model.pattern.PropertyPattern;

public class HighlightingGenerator implements CompilerGenerator {

	HighlightingModel model = new HighlightingModel();

	@Override
	public void generateFiles(Language lang, Filer arg1, Properties arg2,
			String arg3) {
		processLanguage(lang);
	}

	@Override
	public void generateFiles(Language lang, Filer arg1, Properties arg2) {
		processLanguage(lang);
	}

	private void processLanguage(Language language) {
		ColorMapping colors = new ColorMapping();
		HighlightingModel model = new HighlightingModel();
		ModelAcceptor acceptor = new ModelAcceptor(model);
		LanguageVisitor visitor = new LanguageVisitor(acceptor);
		visitor.visit(language);
		try {
			VelocityBackend backend = VelocityBackend.getFileBackend(geOutputPath(model));
			backend.write(model);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write output.", e);
		}
	}

	private Path geOutputPath(HighlightingModel model) {
		return Paths
				.get(model.getLanguageName() + ".Syntax");
	}

	public static interface LanguageAcceptor {
		public void acceptKeyword(String keyword);

		public void acceptOperator(String operator);

		public void acceptSeparator(String separator);

		public void acceptFunction(String keyword);

		public void acceptLanguage(Language language);
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
		}

		@Override
		public void acceptOperator(String operator) {
			// TODO Auto-generated method stub
		}

		@Override
		public void acceptSeparator(String separator) {
			// TODO Auto-generated method stub
		}

		@Override
		public void acceptFunction(String keyword) {
			// TODO Auto-generated method stub
		}

	}

	public static class LanguageVisitor {

		private LanguageAcceptor acceptor;

		public LanguageVisitor(LanguageAcceptor acceptor) {
			this.acceptor = acceptor;
		}

		public void visit(Language language) {
			for (Concept concept : language.getConcepts()) {
				acceptor.acceptLanguage(language);
				this.visit(concept);
			}
		}

		public void visit(Concept concept) {
			for (Property p : concept.getAbstractSyntax()) {
				visit(p);
			}
			for (Notation notation : concept.getConcreteSyntax()) {
				visit(notation);
			}
		}

		private void visit(Property property) {
			for (PropertyPattern propPattern : property.getPatterns()) {
				visit(propPattern);
			}
		}

		private void visit(PropertyPattern propPattern) {
		}

		private void visit(Notation notation) {
			for (NotationPart notationPart : notation.getParts()) {
				visit(notationPart);
			}
			for (NotationPattern notationPattern : notation.getPatterns()) {
				visit(notationPattern);
			}
		}

		private void visit(NotationPart notationPart) {
			if (notationPart instanceof TokenPart) {
				TokenPart tp = (TokenPart) notationPart;
				String token = tp.getToken();
				if (token.matches("^[a-zA-Z0-9]+$")) {
					acceptor.acceptKeyword(token);
				}
			}
		}

		private void visit(NotationPattern notationPattern) {
		}
	}
}
