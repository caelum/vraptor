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
package br.com.caelum.vraptor.scala

import scala.collection.JavaConversions._
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor
import br.com.caelum.vraptor.ioc.ApplicationScoped
import br.com.caelum.vraptor.ioc.Component
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
class ScalaTypeNameExtractor extends DefaultTypeNameExtractor {

  override def nameFor(generic:Type) = {
    generic match {
      case ptype:ParameterizedType if classOf[Seq[_]].isAssignableFrom(ptype.getRawType().asInstanceOf[Class[_]]) =>
				nameFor(ptype.getActualTypeArguments()(0)) + "List";
      case _ =>
        super.nameFor(generic);
    }
	}

}
