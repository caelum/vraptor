package br.com.caelum.vraptor.http;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.VRaptorException;

/**
 * EncodingHandler that uses Encoding from web.xml
 * @author Lucas Cavalcanti
 *
 */
public class WebXmlEncodingHandler implements EncodingHandler {

	private final String encoding;

	public WebXmlEncodingHandler(String encoding) {
		this.encoding = encoding;
	}

	public void setEncoding(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding(encoding);
			response.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new VRaptorException(e);
		}
	}

}
