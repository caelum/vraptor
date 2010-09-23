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
 *
 * @author Sergio Lopes
 * @since 3.2
 *
 */
public interface ComponentScanner {

	/**
	 * Scan all classes in WEB-INF/classes and all classes from
	 * JARs from WEB-INF/lib belonging to specific packages.
	 *
	 * @param basePackages Base packages to register JARs.
	 * @return All class names found.
	 */
	Collection<String> scan(ClasspathResolver resolver);

}