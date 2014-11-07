package sk.tuke.yin.syntaxer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import sk.tuke.yin.syntaxer.model.ColorMapping;
import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.Notation;
import yajco.model.Property;
import yajco.model.pattern.ConceptPattern;

import com.google.common.collect.ImmutableList;

/**
 * Hello world!
 */
public class TestingRunner {
	private static Writer writer;

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			if ("-".equals(args[0])) {
				writer = new StringWriter();
			} else {
				writer = new FileWriter(new File(args[0]));
			}
		}
		try {
			ColorMapping colors = new ColorMapping();
			Velocity.init();
			VelocityContext context = new VelocityContext();
			Language lang = null;//yajco.exemple.sml.parser.StateMachineParser;
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
			context.put("name", "Kate01");
			context.put(
					"keywords",
					ImmutableList.builder().add("class").add("function")
							.add("for").add("var").build());
			Template t = Velocity.getTemplate("test.vm");
			Writer w = getWriter();

			t.merge(context, w);
			String ret = w.toString();
			System.out.println(ret);
		} finally {
			writer.close();
		}
	}

	private static Writer getWriter() {
		if (writer == null) {
			writer = new StringWriter();
		}
		return writer;
	}
}
