package br.com.caelum.vraptor.mydvds.provider;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.util.hibernate.SessionCreator;
import br.com.caelum.vraptor.util.hibernate.SessionFactoryCreator;

/**
 * You can extend SpringProvider (or PicoProvider) in order to register
 * optional components, like the Hibernate Session and SessionFactory
 * Component Factories.
 * Don't forget to register it on your web.xml:
 * <context-param>
 *      <param-name>br.com.caelum.vraptor.provider</param-name>
 *      <param-value>br.com.caelum.vraptor.mydvds.provider.CustomProvider</param-value>
 * </context-param>
 * @author Lucas Cavalcanti
 *
 */
public class CustomProvider extends SpringProvider {

	@Override
	protected void registerCustomComponents(ComponentRegistry registry) {
		registry.register(SessionCreator.class, SessionCreator.class);
		registry.register(SessionFactoryCreator.class, SessionFactoryCreator.class);
	}

}
