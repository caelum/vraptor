package org.vraptor.mydvds.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Dao Factory.
 *
 * Responsible for creating Dao objects and encapsulate access to the model layer.
 *
 * Also gives access to Hibernate transactions and sessions.
 */
@Component
@RequestScoped
public class DaoFactory {

	private static final Logger LOG = Logger.getLogger(DaoFactory.class);

	// current hibernate session
	private final Session session;

	// current hibernate transaction
	private Transaction transaction;

	/*
	 * Receives a Hibernate session.
	 */
	public DaoFactory(Session session) {
		this.session = session;
	}

	/**
	 * Closes the session.
	 */
	public void close() {
		if (this.session.isOpen()) {
			try {
				if (this.hasTransaction()) {
					this.rollback();
				}
			} catch (HibernateException e) {
				LOG.fatal("exception rolling back", e);
			} finally {
				this.session.close();
			}
		}
	}

	/**
	 * Starts a transaction.
	 */
	public void beginTransaction() {
		if (this.hasTransaction()) {
			this.rollback();
			throw new IllegalStateException(
					"There is no nested transaction support!");
		}
		this.transaction = this.session.beginTransaction();
	}

	/**
	 *  Flushes the session.
	 */
	public void flush() {
		this.session.flush();
	}

	/**
	 * Commits the transaction.
	 */
	public void commit() {
		this.session.flush();
		this.transaction.commit();
		this.transaction = null;
	}

	/**
	 *	Rollbacks the transaction.
	 */
	public void rollback() {
		this.transaction.rollback();
		this.transaction = null;
	}

	/**
	 * Checks if there is an active transaction
	 * @return true, if there is an active transaction
	 */
	public boolean hasTransaction() {
		return this.transaction != null && this.transaction.isActive();
	}

	/**
	 * @return hibernate session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the user dao.
	 *
	 * @return the user dao.
	 */
	public UserDao getUserDao() {
		return new UserDao(getSession());
	}

	/**
	 * Returns the dvd dao.
	 *
	 * @return the dvd dao.
	 */
	public DvdDao getDvdDao() {
		return new DvdDao(getSession());
	}

}
