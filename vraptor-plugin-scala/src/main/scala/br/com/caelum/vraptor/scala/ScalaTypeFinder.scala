package br.com.caelum.gnarus.infra.vraptor

import br.com.caelum.vraptor.http.route.TypeFinder
import java.lang.reflect.Method
import br.com.caelum.vraptor.http.ParameterNameProvider
import br.com.caelum.vraptor.ioc.{ApplicationScoped, Component}
import net.vidageek.mirror.dsl.Mirror
import scala.collection.JavaConversions._
import java.lang.Class

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