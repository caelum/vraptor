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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.mydvds.model.Dvd;

/**
 * Test class for DvdDao.
 * When testing DAOs it is a good idea to use a real database, to find query errors.
 *
 * @author Lucas Cavalcanti
 *
 */
public class DvdDaoTest {
	private Session session;
	private DvdDao dao;

	@Test
	public void shouldFindADvdWithSimilarTitle() throws Exception {
		Dvd dvd = new Dvd();
		dvd.setTitle("The last cruzade");
		dao.add(dvd);

		assertThat(dao.searchSimilarTitle("last"), hasItem(dvd));
		assertThat(dao.searchSimilarTitle("first"), not(hasItem(dvd)));
	}

	/**
	 * Getting a real Hibernate Session, from a memory database.
	 */
	@Before
	public void setUp() throws Exception {
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.configure().setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:mydvdsDB");
		session = cfg.buildSessionFactory().openSession();
		session.beginTransaction();
		dao = new DefaultDvdDao(session);
	}


	/**
	 * Undoing all changes to database
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		if (session != null && session.getTransaction().isActive()) {
			session.getTransaction().rollback();
		}
	}
}
