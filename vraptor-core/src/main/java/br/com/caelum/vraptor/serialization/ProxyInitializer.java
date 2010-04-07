/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.serialization;

/**
 * Initializer of proxfied objets.
 * 
 * @author Tomaz Lavieri
 * @since 3.1.2
 */
public interface ProxyInitializer {
	
	/**
	 * Check if the <tt>clazz</tt> send is isAssignableFrom proxy.
	 */
	boolean isProxy(Class<?> clazz);
	
	/**
	 * Initialize the <tt>obj</tt> send if it is a proxy.
	 */
	void initialize(Object obj);
	
	/**
	 * Find the real class of the <tt>obj</tt> send. 
	 * @return the class of delegate object if the <tt>obj</tt> is a proxy or <tt>obj.getClass()</tt> otherwise.
	 */
	Class<?> getActualClass(Object obj);

}
