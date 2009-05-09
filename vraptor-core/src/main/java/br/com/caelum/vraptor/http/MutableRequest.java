package br.com.caelum.vraptor.http;

import javax.servlet.http.HttpServletRequest;

/**
 * A request capable of receiving extra parameters.
 * 
 * @author guilherme silveira
 */
public interface MutableRequest extends HttpServletRequest {

	void setParameter(String key, String... value);

}
