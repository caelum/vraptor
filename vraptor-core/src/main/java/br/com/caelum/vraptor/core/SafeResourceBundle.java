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
package br.com.caelum.vraptor.core;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A Resource bundle that doesn't throw exception when there is no resource of given key.
 * It only returns ??? + key + ??? when the key doesn't exist.
 *
 * @author Lucas Cavalcanti
 */
public class SafeResourceBundle extends ResourceBundle {

	private final ResourceBundle delegate;

	public SafeResourceBundle(ResourceBundle delegate) {
		this.delegate = delegate;
	}
	@Override
	public Enumeration<String> getKeys() {
		return delegate.getKeys();
	}

	@Override
	protected Object handleGetObject(String key) {
		try {
			return delegate.getString(key);
		} catch (MissingResourceException e) {
			return "???" + key + "???";
		}
	}

}
