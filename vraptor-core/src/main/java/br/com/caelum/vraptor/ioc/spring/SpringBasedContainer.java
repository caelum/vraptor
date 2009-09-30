
package br.com.caelum.vraptor.ioc.spring;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;

import br.com.caelum.vraptor.ioc.AbstractComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainer extends AbstractComponentRegistry implements Container {

    private VRaptorApplicationContext applicationContext;

    private final List<Class<?>> toRegister = new ArrayList<Class<?>>();

    public SpringBasedContainer(ApplicationContext parentContext, String... basePackages) {
		String[] packages = {"br.com.caelum.vraptor"};
        if (basePackages.length > 0) {
            packages = basePackages;
        }
        applicationContext = new VRaptorApplicationContext(this, packages);
        applicationContext.setParent(parentContext);
    }

    public void register(Class<?> requiredType, Class<?> componentType) {
    	if (applicationContext.isActive()) {
			applicationContext.register(componentType);
		} else {
			toRegister.add(componentType);
		}
    }

    public List<Class<?>> getToRegister() {
		return toRegister;
	}

    public <T> T instanceFor(Class<T> type) {
        return applicationContext.getBean(type);
    }

    public void start(ServletContext context) {
        applicationContext.setServletContext(context);
        applicationContext.refresh();
        applicationContext.start();
    }

    public void stop() {
        applicationContext.stop();
        applicationContext.destroy();
        applicationContext = null;
    }

}
