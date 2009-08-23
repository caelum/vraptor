package br.com.caelum.vraptor.util.migration;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * Provides connections and database metadata based on a hibernate session
 * factory.
 * 
 * @author Guilherme Silveira
 */
@Component
@ApplicationScoped
public class HibernateConnectionProvider implements ConnectionProvider {

	private final SessionFactory factory;

	public HibernateConnectionProvider(SessionFactory factory,
			ContainerProvider provider) {
		this.factory = factory;
	}

	public void apply(Migrations migrations) throws VRaptorException {
		Session session = factory.openSession();
		try {
			for (Migration migration : migrations.getAll()) {
				execute(session, migration);
			}
		} finally {
			session.close();
		}
	}

	private void execute(Session session, Migration migration) {
		Transaction tx = session.beginTransaction();
		PreparedStatement stmt = null;
		try {
			migration.execute(session);
			stmt = session.connection().prepareStatement(
					"insert into migrations (id) values (?)");
			stmt.setString(1, migration.getId());
			stmt.execute();
			tx.commit();
		} catch (SQLException e) {
			throw new VRaptorException("Unable to execute migration "
					+ migration.getId(), e);
		} finally {
			close(migration, stmt);
			if (!tx.wasCommitted()) {
				tx.rollback();
			}
		}
	}

	private void close(Migration migration, PreparedStatement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				throw new VRaptorException("Unable to execute migration "
						+ migration.getId(), e);
			}
		}
	}

	public List<String> getAppliedMigrations() {
		Session session = factory.openSession();
		try {
			return session.createQuery("select id from migrations").list();
		} finally {
			session.close();
		}
	}

}
