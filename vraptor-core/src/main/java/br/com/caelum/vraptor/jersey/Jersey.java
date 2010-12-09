package br.com.caelum.vraptor.jersey;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.InterceptorStack;

/**
 * A jersey to vraptor interface.
 * 
 * @author guilherme silveira
 */
public interface Jersey {

	boolean findComponent(InterceptorStack stack,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException;

	/**
	 * Whether this request is a jersey based one.
	 * 
	 * @param request
	 *            the current request
	 * @return true if the request is a jersey based, false otherwise
	 */
	boolean isMine(HttpServletRequest request);

	/**
	 * Returns the resource instance for this request.
	 * 
	 * @param request
	 *            the request
	 * @return the resource instance
	 */
	Object instantiate(HttpServletRequest request);

	/**
	 * Continues the execution of this request, using the specified instance.
	 */
	void execute(HttpServletRequest request, Object instance);

	boolean shouldInstantiate(Class<?> type);

}