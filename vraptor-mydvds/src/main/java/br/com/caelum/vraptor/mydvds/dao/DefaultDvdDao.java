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
package br.com.caelum.vraptor.mydvds.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.mydvds.model.Dvd;
import br.com.caelum.vraptor.mydvds.model.DvdRental;

/**
 * Default implementation for DvdDao.
 *
 * Annotating this class with @Component we have the dependency injection
 * support either on this class and on other classes that depend on
 * DvdDao or DefaultDvdDao
 *
 * @author Lucas Cavalcanti
 */
@Component
public class DefaultDvdDao implements DvdDao {

	// current hibernate session
	private final Session session;

	/**
	 * Creates a new DvdDao.
	 *
	 * @param session hibernate session.
	 */
	public DefaultDvdDao(Session session) {
		this.session = session;
	}

	public void add(Dvd dvd) {
		session.save(dvd);
	}

	public void add(DvdRental copy) {
		session.save(copy);
	}

	@SuppressWarnings("unchecked")
	public List<Dvd> searchSimilarTitle(String title) {
		// creates a criteria based on the Dvd class and adds
		// the "title" restriction and then returns the list.
		return session.createCriteria(Dvd.class).add(
				Restrictions.ilike("title", "%" + title + "%")).list();
	}

}
