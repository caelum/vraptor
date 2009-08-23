package br.com.caelum.vraptor.util.migration;

import java.util.List;

public class ArrayBasedMigrationProvider implements MigrationsProvider {

	private final List<Migration> all;

	public ArrayBasedMigrationProvider(List<Migration> migrations) {
		this.all = migrations;
	}

	public Migrations all() {
		return new Migrations(all);
	}

}
