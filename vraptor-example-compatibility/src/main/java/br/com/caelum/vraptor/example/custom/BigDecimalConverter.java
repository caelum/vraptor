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
package br.com.caelum.vraptor.example.custom;

import java.math.BigDecimal;

import org.vraptor.LogicRequest;
import org.vraptor.converter.ConversionException;
import org.vraptor.converter.Converter;

public class BigDecimalConverter implements Converter{

    public Object convert(String value, Class<?> arg1, LogicRequest arg2) throws ConversionException {
        if(value==null) {
            return null;
        }
        return new BigDecimal("1" + value);
    }

    public Class<?>[] getSupportedTypes() {
        return new Class<?>[]{BigDecimal.class};
    }

}
