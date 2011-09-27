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
package br.com.caelum.vraptor.util.test;

import java.util.Locale;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.SafeResourceBundle;
import br.com.caelum.vraptor.util.EmptyBundle;

/**
 * A localization that returns safe empty bundles
 *
 * @author Lucas Cavalcanti
 * @since 3.3.0
 *
 */
public class MockLocalization implements Localization {

	public ResourceBundle getBundle() {
		return new SafeResourceBundle(new EmptyBundle());
	}

	public Locale getFallbackLocale() {
		return null;
	}

	public Locale getLocale() {
		return null;
	}

	public String getMessage(String key, Object... parameters) {
		return getBundle().getString(key);
	}

}
