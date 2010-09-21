/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.scan;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;

/**
 * A dynamic impl of DynamicWebAppBootstrap for use in dev time.
 *
 * @author Sérgio Lopes
 * @since 3.2
 */
public class DynamicWebAppBootstrap implements WebAppBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(DynamicWebAppBootstrap.class);
	private final Collection<String> classNames;

	public DynamicWebAppBootstrap(Collection<String> classNames) {
		this.classNames = classNames;
	}

	public void configure(ComponentRegistry registry) {
		for (String className : classNames) {
			logger.trace("Registering class {}", className);

			try {
				Class<?> clazz = Class.forName(className);
				registry.deepRegister(clazz);
			} catch (ClassNotFoundException e) {
				throw new ScannerException("Error while registering classes", e);
			}
		}
	}

}
