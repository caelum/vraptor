package br.com.caelum.vraptor.mydvds.dao;

import java.util.List;

import org.hibernate.HibernateException;

import br.com.caelum.vraptor.mydvds.model.User;

/**
 * Data Access Object of User entity.
 * @author Lucas Cavalcanti
 */
public interface UserDao {

	/**
	 * Finds an user by login and password.
	 *
	 * @param login
	 * @param password
	 * @return found user if it is unique
	 * @throws HibernateException, if there are more than one user
	 */
	User find(String login, String password);

	/**
	 * Finds an user by login
	 *
	 * @param login
	 * @return found user if it is unique
	 * @throws HibernateException, if there are more than one user
	 */
	User find(String login);

	/**
	 * Adds the user on database
	 *
	 * @param user
	 */
	void add(User user);

	/**
	 * Synchronize the user data with the database. Any not saved modification on user will be
	 * overwritten.
	 *
	 * @param user
	 */
	void refresh(User user);

	/**
	 * Update the user on database.
	 * @param user
	 */
	void update(User user);

	/**
	 * Retrieves all users from database.
	 *
	 * @return
	 */
	List<User> listAll();

	/**
	 * Checks if there is already an user with given login.
	 *
	 * @param login
	 * @return true if there exists a user
	 */
	boolean containsUserWithLogin(String login);

}