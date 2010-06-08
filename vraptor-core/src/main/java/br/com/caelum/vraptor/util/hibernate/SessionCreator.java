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
package br.com.caelum.vraptor.util.hibernate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Opens and closes a Session from a SessionFactory and
 * provides it to container
 * @author Lucas Cavalcanti
 */
@Component
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
