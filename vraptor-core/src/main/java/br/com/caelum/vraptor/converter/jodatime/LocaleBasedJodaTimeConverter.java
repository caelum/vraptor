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
package br.com.caelum.vraptor.converter.jodatime;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import br.com.caelum.vraptor.core.Localization;

class LocaleBasedJodaTimeConverter {

    private final Localization localization;

	public LocaleBasedJodaTimeConverter(Localization localization) {
		this.localization = localization;
	}

	public DateTime convert(String value, DateTimeFormatter formatter) throws ParseException {
	    if (isNullOrEmpty(value)) {
			return null;
		}
	    
		return formatter.withLocale(getLocale()).parseDateTime(value);
	}

	public Locale getLocale() {
		Locale locale = localization.getLocale();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
}