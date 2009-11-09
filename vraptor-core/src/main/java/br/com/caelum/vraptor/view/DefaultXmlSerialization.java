package br.com.caelum.vraptor.view;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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

	public DefaultXmlSerialization(HttpServletResponse response, Restfulie restfulie) {
		this.response = response;
		this.restfulie = restfulie;
	}

	public <T> XmlSerializer from(T object) throws IOException {
		response.setContentType("application/xml");
		XmlSerializer serializer = new XmlSerializer(null, response.getWriter(), restfulie).from(object);
		return serializer;
	}

}
