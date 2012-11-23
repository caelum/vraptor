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

package br.com.caelum.vraptor.serialization;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.validator.I18nMessage;


/**
 * Basic i18n messsage json serialization alias.
 *
 * @author Leonardo Wolter
 * @since 3.5
 */
@Component
public class I18nMessageSerialization implements View{

	private final JSONSerialization jsonSerialization;
	private final Localization localization;

	public I18nMessageSerialization(JSONSerialization jsonSerialization, Localization localization) {
		this.jsonSerialization = jsonSerialization;
		this.localization = localization;
    }

	public <T> Serializer from(String key) {
		return from(key, "message");
	}
	
	public <T> Serializer from(String key, String alias) {
		I18nMessage i18nMessage = new I18nMessage("teste", key);
		i18nMessage.setBundle(localization.getBundle());
		return jsonSerialization.from(i18nMessage.getMessage(), alias);
	}

	public boolean accepts(String format) {
		return jsonSerialization.accepts(format);
	}

	public JSONSerialization indented() {
		return jsonSerialization.indented();
	}
	
}
