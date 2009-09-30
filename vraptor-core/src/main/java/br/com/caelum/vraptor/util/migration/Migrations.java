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
