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

/**
 * Classpath scanning for VRaptor projects. It can scan everything dynamically when 
 * the context starts or can scan in build time and generate proper artifacts to prevent
 * performance degradation in runtime. 
 * 
 * VRaptor will scan:
 * 
 *  - all components in WEB-INF/classes/
 *  - all base packages from JARs in WEB-INF/classes
 * 
 * There are two options to configure the base packages:
 * 
 *  - configure the br.com.caelum.vraptor.packages context-param in web.xml
 *  - create a META-INF/br.com.caelum.vraptor.packages file inside some JARs
 * 
 * @author Sérgio Lopes
 * @since 3.2
 */
package br.com.caelum.vraptor.scan;

