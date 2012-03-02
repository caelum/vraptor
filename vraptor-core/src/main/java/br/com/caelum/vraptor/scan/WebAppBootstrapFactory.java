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

import br.com.caelum.vraptor.config.BasicConfiguration;

/**
 * Creates the right WebAppBootstrap
 * 
 * @author Sérgio Lopes
 * @author Guilherme Silveira
 * @since 3.2
 */
public class WebAppBootstrapFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(WebAppBootstrapFactory.class);

	/**
	 * Returns the WebAppBootstrap for this web application
	 * 
	 * @param servletContext
	 * @return
	 */
	public WebAppBootstrap create(BasicConfiguration config) {
		WebAppBootstrap strap = tryStaticBootstrap();
		
		if (strap != null) {
			return strap;
		}
		
		if (config.isClasspathScanningEnabled()) {
			return scannerFor(config);
		}

		return new NullWebAppBootstrap();
	}

	private WebAppBootstrap tryStaticBootstrap() {
		try {
			Class<?> clazz = Class
					.forName(WebAppBootstrap.STATIC_BOOTSTRAP_NAME);

			logger.info("Found a static WebAppBootstrap; using it and skipping classpath scanning.");
			return (WebAppBootstrap) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new ScannerException(
					"Error while creating the StaticWebAppBootstrap", e);
		}
	}

	private WebAppBootstrap scannerFor(BasicConfiguration config) {
		logger.info("Dynamic WebAppBootstrap found.");

		// dinamically scan the classpath if there's no static cache generated
		ClasspathResolver resolver = new WebBasedClasspathResolver(
				config.getServletContext());

		logger.trace("Start classpath scanning");
		ComponentScanner scanner = new ScannotationComponentScanner();
		Collection<String> classNames = scanner.scan(resolver);
		logger.trace("End classpath scanning");

		return new DynamicWebAppBootstrap(classNames);
	}
}
