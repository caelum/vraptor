package br.com.caelum.vraptor.util.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("unchecked")
public class Migrations {

	private final Set<Migration> migrations;

	Migrations(List<Migration> migrations) {
		this.migrations = new TreeSet<Migration>(new MigrationComparator());
		this.migrations.addAll(migrations);
	}

	Migrations() {
		this(new ArrayList<Migration>());
	}

	public void add(Migration migration) {
		this.migrations.add(migration);
	}

	public Set<Migration> getAll() {
		return migrations;
	}

	Integer size() {
		return migrations.size();
	}

	@Override
	public String toString() {
		return "[Migrations: " + migrations + "]";
	}

}
