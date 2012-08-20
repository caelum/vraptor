---
section: 6
title: Usando tiles com VRaptor3
category: [pt, cookbook]
layout: page
---

<h4>por Otávio Scherer Garcia</h4>

Caso você queira integrar Tiles 2 com VRaptor fazendo com que seja feito forward diretamente para o tiles e não para o JSP (padrão no VRaptor3), basta escrever uma classe que implemente PathResolver com sua convenção.
A primeira coisa a fazer é colocar o tiles para responder como servlet. Assim, toda requisição vinda por *.tiles será redirecionada ao tiles, e não ao VRaptor. Lembre-se de colocar a declaração do tiles servlet antes do vraptor filter.

Coloque o seguinte conteúdo no web.xml:

{% highlight xml %}
<!-- arquivo de definições do tiles -->
<context-param>
 <param-name>org.apache.tiles.impl.BasicTilesContainer.DEFINITIONS_CONFIG</param-name>
  <param-value>/WEB-INF/classes/tiles.xml</param-value>
</context-param>

<!-- servlet de inicialização do tiles -->
<servlet>
  <servlet-name>TilesServlet</servlet-name>
  <servlet-class>org.apache.tiles.web.startup.TilesServlet</servlet-class>
  <load-on-startup>2</load-on-startup>
</servlet>

<!-- servlet que responde as requisições do tiles -->
<servlet>
  <servlet-name>TilesDispatchServlet</servlet-name>
 <servlet-class>org.apache.tiles.web.util.TilesDispatchServlet</servlet-class>
</servlet>

<!-- o tiles responderá por toda requisição *.tiles -->
<servlet-mapping>
  <servlet-name>TilesDispatchServlet</servlet-name>
  <url-pattern>*.tiles</url-pattern>
</servlet-mapping>
{% endhighlight %}

No meu caso usei como padrão para o tiles a convenção /package.controller.metodo. Sendo assim criei o seguinte path resolver.

{% highlight java %}
@Component
public class TilesPathResolver
  implements PathResolver {

  static final String VIEW_SUFIX = ".tiles";
  static final String CLASS_SUFIX = "Controller";

  @Override
  public String pathFor(ResourceMethod method) {
      final Class<?> clazz = method.getResource().getType();

      final StringBuilder s = new StringBuilder();
      s.append("/");
      // retorna apenas o nome do último pacote
      s.append(StringUtils.substringAfterLast(clazz.getPackage().getName(), "."));
      s.append(".");
      //remove o sufixo controller
      s.append(StringUtils.substringBefore(clazz.getSimpleName(), CLASS_SUFIX));
      s.append(".");
      s.append(method.getMethod().getName());
      s.append(VIEW_SUFIX);

   // definições do tile em minusculo, mas você pode alterar isso
      return s.toString().toLowerCase();
  }
}
{% endhighlight %}

Se você não quiser utilizar a classe StringUtils do projeto commons-lang, você pode alterar o metodo para:

{% highlight java %}
final Class<?> clazz = method.getResource().getType();
String pkgname = clazz.getPackage().getName();

final StringBuilder s = new StringBuilder();
s.append("/");
// retorna apenas o nome do último pacote
s.append(pkgname.substring(pkgname.lastIndexOf(".") + 1));
s.append(".");
//remove o sufixo controller
s.append(clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf(CLASS_SUFIX)));
s.append(".");
s.append(method.getMethod().getName());
s.append(VIEW_SUFIX);

// definições do tile em minusculo, mas você pode alterar isso
return s.toString().toLowerCase();
{% endhighlight %}

Nesse caso se você tiver o controler CustomerController e chamar o método list, a view será redirecionada ao URI /admin.customer.list.tiles, que será executado pelo tiles servlet.

{% highlight java %}
package xpto.admin;

@Resource
public class CustomerController {
  public List<Customer> list() {
      return myServiceClass.listAllCustomers();
      // será redirecionado para a definição admin.customer.list
  }
}
<definition name="admin.customer.list" extends="default">
  <put-attribute name="body" value="/WEB-INF/jspx/admin/customer.list.jspx" />
</definition>
{% endhighlight %}

<h4>Referências:</h4>

<ul>
<li><a href="http://tiles.apache.org/">Apache Tiles</a></li>
<li><a href="http://vraptor.caelum.com.br/documentacao/view-e-ajax/">VRaptor View e PathResolver</a></li>
</ul>
