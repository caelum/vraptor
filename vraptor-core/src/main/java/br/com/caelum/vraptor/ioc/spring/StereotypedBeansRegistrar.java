
package br.com.caelum.vraptor.ioc.spring;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
public class StereotypedBeansRegistrar implements ApplicationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StereotypedBeansRegistrar.class);
	private final List<StereotypeHandler> stereotypeHandlers;

    public StereotypedBeansRegistrar(List<StereotypeHandler> stereotypeHandlers) {
		this.stereotypeHandlers = stereotypeHandlers;
    }

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			ContextRefreshedEvent contextRefreshedEvent = (ContextRefreshedEvent) event;
			handleRefresh(contextRefreshedEvent.getApplicationContext());
		}
	}

	private void handleRefresh(ApplicationContext beanFactory) {
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String name : beanDefinitionNames) {
			Class<?> beanType = beanFactory.getType(name);
			LOGGER.debug("scanning bean with type: " + beanType + ", to see if it is a component candidate");
			
			for (StereotypeHandler handler : stereotypeHandlers) {
				if (beanType.isAnnotationPresent(handler.stereotype())) {
					handler.handle(beanType);
				}
			}
		}
	}
}
