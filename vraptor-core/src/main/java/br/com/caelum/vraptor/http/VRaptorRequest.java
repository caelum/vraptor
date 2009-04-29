package br.com.caelum.vraptor.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A request capable of adding new parameters.
 * @author guilherme silveira
 *
 */
public class VRaptorRequest extends HttpServletRequestWrapper{

	public VRaptorRequest(HttpServletRequest request) {
		super(request);
	}

}
