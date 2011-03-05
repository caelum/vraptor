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
package br.com.caelum.vraptor.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Locale based calendar converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Calendar.class)
@RequestScoped
public class LocaleBasedCalendarConverter implements Converter<Calendar> {

    private final Localization localization;
    
    public LocaleBasedCalendarConverter(Localization localization) {
        this.localization = localization;
    }

    public Calendar convert(String value, Class<? extends Calendar> type, ResourceBundle bundle) {
        if (isNullOrEmpty(value)) {
            return null;
        }
        
        Locale locale = localization.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        try {
            Date date = format.parse(value);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_date"), value));
        }
    }

}
