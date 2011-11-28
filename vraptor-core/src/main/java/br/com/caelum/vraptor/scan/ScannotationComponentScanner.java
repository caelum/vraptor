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

import static com.google.common.base.Objects.firstNonNull;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.vidageek.mirror.dsl.Mirror;

import org.scannotation.AnnotationDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.Stereotype;

/**
 * A Scannotation based Component Scanner
 *
 * @author Sérgio Lopes
 * @since 3.2
 */
public class ScannotationComponentScanner implements ComponentScanner {

	private static final Logger logger = LoggerFactory.getLogger(ScannotationComponentScanner.class);

	public Collection<String> scan(ClasspathResolver resolver) {
		final URL webInfClasses = resolver.findWebInfClassesLocation();
		final List<String> basePackages = resolver.findBasePackages();

		HashSet<String> results = new HashSet<String>();

		Map<String, Set<String>> webInfClassesAnnotationMap = scanWebInfClasses(webInfClasses);
		Map<String, Set<String>> basePackagesAnnotationMap = scanBasePackages(basePackages, resolver);

		Set<String> stereotypeNames = findStereotypes(webInfClassesAnnotationMap, basePackagesAnnotationMap, basePackages);

		findComponentsFromWebInfClasses(webInfClassesAnnotationMap, stereotypeNames, results);
		findComponentsFromBasePackages(basePackagesAnnotationMap, basePackages, results);

		return results;
	}

	private Map<String, Set<String>> scanWebInfClasses(URL webInfClasses) {
		try {
			AnnotationDB db = createAnnotationDB();
			db.scanArchives(webInfClasses);
			return db.getAnnotationIndex();
		} catch (IOException e) {
			throw new ScannerException("Could not scan WEB-INF/classes", e);
		}
	}

	private Map<String, Set<String>> scanBasePackages(List<String> basePackages, ClasspathResolver resolver) {
		try {
			AnnotationDB db = createAnnotationDB();

			for (String basePackage : basePackages) {
				scanPackage(basePackage, db, resolver);
			}

			return db.getAnnotationIndex();
		} catch (IOException e) {
			throw new ScannerException("Could not scan base packages", e);
		}
	}



	private void scanPackage(String basePackage, AnnotationDB db, ClasspathResolver resolver) throws IOException {
        String resource = basePackage.replace('.', '/');
        Enumeration<URL> urls = resolver.getClassLoader().getResources(resource);
        if (!urls.hasMoreElements()) {
            logger.error("There's no occurence of package {} in classpath", basePackage);
            return;
        }

        do {
            String fileName = null;
            URL url = urls.nextElement();
            logger.info("scanning url {}", url);

            if (url.getProtocol().startsWith("vfs")) {
                fileName = toJBossVFSFileName(url);
            } else {
                fileName = toFileName(resource, url.getFile());
            }
            
            if (!fileName.startsWith("file:")) {
                fileName = "file:" + fileName;
            }

            db.scanArchives(new URL(fileName));
        } while (urls.hasMoreElements());
	}

    private String toJBossVFSFileName(URL url)
        throws IOException {
        Object content = url.openConnection().getContent();
        File pfile = (File) new Mirror().on(content).invoke().method("getPhysicalFile").withoutArgs();
        logger.info("real file for url {} is {}", url, pfile);

        if (pfile.isDirectory()) {
            return pfile.getAbsolutePath() + "/";
        }

        return pfile.getAbsolutePath();
    }

    private String toFileName(String resource, String file) {
        return file.substring(0, file.length() - resource.length() - 1).replaceAll("(!)(/)?$", "");
    }

	private Set<String> findStereotypes(Map<String, Set<String>> webInfClassesAnnotationMap, Map<String, Set<String>> basePackagesAnnotationMap, List<String> basePackages) {
		HashSet<String> results = new HashSet<String>();

		addVRaptorStereotypes(results);

		addWebInfClassesStereotypes(webInfClassesAnnotationMap, results);

		addBasePackagesStereotypes(basePackagesAnnotationMap, basePackages, results);

		return results;
	}

	private void addBasePackagesStereotypes(Map<String, Set<String>> basePackagesAnnotationMap,
			List<String> basePackages, HashSet<String> results) {
		Set<String> libStereotypes = nullToEmpty(basePackagesAnnotationMap.get(Stereotype.class.getName()));
		for (String stereotype : libStereotypes) {
			if (packagesContains(basePackages, stereotype)) {
				results.add(stereotype);
			}
		}
	}

	private boolean packagesContains(List<String> basePackages, String clazz) {
		for (String basePackage : basePackages) {
			if (clazz.startsWith(basePackage)) {
				return true;
			}
		}
		return false;
	}

	private void addWebInfClassesStereotypes(Map<String, Set<String>> webInfClassesAnnotationMap,
			HashSet<String> results) {
		Set<String> myStereotypes = nullToEmpty(webInfClassesAnnotationMap.get(Stereotype.class.getName()));
		results.addAll(myStereotypes);
	}

	private void addVRaptorStereotypes(HashSet<String> results) {
		for (Class<? extends Annotation> stereotype : BaseComponents.getStereotypes()) {
			results.add(stereotype.getName());
		}
	}

	private void findComponentsFromWebInfClasses(Map<String, Set<String>> index, Set<String> stereotypeNames, Set<String> results) {
		for (String stereotype : stereotypeNames) {
			Set<String> classes = nullToEmpty(index.get(stereotype));
			results.addAll(classes);
		}
	}

	private void findComponentsFromBasePackages(Map<String, Set<String>> index, List<String> basePackages, Set<String> results) {
		for (Class<? extends Annotation> stereotype : BaseComponents.getStereotypes()) {
			Set<String> classes = nullToEmpty(index.get(stereotype.getName()));

			for (String clazz : classes) {
				if (packagesContains(basePackages, clazz)) {
					results.add(clazz);
				}
			}
		}
	}

	private <T> Set<T> nullToEmpty(Set<T> set) {
		return firstNonNull(set, Collections.<T>emptySet());
	}

	private AnnotationDB createAnnotationDB() {
		AnnotationDB db = new AnnotationDB();
		db.setScanClassAnnotations(true);
		db.setScanFieldAnnotations(false);
		db.setScanMethodAnnotations(false);
		db.setScanParameterAnnotations(false);
		return db;
	}
}