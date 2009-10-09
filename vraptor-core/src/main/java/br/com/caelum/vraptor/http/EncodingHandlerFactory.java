package br.com.caelum.vraptor.http;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@ApplicationScoped
public class EncodingHandlerFactory implements ComponentFactory<EncodingHandler>{


	private final EncodingHandler handler;

	public EncodingHandlerFactory(ServletContext context) {
		String encoding = new BasicConfiguration(context).getEncoding();
		this.handler = encoding == null? new NullEncodingHandler() : new WebXmlEncodingHandler(encoding);
	}

	public EncodingHandler getInstance() {
		return handler;
	}

}
