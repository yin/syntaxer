package sk.tuke.yin.syntaxer;

import java.util.Properties;

import javax.annotation.processing.Filer;

import yajco.generator.parsergen.CompilerGenerator;
import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.Notation;
import yajco.model.Property;
import yajco.model.pattern.ConceptPattern;

public class HighlightingGenerator implements CompilerGenerator {

	@Override
	public void generateFiles(Language lang, Filer arg1, Properties arg2,
			String arg3) {
		System.out.println("    >>>> <<<<    ");
		for (Concept concept : lang.getConcepts()) {
			for (ConceptPattern pattern : concept.getPatterns()) {
				System.out.println(pattern);
			}
			for (Property pattern : concept.getAbstractSyntax()) {
				System.out.println(pattern);
			}
			for (Notation pattern : concept.getConcreteSyntax()) {
				System.out.println(pattern);
			}
		}
	}

	@Override
	public void generateFiles(Language lang, Filer arg1, Properties arg2) {
		System.out.println("     <<<<<<<<< >>>>>>>>>.");
		
		for (Concept concept : lang.getConcepts()) {
			for (ConceptPattern pattern : concept.getPatterns()) {
				System.out.println(pattern);
			}
			for (Property pattern : concept.getAbstractSyntax()) {
				System.out.println(pattern);
			}
			for (Notation pattern : concept.getConcreteSyntax()) {
				System.out.println(pattern);
			}
		}
	}

}
