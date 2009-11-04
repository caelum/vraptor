/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.util.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

/**
 * A Custom provider that registers Hibernate optional components:
 *
 * <ul>
 *  <li> {@link SessionCreator} that creates hibernate {@link Session}</li>
 *  <li> {@link SessionFactoryCreator} that creates hibernate {@link SessionFactory} from
 *  	default hibernate.cfg.xml on classpath</li>
 *  <li> {@link HibernateTransactionInterceptor} that opens a transaction at the beginning of
 *  	the request, and commits (or rollbacks) that transaction at the end of the request. </li>
 * </ul>
 *
 * You can register this class as your provider on web.xml:
 * <code>
 * <context-param>
 *      <param-name>br.com.caelum.vraptor.provider</param-name>
 *      <param-value>br.com.caelum.vraptor.util.hibernate.HibernateCustomProvider</param-value>
 * </context-param>
 * </code>
 * @author Lucas Cavalcanti
 *
 */
public class HibernateCustomProvider extends SpringProvider {

	@Override
	protected void registerCustomComponents(ComponentRegistry registry) {
		registry.register(SessionCreator.class, SessionCreator.class);
		registry.register(SessionFactoryCreator.class, SessionFactoryCreator.class);
		registry.register(HibernateTransactionInterceptor.class, HibernateTransactionInterceptor.class);
	}
}
