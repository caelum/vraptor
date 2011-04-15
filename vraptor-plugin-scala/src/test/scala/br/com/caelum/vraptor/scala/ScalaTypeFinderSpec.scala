package br.com.caelum.gnarus.infra.vraptor

import br.com.caelum.vraptor.http.ParameterNameProvider
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import scala.collection.JavaConversions._
import java.lang.reflect.{AccessibleObject, Method}

class ScalaTypeFinderSpec extends FlatSpec with ShouldMatchers {

  class A(val x:String)

  class B(val a:A)

  class C(val b:B)
  trait X {
    def abc(c:C)
  }
  "ScalaTypeFinder" should "find correct paths" in {
      val provider = new ParameterNameProvider {
        def parameterNamesFor(method: AccessibleObject) = Array("c")
      }
      val finder = new ScalaTypeFinder(provider)
      val params = finder.getParameterTypes(classOf[X].getDeclaredMethods()(0), Array("c", "c.b", "c.b.a", "c.b.a.x"))

      assert(params("c").isInstanceOf[Class[C]])
      assert(params("c.b").isInstanceOf[Class[B]])
      assert(params("c.b.a").isInstanceOf[Class[A]])
      assert(params("c.b.a.x").isInstanceOf[Class[String]])
    }
}