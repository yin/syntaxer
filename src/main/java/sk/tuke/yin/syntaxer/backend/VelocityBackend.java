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
import com.google.common.collect.ImmutableMap;

public class VelocityBackend {
	public static final ImmutableMap<String, String> targetEditors = new ImmutableMap.Builder<String, String>()
			.put("kate", "kate.vm").put("codemirror", "codemirror.js.vm")
			.build();
	private final String templateName;
	private final Writer writer;

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
		Template template = ve.getTemplate(templateName);
		template.merge(context, writer);
		template.process();
		writer.flush();
		return writer;
	}

	public static class BackendBuilder {
		public enum WriteTo {
			STDOUT, STRING, FILE, CUSTOM;
		}

		WriteTo writeTo;
		Object[] writeToCustom;
		Path customFile;
		String editor;

		public BackendBuilder() {
		}

		public BackendBuilder writeTo(WriteTo writeTo, Object... custom) {
			this.writeTo = writeTo;
			this.writeToCustom = custom;
			return this;
		}

		public BackendBuilder targetEditor(String editor) {
			this.editor = editor;
			return this;
		}

		public VelocityBackend build() throws IOException {
			String template = null;
			Writer writer = null;
			if (editor == null) {
				throw new IllegalStateException("Target editor not specified.");
			} else {
				if (targetEditors.containsKey(editor)) {
					template = targetEditors.get(editor);
				} else {
					throw new IllegalArgumentException("Unsupported Target Editor was specified.");
				}
			}
			if (writeTo == null) {
				throw new IllegalStateException("Output writer not specified.");
			} else {
				switch (writeTo) {
				case STDOUT:
					writer = new OutputStreamWriter(System.out);
					break;
					case STRING:
						writer = new StringWriter();
						break;
					case FILE:
						if (writeToCustom.length == 1 && writeToCustom[0] instanceof Path) {
							Path path = (Path) writeToCustom[0]; 
							writer = new FileWriter(path.toFile());
						} else {
							throw new IllegalArgumentException("File path not specified. Path can be "
									+ "specified as single optional argument to writeTo() call.");
						}
					break;
					case CUSTOM:
						try {
							if (writeToCustom.length > 1) {
								throw new IllegalStateException("More than one optional arguments"
										+ "supplied as custom writer.");
							}
							writer = (Writer) writeToCustom[0];
						}catch(Exception e) {
							throw new IllegalArgumentException("Can not use custom Writer. Either"
									+ "not supplied or not a Writer.", e);
						}
						break;
				}
			}
			return new VelocityBackend(template, writer);
		}
	}
}
