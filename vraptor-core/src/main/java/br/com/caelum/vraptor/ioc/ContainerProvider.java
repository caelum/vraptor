
package br.com.caelum.vraptor.ioc;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;

public interface ContainerProvider {

    <T> T provideForRequest(RequestInfo vraptorRequest, Execution<T> execution);

    void stop();

    void start(ServletContext context);

}
