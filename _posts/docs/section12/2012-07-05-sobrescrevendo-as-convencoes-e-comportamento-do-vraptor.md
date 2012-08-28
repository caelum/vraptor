---
title: Sobrescrevendo as convenções e o comportamento do VRaptor
layout: page
section: 12
category: [pt, docs]
---

A maioria dos comportamentos e convenções do VRaptor são personalizáveis. E a forma de personalizar é bem fácil: criar um componente que implementa uma das interfaces internas do VRaptor. Ao fazer isso, o VRaptor vai usar a implementação personalizada ao invés da padrão.
Para saber qual é a interface certa para personalizar um certo comportamento, pergunte na lista de desenvolvedores do VRaptor: caelum-vraptor-dev@googlegroups.com ou no <a href="http://www.guj.com.br/forums/show/23.java">fórum do GUJ</a>.

Abaixo veremos alguns exemplos de personalização:

<h3>Mudando a view renderizada por padrão</h3>

Se você precisa mudar a view renderizada por padrão, ou mudar o local em que ela é procurada, basta criar a seguinte classe:

{% highlight java %}
@Component
public class CustomPathResolver extends DefaultPathResolver {
    
    @Override
    protected String getPrefix() {
        return "/pasta/raiz/";
    }
    
    @Override
    protected String getExtension() {
        return "ftl"; // ou qualquer outra extensão
    }

    @Override
    protected String extractControllerFromName(String baseName) {
        return //sua convenção aqui
               //ex.: Em vez de redirecionar UserController para 'user'
               //você quer redirecionar para 'userResource'
               //ex.2: Se você sobrescreveu a conveção para nome dos Controllers para XXXResource
               //e quer continuar redirecionando para 'user' e não para 'userResource'
    }

}
{% endhighlight %}

Se você precisa mudar mais ainda a convenção basta implementar a interface PathResolver.

<h3>Mudando a URI padrão</h3>

Por padrão, a URI para o método ClientesController.lista() é /clientes/lista, ou seja, nome_do_controller/nome_do_metodo. Para sobrescrever essa convenção, basta criar a classe:

{% highlight java %}
@Component
@ApplicationScoped
public class MeuRoutesParser extends PathAnnotationRoutesParser {
    //delegate constructor
    protected String extractControllerNameFrom(Class<?> type) {
        return //sua convenção aqui
    }

     protected String defaultUriFor(String controllerName, String methodName) {
        return //sua convenção aqui
    }
}
{% endhighlight %}

Se você precisa mudar mais ainda a convenção, basta implementar a interface RoutesParser.

<h3>Mudando o encoding da sua aplicação</h3>

Se você quiser que todas as requisições da sua aplicação sejam de um encoding determinado, para evitar problemas de acentuação por exemplo, você pode colocar o seguinte parâmetro no seu web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.encoding</param-name>
    <param-value>UTF-8</param-value>
</context-param>
{% endhighlight %}

Assim, todas as suas páginas e dados passados para formulário usarão o encoding UTF-8, evitando problemas de acentuação.
