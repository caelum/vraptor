package br.com.caelum.vraptor.mydvds.dao;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.mydvds.model.User;

/**
 * Test class for UserDao.
 * When testing DAOs, we don't test methods that only delegates to Hibernate,
 * like dao.save(), dao.update(), dao.refresh() and listAll().
 *
 * @author Lucas Cavalcanti
 *
 */
public class UserDaoTest {
	private Session session;
	private UserDao dao;


	@Test
	public void shouldFindUsersByLoginAndPassword() throws Exception {
		User user = new User();
		user.setName("Test Boy");
		user.setLogin("myLogin");
		user.setPassword("secret!");
		dao.add(user);

		assertThat(dao.find("myLogin", "secret!"), is(user));
		assertThat(dao.find("mispelledLogin", "secret!"), is(nullValue()));
		assertThat(dao.find("myLogin", "wrongPassword"), is(nullValue()));
	}
	@Test
	public void shouldFindUsersByLogin() throws Exception {
		User user = new User();
		user.setName("Test Boy");
		user.setLogin("myLogin");
		user.setPassword("secret!");
		dao.add(user);

		assertThat(dao.find("myLogin"), is(user));
		assertThat(dao.find("mispelledLogin"), is(nullValue()));
	}

	@Test
	public void checkingIfThereIsAUserWithAGivenLogin() throws Exception {
		User user = new User();
		user.setName("Test Boy");
		user.setLogin("myLogin");
		user.setPassword("secret!");
		dao.add(user);

		assertTrue(dao.containsUserWithLogin("myLogin"));
		assertFalse(dao.containsUserWithLogin("mispelledLogin"));

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
		dao = new DefaultUserDao(session);
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
