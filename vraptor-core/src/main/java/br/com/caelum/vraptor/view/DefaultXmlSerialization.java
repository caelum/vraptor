package br.com.caelum.vraptor.view;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.rest.Restfulie;

/**
 * The default implementation of xml serialization.<br/>
 * It will set the content type to application/xml by default.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
@Component
@RequestScoped
public class DefaultXmlSerialization implements XmlSerialization {

	private final HttpServletResponse response;
	private final Restfulie restfulie;
	private final Configuration config;

	public DefaultXmlSerialization(HttpServletResponse response, Restfulie restfulie, Configuration config) {
		this.response = response;
		this.restfulie = restfulie;
		this.config = config;
	}

	public <T> XmlSerializer from(T object) {
		response.setContentType("application/xml");
		try {
			XmlSerializer serializer = new XmlSerializer(null, response.getWriter(), restfulie, config).from(object);
			return serializer;
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data",e);
		}
	}

}
