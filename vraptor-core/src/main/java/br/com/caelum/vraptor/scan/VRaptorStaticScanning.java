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

/**
 * A static Scanner that generates an static WebAppBootstrap
 *
 * @author Sérgio Lopes
 * @since 3.2
 */
public class VRaptorStaticScanning {

	/**
	 * @param args[0] (optional) The WEB-INF/web.xml location
	 */
	public static void main(String[] args) {
		VRaptorStaticScanning app = new VRaptorStaticScanning();
		app.start(args.length == 1 ? args[0] : null);
	}

	void start(String webxml) {
		System.out.println("Starting VRaptor's static classpath scanning");

		ClasspathResolver cpr;
		if (webxml == null) {
			cpr = new StandaloneClasspathResolver();
		} else {
			cpr = new StandaloneClasspathResolver(webxml);
		}

		System.out.print("Initiating the scanning...");
		ComponentScanner scanner = new ScannotationComponentScanner();
		Collection<String> classes = scanner.scan(cpr);
		System.out.println(" done.");

		System.out.print("Generating the static registry...");
		BootstrapGenerator generator = new JavassistBootstrapGenerator();
		generator.generate(classes, cpr);
		System.out.println(" done.");
	}
}
