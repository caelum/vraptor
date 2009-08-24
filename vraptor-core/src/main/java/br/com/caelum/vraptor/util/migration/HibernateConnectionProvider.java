package br.com.caelum.vraptor.util.migration;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Provides connections and database metadata based on a hibernate session
 * factory.
 *
 * You must have a SessionFactory registered on Container, for example, using a
 * ComponentFactory.
 *
 * @author Guilherme Silveira
 */
@Component
@ApplicationScoped
public class HibernateConnectionProvider implements ConnectionProvider {

	private final SessionFactory factory;

	public HibernateConnectionProvider(SessionFactory factory) {
		this.factory = factory;
	}

	public void apply(Migrations migrations) throws VRaptorException {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			for (Migration migration : migrations.getAll()) {
				execute(session, migration);
			}
			tx.commit();
		} finally {
			if (tx != null && !tx.wasCommitted()) {
				tx.rollback();
			}

			session.close();
		}
	}

	private void execute(Session session, Migration<Session> migration) {
		migration.execute(session);
		session.save(new MigrationInfo(migration.getId()));
	}

	@SuppressWarnings("unchecked")
	public List<String> getAppliedMigrations() {
		Session session = factory.openSession();
		try {
			return session.createQuery("select id from MigrationInfo").list();
		} finally {
			session.close();
		}
	}

}
