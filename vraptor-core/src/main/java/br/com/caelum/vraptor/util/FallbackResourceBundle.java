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
package br.com.caelum.vraptor.util;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * A resource bundle which uses two different bundles to look for messages.
 *
 * @author Guilherme Silveira
 */
public class FallbackResourceBundle extends ResourceBundle {

	private final ResourceBundle main;
	private final ResourceBundle fallback;

	public FallbackResourceBundle(ResourceBundle main, ResourceBundle fallback) {
		this.main = main;
		this.fallback = fallback;
	}

	@Override
	public Enumeration<String> getKeys() {
		return new FallbackEnumeration(main.getKeys(), fallback.getKeys());
	}

	@Override
	protected Object handleGetObject(String key) {
		try {
			return main.getString(key);
		} catch (MissingResourceException e) {
			try {
				return fallback.getString(key);
			} catch (MissingResourceException e1) {
				return "???" + key + "???";
			}
		}
	}

}
