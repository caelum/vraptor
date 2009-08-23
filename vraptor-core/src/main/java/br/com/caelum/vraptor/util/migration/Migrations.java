package br.com.caelum.vraptor.util.migration;

import java.util.ArrayList;
import java.util.List;

public class Migrations {
	
	private final List<Migration> migrations;

	Migrations(List<Migration> migrations) {
		this.migrations = migrations;
	}
	
	Migrations() {
		this(new ArrayList<Migration>());
	}

	public void add(Migration migration) {
		this.migrations.add(migration);
	}

	public List<Migration> getAll() {
		return migrations;
	}

	Integer size() {
		return migrations.size();
	}

	Migration get(int i) {
		return migrations.get(i);
	}
	
	@Override
	public String toString() {
		return "[Migrations: " + migrations + "]";
	}

}
