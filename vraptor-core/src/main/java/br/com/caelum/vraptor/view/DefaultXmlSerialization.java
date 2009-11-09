package br.com.caelum.vraptor.view;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;

/**
 * The default implementation of xml serialization.<br/>
 * It will set the content type to application/xml by default.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
@Component
public class DefaultXmlSerialization implements XmlSerialization {

	final HttpServletResponse response;

	public DefaultXmlSerialization(HttpServletResponse response) {
		this.response = response;
	}

	public <T> XmlSerializer from(T object) throws IOException {
		response.setContentType("application/xml");
		XmlSerializer serializer = new XmlSerializer(response.getWriter()).from(object);
		return serializer;
	}

}
