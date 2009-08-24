package br.com.caelum.vraptor.util.hibernate;

import javax.annotation.PreDestroy;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * Creates a SessionFactory from default resource /hibernate.cfg.xml, using
 * AnnotationConfiguration, and provides it to container
 * @author Lucas Cavalcanti
 *
 */
@ApplicationScoped
public class SessionFactoryCreator implements ComponentFactory<SessionFactory> {
	private final SessionFactory factory;

	public SessionFactoryCreator() {
		factory = new AnnotationConfiguration().configure().buildSessionFactory();
	}

	public SessionFactory getInstance() {
		return factory;
	}

	@PreDestroy
	public void destroy() {
		factory.close();
	}

}
