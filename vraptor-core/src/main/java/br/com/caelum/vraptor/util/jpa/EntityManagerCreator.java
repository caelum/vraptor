package br.com.caelum.vraptor.util.jpa;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import br.com.caelum.vraptor.ioc.ComponentFactory;

public class EntityManagerCreator implements ComponentFactory<EntityManager>{

	private final EntityManagerFactory factory;
	private EntityManager entityManager;

	public EntityManagerCreator(EntityManagerFactory factory) {
		this.factory = factory;
	}

	@PostConstruct
	public void create() {
		entityManager = factory.createEntityManager();
	}

	public EntityManager getInstance() {
		return entityManager;
	}

	@PreDestroy
	public void destroy() {
		entityManager.close();
	}

}
