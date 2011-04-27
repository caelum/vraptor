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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A ClasspathResolver for static use, without a web context.
 *
 * @author Sérgio Lopes
 * @since 3.2
 */
public class StandaloneClasspathResolver implements ClasspathResolver {

	private static final Logger logger = LoggerFactory.getLogger(StandaloneClasspathResolver.class);
	private final File webxml;

	public StandaloneClasspathResolver() {
		// try to discover web.xml location related to vraptor.jar
		String vraptor = "br/com/caelum/vraptor/VRaptor.class";
		URL vraptorJAR = getClassLoader().getResource(vraptor);
		String filename = vraptorJAR.getPath();

		int jarSeparationIndex = filename.lastIndexOf('!');
		filename = filename.substring(filename.indexOf(':') + 1, jarSeparationIndex == -1 ? filename.length() - 1: jarSeparationIndex);
		filename = filename.substring(0, filename.lastIndexOf('/'));

		this.webxml = new File(filename.substring(0, filename.lastIndexOf('/')) + "/web.xml");
	}

	public StandaloneClasspathResolver(String webxml) {
		this.webxml = new File(webxml);
	}

	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	// find WEB-INF classes related to web.xml
	public URL findWebInfClassesLocation() {
		try {
			File webInfClasses = new File(this.getWebxml().getParent() + "/classes");
			if (webInfClasses.exists()) {
				return new URL("file:" + webInfClasses.getAbsolutePath() + "/");
			}
			throw new ScannerException("Could not determine WEB-INF/classes location");
		} catch (MalformedURLException e) {
			throw new ScannerException("Could not determine WEB-INF/classes location", e);
		}
	}

	public List<String> findBasePackages() {
		ArrayList<String> packages = new ArrayList<String>();
		getPackagesFromWebXml(packages);
		getPackagesFromPluginsJARs(packages);
		return packages;
	}

	void getPackagesFromWebXml(List<String> result) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(this.getWebxml());

			NodeList params = doc.getElementsByTagName("context-param");
			for (int i = 0; i < params.getLength(); i++) {
				Element param = (Element) params.item(i);
				NodeList paramName = param.getElementsByTagName("param-name");
				if ("br.com.caelum.vraptor.packages".equals(paramName.item(0).getTextContent())) {
					NodeList paramValue = param.getElementsByTagName("param-value");
					String packages = paramValue.item(0).getTextContent();

					Collections.addAll(result, packages.trim().split("\\s*,\\s*"));
					return;
				}
			}
			logger.debug("No <context-param> found in web.xml");
		} catch (Exception e) {
			throw new ScannerException("Problems while parsing web.xml", e);
		}
	}

	/**
	 * find plugin packages
	 * @param result
	 */
	void getPackagesFromPluginsJARs(List<String> result) {
		try {
			ClassLoader classLoader = getClassLoader();
			Enumeration<URL> urls = classLoader.getResources("META-INF/br.com.caelum.vraptor.packages");

			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String packagesConfig = new Scanner(url.openStream()).useDelimiter("\\Z").next();
				if (packagesConfig != null) {
					Collections.addAll(result, packagesConfig.trim().split("\\s*,\\s*"));
				} else {
					logger.warn("Plugin packages file was empty: {}", url.getPath());
				}
			}
		} catch (IOException e) {
			logger.error("Exception while searching for packages file inside JARs", e);
		}
	}

	private File getWebxml() {
		if (!this.webxml.exists()) {
			throw new ScannerException("Could not locate web.xml. Please use the proper argument in command-line.");
		}
		return webxml;
	}
}