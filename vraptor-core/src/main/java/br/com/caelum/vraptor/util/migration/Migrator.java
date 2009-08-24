package br.com.caelum.vraptor.util.migration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * A component responsible for migrating your database settings to a new
 * situation.<br/>
 * Upon server startup it will check which migrations were not applied (by id)
 * and apply them to the current database.
 *
 * @author guilherme silveira
 *
 */
@ApplicationScoped
public class Migrator {

    private static final Logger logger = LoggerFactory.getLogger(Migrator.class);
	private final ConnectionProvider provider;
	private final MigrationsProvider migrations;

	Migrator(ConnectionProvider provider, MigrationsProvider migrations) {
		this.provider = provider;
		this.migrations = migrations;
	}

	public Migrations getMigrationsToApply() {
		Set<String> applied = new HashSet<String>(provider.getAppliedMigrations());
		List<Migration> toApply = new ArrayList<Migration>(migrations.all().getAll());
		for (Iterator<Migration> it = toApply.iterator(); it.hasNext();) {
			Migration migration = it.next();
			if (applied.contains(migration.getId())) {
				it.remove();
			}
		}
		return new Migrations(toApply);
	}

	@PostConstruct
	public void startup() {
		logger.info("Starting the Migrator");
		Migrations migrations = getMigrationsToApply();
		logger.debug("Full list of migrations found " + migrations);
		provider.apply(migrations);
	}

}
