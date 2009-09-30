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
import java.util.Collection;
import java.util.List;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.pico.Scanner;

public class MigrationsLookupProvider implements MigrationsProvider {

	private final Scanner scanner;
	public MigrationsLookupProvider(Scanner scanner) {
		this.scanner = scanner;
	}
	public Migrations all() {
		Collection<Class<? extends Migration>> migrationTypes = this.scanner.getSubtypesOfWithAnnotation(Migration.class, Component.class);
		return new Migrations(instantiateAll(migrationTypes));
	}
	private List<Migration> instantiateAll(Collection<Class<? extends Migration>> migrationTypes) {
		List<Migration> migrations = new ArrayList<Migration>();
		for (Class<? extends Migration> type : migrationTypes) {
			try {
				migrations.add(type.getConstructor().newInstance());
			} catch (Exception e) {
				throw new VRaptorException("Cannot instantiate migration: ", e);
			}
		}
		return migrations;
	}

}
