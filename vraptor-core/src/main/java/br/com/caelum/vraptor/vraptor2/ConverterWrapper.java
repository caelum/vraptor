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

package br.com.caelum.vraptor.vraptor2;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.vraptor.converter.ConversionException;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.ConversionError;


/**
 * Wraps a vraptor2 converter in a vraptor3 converter.
 * 
 * @author guilherme silveira
 */
public class ConverterWrapper implements Converter {

	private final org.vraptor.converter.Converter converter;

	public ConverterWrapper(org.vraptor.converter.Converter converter) {
		this.converter = converter;
	}

	public Object convert(String value, Class type, ResourceBundle bundle) {
		try {
			return converter.convert(value, type, null);
		} catch (ConversionException e) {
			// prints stack trace because its an internal problem that ocurred
			e.printStackTrace();
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_valid"), value));
		}
	}

}
