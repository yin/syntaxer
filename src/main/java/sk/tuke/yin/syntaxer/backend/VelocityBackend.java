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

import com.google.common.collect.ImmutableList;

public class VelocityBackend {
	private static final String DEFUALT_TEMPLATE = "kate.vm";
	public final String templateName;
	private final Writer writer;

	@Deprecated
	public VelocityBackend() {
		this.templateName = DEFUALT_TEMPLATE;
		this.writer = new OutputStreamWriter(System.out);
	}

	public VelocityBackend(String templateName, Writer writer) {
		this.templateName = templateName;
		this.writer = writer;
	}

	public static VelocityContext modelToContext(HighlightingModel model) {
		VelocityContext context = new VelocityContext();
		context.put("name", model.getLanguageName());
		context.put("keywords",
				ImmutableList.builder().addAll(model.getKeywords()).build());
		return context;
	}

	public Object write(HighlightingModel model) throws IOException {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		ve.init();

		VelocityContext context = modelToContext(model);
		Template t = null;
		t = ve.getTemplate(templateName);
		t.merge(context, writer);
		t.process();
		writer.flush();
		System.out.println(writer.toString());
		return writer;
	}

	public static VelocityBackend getStdoutBackend() {
		return new VelocityBackend(DEFUALT_TEMPLATE, new OutputStreamWriter(
				System.out));
	}

	public static VelocityBackend getStringBackend() {
		return new VelocityBackend(DEFUALT_TEMPLATE, new StringWriter());
	}

	public static VelocityBackend getFileBackend(Path path) throws IOException {
		return new VelocityBackend(DEFUALT_TEMPLATE, new FileWriter(
				path.toFile()));
	}
}
