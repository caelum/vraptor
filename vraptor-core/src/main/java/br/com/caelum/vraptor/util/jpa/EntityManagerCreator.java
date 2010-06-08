/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.util.jpa;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * An example of how to create EntityManager's for your components
 * @author Lucas Cavalcanti
 *
 */
@Component
@RequestScoped
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
