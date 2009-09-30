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
package br.com.caelum.vraptor.util.migration;

import java.util.Arrays;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class HibernateConnectionProviderTest {


	private Mockery mockery;
	private SessionFactory factory;
	private Session session;
	private HibernateConnectionProvider provider;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		factory = mockery.mock(SessionFactory.class);
		session = mockery.mock(Session.class);
		provider = new HibernateConnectionProvider(factory);

		mockery.checking(new Expectations() {
			{
				allowing(factory).openSession();will(returnValue(session));
			}
		});
	}

	@Test
	public void shouldExecuteAllMigrationsAndSaveMigrationInfos() throws Exception {
		final Migration migration = mockery.mock(Migration.class, "a migration");
		final Migration otherMigration = mockery.mock(Migration.class, "other migration");


		mockery.checking(new Expectations() {
			{
				allowing(migration).getId(); will(returnValue("1"));
				allowing(otherMigration).getId(); will(returnValue("2"));

				one(migration).execute(session);
				one(otherMigration).execute(session);

				one(session).save(new MigrationInfo("1"));
				one(session).save(new MigrationInfo("2"));

				ignoring(anything());

			}
		});

		Migrations migrations = new Migrations(Arrays.asList(migration, otherMigration));
		provider.apply(migrations);

		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldRollbackIfAMigrationFails() throws Exception {
		final Migration migration = mockery.mock(Migration.class, "a migration");
		final Migration otherMigration = mockery.mock(Migration.class, "other migration");


		mockery.checking(new Expectations() {
			{
				allowing(migration).getId(); will(returnValue("1"));
				allowing(otherMigration).getId(); will(returnValue("2"));

				one(migration).execute(session); will(throwException(new RuntimeException()));
				never(otherMigration).execute(session);

				never(session).save(new MigrationInfo("1"));
				never(session).save(new MigrationInfo("2"));

				Transaction transaction = mockery.mock(Transaction.class);

				allowing(session).beginTransaction();
				will(returnValue(transaction));

				one(transaction).rollback();
				one(transaction).wasCommitted(); will(returnValue(false));

				ignoring(anything());

			}
		});

		Migrations migrations = new Migrations(Arrays.asList(migration, otherMigration));
		try {
			provider.apply(migrations);
			Assert.fail("Expected exception");
		} catch (RuntimeException e) {
			//expected
			mockery.assertIsSatisfied();
		}

	}

}
