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
package br.com.caelum.vraptor.mydvds.provider;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.util.hibernate.HibernateTransactionInterceptor;
import br.com.caelum.vraptor.util.hibernate.SessionCreator;
import br.com.caelum.vraptor.util.hibernate.SessionFactoryCreator;

/**
 * You can extend SpringProvider (or PicoProvider) in order to register
 * optional components, like the Hibernate Session and SessionFactory
 * Component Factories.
 *
 * Don't forget to register it on your web.xml:
 *
 * <context-param>
 *      <param-name>br.com.caelum.vraptor.provider</param-name>
 *      <param-value>br.com.caelum.vraptor.mydvds.provider.CustomProvider</param-value>
 * </context-param>
 *
 * @author Lucas Cavalcanti
 *
 */
public class CustomProvider extends SpringProvider {

	@Override
	protected void registerCustomComponents(ComponentRegistry registry) {
		//Usage is: register.register(TypeToRegister.class, ImplementationForThisType.class);
		//In this case the implementation is equal to type to register.
		registry.register(SessionCreator.class, SessionCreator.class);
		registry.register(SessionFactoryCreator.class, SessionFactoryCreator.class);

		registry.register(HibernateTransactionInterceptor.class, HibernateTransactionInterceptor.class);
	}

}
