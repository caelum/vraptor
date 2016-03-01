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
	private static final String LOGIN = "login";

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
			.setParameter(LOGIN, login)
			.setParameter("password", password);

		return (User) query.uniqueResult();
	}

	public User find(String login) {
		String hql = "from User u where u.login = :login";

		Query query = session.createQuery(hql).setParameter(LOGIN, login);

		return (User) query.uniqueResult();
	}

	public void add(User user) {
		session.save(user);
	}

	public void refresh(User user) {
		session.refresh(user);
	}

	public void update(User user) {
		session.update(user);
	}

	@SuppressWarnings("unchecked")
	public List<User> listAll() {
		return session.createCriteria(User.class).list();
	}

	public boolean containsUserWithLogin(String login) {
		String hql = "from User user where user.login = :login";
		Query query = session.createQuery(hql).setParameter(LOGIN, login);

		return !query.list().isEmpty();
	}

}
