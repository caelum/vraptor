package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

import javax.servlet.ServletContext;

/**
 * @author Fabio Kung
 */
public class SpringProvider implements ContainerProvider {
    public static final String BASE_PACKAGES_PARAMETER_NAME = "br.com.caelum.vraptor.spring.packages";

    private SpringBasedContainer container;

    public Container provide(VRaptorRequest request) {
        VRaptorRequestHolder.setRequestForCurrentThread(request);
        return container;
    }

    public void stop() {
        container.stop();
    }

    public void start(ServletContext context) {
        String packagesParameter = context.getInitParameter(BASE_PACKAGES_PARAMETER_NAME);

        String[] packages = null;
        if (packagesParameter == null) {
            throw new MissingConfigurationException("org.vraptor.spring.packages init-parameter not found in web.xml");
        } else {
            packages = packagesParameter.split(",");
        }

        container = new SpringBasedContainer(packages);
        container.start(context);
    }

}
