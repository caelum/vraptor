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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive char converter.
 *
 * @author Cecilia Fernandes
 */
@Convert(char.class)
@ApplicationScoped
public class PrimitiveCharConverter implements Converter<Character> {

    public Character convert(String value, Class<? extends Character> type, ResourceBundle bundle) {
        if (isNullOrEmpty(value)) {
            return '\u0000';
        }
        
        if (value.length() != 1) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_character"), value));
        }
        return value.charAt(0);
    }

}