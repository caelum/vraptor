package br.com.caelum.vraptor.view;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class DefaultXmlSerialization implements XmlSerialization {

	private final HttpServletResponse response;
	
	public DefaultXmlSerialization(HttpServletResponse response) {
		this.response = response;
	}
	
	public <T> XmlSerializer from(T object) throws IOException {
		XmlSerializer serializer = new XmlSerializer(response.getWriter());
		return serializer;
	}
	
}
