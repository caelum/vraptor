package br.com.caelum.gnarus.infra.vraptor

import br.com.caelum.vraptor.http.route.Evaluator
import net.vidageek.mirror.dsl.Mirror
import br.com.caelum.vraptor.ioc.{ApplicationScoped, Component}

@Component
@ApplicationScoped
class ScalaEvaluator extends Evaluator {
   override def get(obj:AnyRef, path:String) = {
      path.split("\\.").drop(1).foldLeft(obj)((o,p) =>
        new Mirror().on(o).invoke.method(p).withoutArgs
      )
   }
}