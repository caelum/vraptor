package br.com.caelum.vraptor.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EncodingHandler that does nothing
 * @author Lucas Cavalcanti
 *
 */
public class NullEncodingHandler implements EncodingHandler{
	public void setEncoding(HttpServletRequest request, HttpServletResponse response) {
	}
}
