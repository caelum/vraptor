package br.com.caelum.vraptor.serialization.xstream;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.JSONPSerialization;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

/**
 *
 * Default implementation for JSONPSerialization that decorates default jsonSerialization output
 * adding the callback padding
 *
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 * @since 3.2.1
 *
 */
public class XStreamJSONPSerialization implements JSONPSerialization {

	private final HttpServletResponse response;
	private final TypeNameExtractor extractor;
	private final ProxyInitializer initializer;

	public XStreamJSONPSerialization(HttpServletResponse response, TypeNameExtractor extractor, ProxyInitializer initializer) {
		this.response = response;
		this.extractor = extractor;
		this.initializer = initializer;

	}

	public JSONSerialization withCallback(final String callbackName) {
		return new XStreamJSONSerialization(response, extractor, initializer) {
			@Override
			protected SerializerBuilder getSerializer() {
				try {
					final PrintWriter writer = response.getWriter();
					final StringWriter out = new StringWriter();
					return new XStreamSerializer(getXStream(), new PrintWriter(out), extractor, initializer) {
						@Override
						public void serialize() {
							super.serialize();
							writer.append(callbackName).append("(").append(out.getBuffer()).append(")");
							writer.close();
						}
					};
				} catch (IOException e) {
					throw new ResultException("Unable to serialize data", e);
				}
			}
		};
	}

}
