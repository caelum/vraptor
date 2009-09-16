package org.vraptor.mydvds.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.vraptor.mydvds.model.User;

/**
 * Data Access Object referente a entidade User.
 */
public class UserDao {

	private Session session;

	/**
	 * Cria um novo UserDao.
	 * @param session sessão do hibernate
	 */
	UserDao(Session session) {
		this.session = session;
	}

	/**
	 * Devolve o usuario encontrado pelo login e senha.
	 *
	 * @param login
	 * @param password a senha
	 * @return usuario encontrado (único)
	 * @throws HibernateException, se encontrar mais do que um usuário
	 */
	public User search(String login, String password) {
		//procura um usuario usando criteria,
		//adiciona dois restricçõs nessa critéria, login e senha,
		return (User) session.createCriteria(User.class).add(
				Restrictions.eq("login", login)).add(
				Restrictions.eq("password", password)).uniqueResult();
	}

	/**
	 * Adiciona o usuário no banco de dados.
	 *
	 * @param user
	 */
	public void add(User user) {
		session.save(user);
	}

	/**
	 * Lê o usuario do banco de dados e sincroniza o estado do
	 * usuário. Qualquer alteração no objeto usuário, que não foi salvo antes,
	 * será sobrescrita.
	 *
	 * @param user
	 */
	public void refresh(User user) {
		session.refresh(user);
	}

	/**
	 * Atualiza o usuário no banco de dados.
	 *
	 * @param user
	 */
	public void update(User user) {
		session.update(user);
	}

	/**
	 * Devolve todos os usuários.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> listAll() {
		return session.createCriteria(User.class).list();
	}

	/**
	 * Verifique se já existe um usuário com o login.
	 *
	 * @param login o login para verificar
	 * @return true se já existe uma usuário com login
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
