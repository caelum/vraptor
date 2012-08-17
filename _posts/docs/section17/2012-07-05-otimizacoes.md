---
title: Otimizações
layout: page
language: pt
section: 17
category: [pt, docs]
---

<h3>Commons Upload</h3>

Se você não for usar upload na sua aplicação, remova o jar do commons upload do classpath. Isso evita o carregamento do interceptor de upload, deixando o request mais rápido.

<h3>Anotação @Lazy em Interceptors</h3>

Se o método accepts do seu interceptor não depende do seu estado interno você pode anotá-lo com @Lazy:

{% highlight java %}
@Intercepts
@Lazy
public class MeuLazyInterceptor implements Interceptor {
    public MeuLazyInterceptor(Dependencia dependencia) {
        this.dependencia = dependencia;
    }
    public boolean accepts(ResourceMethod method) {
        // depende apenas do method
        return method.containsAnnotation(Abc.class);
    }
    public void intercepts(...) {
        //...
    }
}
{% endhighlight %}

Assim o VRaptor só vai instanciar esse interceptor se o método accepts retornar true. Para fazer isso o VRaptor cria uma instância não funcional do interceptor (todas as dependências nulas) e chama o método accepts, evitando uma chamada ao Container de DI desnecessária. Acessos ao estado interno do interceptor podem gerar NullPointerException.
Não use o @Lazy se o accepts é trivial (apenas retorna true).
