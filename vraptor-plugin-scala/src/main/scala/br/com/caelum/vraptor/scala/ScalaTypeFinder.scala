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

import br.com.caelum.vraptor.http.route.TypeFinder
import java.lang.reflect.Method
import br.com.caelum.vraptor.http.ParameterNameProvider
import br.com.caelum.vraptor.ioc.{ApplicationScoped, Component}
import net.vidageek.mirror.dsl.Mirror
import scala.collection.JavaConversions._
import java.lang.Class

/**
 * uses scala acessors to discover types
 * @author Lucas Cavalanti
 */
@Component
@ApplicationScoped
class ScalaTypeFinder(provider:ParameterNameProvider) extends TypeFinder {
  implicit def class2raw(klass: Class[_]) = new {
    def raw = klass.asInstanceOf[Class[AnyRef]]
  }

  implicit def isRoot(name: String) = new {
    def isRootOf(path: String) = path.startsWith(name + ".") || path.equals(name)
  }
  def getParameterTypes(method:Method, parameterPaths:Array[String]):java.util.Map[String, Class[_]] = {
    val names = provider.parameterNamesFor(method)
    val types = method.getParameterTypes()
    val entries =
    for (path <- parameterPaths; i <- names.indices; if (names(i).isRootOf(path))) yield {
        path -> path.split("\\.").drop(1).foldLeft(types(i).raw)(
        new Mirror().on(_).reflect.method(_).withoutArgs.getReturnType.raw
        )
      }
    Map(entries:_*)
  }

}