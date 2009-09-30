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
package br.com.caelum.vraptor.view;

/**
 * Translate the Accept header to a _format String
 * @author Sérgio Lopes
 * @author Jonas Abreu
 */
public interface AcceptHeaderToFormat {

	/**
	 * Get the _format associated with the given Accept header.
	 * Should not return null, return html instead.
	 * 
	 * @param accept The Accept HTTP header
	 * @return The _format String
	 */
	String getFormat(String accept);
	
}
