package br.com.caelum.vraptor.ioc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.Request;
import br.com.caelum.vraptor.resource.ResourceMethod;

public interface Container {

    void start();

    void stop();

    /**
	 * Prepares a request execution for this request/response pair.
	 */
	Request prepare(ResourceMethod method, HttpServletRequest request, HttpServletResponse response);

    /**
     * Retrieves the appropriate instance for the given class.
     *
     * @param type of the required component
     * @param <T>
     * @return the registered component
     */
    <T> T instanceFor(Class<T> type);

}
