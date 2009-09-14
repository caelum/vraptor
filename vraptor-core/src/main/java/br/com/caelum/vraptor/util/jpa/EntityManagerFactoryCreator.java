package br.com.caelum.vraptor.util.jpa;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@ApplicationScoped
public class EntityManagerFactoryCreator implements ComponentFactory<EntityManagerFactory>{

	private EntityManagerFactory factory;

	@PostConstruct
	public void create() {
		factory = Persistence.createEntityManagerFactory("default");
	}

	public EntityManagerFactory getInstance() {
		return factory;
	}

	@PreDestroy
	public void destroy() {
		factory.close();
	}

}
