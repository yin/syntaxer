package sk.tuke.yin.syntaxer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Hello world!
 */
@Deprecated
public class TestingRunner {
	private static Writer writer;

	public static void main(String[] args) throws IOException {
/*		if (args.length > 0) {
			if ("-".equals(args[0])) {
				writer = new StringWriter();
			} else {
				writer = new FileWriter(new File(args[0]));
			}
		}
		try {
			ColorMapping colors = new ColorMapping();
			HighlightingModel model = new HighlightingModel();
			VelocityBackend.write(model);
		} finally {
			writer.close();
		}
		*/
	}

	public static Writer getWriter() {
		if (writer == null) {
			writer = new StringWriter();
		}
		return writer;
	}
}
