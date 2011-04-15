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

import br.com.caelum.vraptor.http.route.Evaluator
import net.vidageek.mirror.dsl.Mirror
import br.com.caelum.vraptor.ioc.{ApplicationScoped, Component}

/**
 * uses scala accessors to get fields
 * @author Lucas Cavalcanti
 */
@Component
@ApplicationScoped
class ScalaEvaluator extends Evaluator {
   override def get(obj:AnyRef, path:String) = {
      path.split("\\.").drop(1).foldLeft(obj)((o,p) =>
        new Mirror().on(o).invoke.method(p).withoutArgs
      )
   }
}