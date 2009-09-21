package br.com.caelum.vraptor.mydvds.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.mydvds.model.User;

/**
 * Data Access Object of User entity.
 */
@Component
public class UserDao {

	private final Session session;

	/**
	 * Creates a new UserDao. You can receive dependencies through constructor,
	 * because this class is annotated with @Component. This class can be used
	 * as dependency of another class, as well.
	 * @param session Hibernate's Session.
	 */
	public UserDao(Session session) {
		this.session = session;
	}

	/**
	 * Finds an user by login and password.
	 *
	 * @param login
	 * @param password
	 * @return found user if it is unique
	 * @throws HibernateException, if there are more than one user
	 */
	public User search(String login, String password) {
		//procura um usuario usando criteria,
		//adiciona dois restricçõs nessa critéria, login e senha,
		return (User) session.createCriteria(User.class).add(
				Restrictions.eq("login", login)).add(
				Restrictions.eq("password", password)).uniqueResult();
	}

	/**
	 * Adds the user on database
	 *
	 * @param user
	 */
	public void add(User user) {
		session.save(user);
	}

	/**
	 * Synchronize the user data with the database. Any not saved modification on user will be
	 * overwritten.
	 *
	 * @param user
	 */
	public void refresh(User user) {
		session.refresh(user);
	}

	/**
	 * Update the user on database.
	 * @param user
	 */
	public void update(User user) {
		session.update(user);
	}

	/**
	 * Retrieves all users from database.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> listAll() {
		return session.createCriteria(User.class).list();
	}

	/**
	 * Checks if there is already an user with given login.
	 *
	 * @param login
	 * @return true if there exists a user
	 */
	public boolean containsUserWithLogin(String login) {
		//cria uma query HQL para verificar,
		//HQL: "from User as user where user.login = :login"
		//query devolve uma lista e
		//verifique se a lista está vazía
		return !session.createQuery(
				"from " + User.class.getName()
						+ " as user where user.login = :login").setString(
				"login", login).list().isEmpty();
	}

}
