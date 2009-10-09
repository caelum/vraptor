package br.com.caelum.vraptor.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Component for setting request and response encoding
 * @author Lucas Cavalcanti
 *
 */
public interface EncodingHandler {

	void setEncoding(HttpServletRequest request, HttpServletResponse response);
}
