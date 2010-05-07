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
package br.com.caelum.vraptor.scala;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.util.StringUtils;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;

/**
 * Scala-compatible implementation for {@link TypeNameExtractor}.
 * It decapitalizes the name of the type, or if the type is a generic collection,
 * uses the decapitalized name of generic type plus 'List'.
 *
 * @author Pedro Matiello <pmatiello@gmail.com>
 * @author Alberto Souza <alberto.souza@caelum.com.br>
 * @author Sérgio Lopes <sergio.lopes@caelum.com.br>
 */
@ApplicationScoped
@Component
public class ScalaTypeNameExtractor extends DefaultTypeNameExtractor {

    @Override
	public String nameFor(Type generic) {
		if (generic instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) generic;
			Class<?> raw = (Class<?>) type.getRawType();
			if (scala.collection.Seq.class.isAssignableFrom(raw)) {
				return nameFor(type.getActualTypeArguments()[0]) + "List";
			}
		}
		
    	return super.nameFor(generic);        
	}

}
