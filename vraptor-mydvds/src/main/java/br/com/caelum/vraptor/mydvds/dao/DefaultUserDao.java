package br.com.caelum.vraptor.mydvds.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.mydvds.model.User;

/**
 * Default implementation for UserDao
 *
 * @author Lucas Cavalcanti
 */
@Component
public class DefaultUserDao implements UserDao {

	private final Session session;

	/**
	 * Creates a new UserDao. You can receive dependencies through constructor,
	 * because this class is annotated with @Component. This class can be used
	 * as dependency of another class, as well.
	 * @param session Hibernate's Session.
	 */
	public DefaultUserDao(Session session) {
		this.session = session;
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.UserDao#find(java.lang.String, java.lang.String)
	 */
	public User find(String login, String password) {
		String hql = "from User u where u.login = :login and u.password = :password";

		Query query = session.createQuery(hql)
			.setParameter("login", login)
			.setParameter("password", password);

		return (User) query.uniqueResult();
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.UserDao#add(br.com.caelum.vraptor.mydvds.model.User)
	 */
	public void add(User user) {
		session.save(user);
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.UserDao#refresh(br.com.caelum.vraptor.mydvds.model.User)
	 */
	public void refresh(User user) {
		session.refresh(user);
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.UserDao#update(br.com.caelum.vraptor.mydvds.model.User)
	 */
	public void update(User user) {
		session.update(user);
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.UserDao#listAll()
	 */
	@SuppressWarnings("unchecked")
	public List<User> listAll() {
		return session.createCriteria(User.class).list();
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.UserDao#containsUserWithLogin(java.lang.String)
	 */
	public boolean containsUserWithLogin(String login) {
		String hql = "from User user where user.login = :login";
		Query query = session.createQuery(hql).setParameter("login", login);

		return !query.list().isEmpty();
	}

}
