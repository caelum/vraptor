package br.com.caelum.vraptor.util.hibernate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Opens and closes a Session from a SessionFactory and
 * provides it to container
 * @author Lucas Cavalcanti
 */
@RequestScoped
public class SessionCreator implements ComponentFactory<Session> {

	private final SessionFactory factory;
	private Session session;

	public SessionCreator(SessionFactory factory) {
		this.factory = factory;
	}

	@PostConstruct
	public void create() {
		this.session = factory.openSession();
	}

	public Session getInstance() {
		return session;
	}

	@PreDestroy
	public void destroy() {
		this.session.close();
	}

}
