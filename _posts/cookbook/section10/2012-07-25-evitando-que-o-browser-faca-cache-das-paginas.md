---
section: 10
title: Evitando que o browser faça cache das páginas
language: pt
category: [pt, cookbook]
layout: page
---

<h4>por Otávio Scherer Garcia e Lucas Cavalcanti</h4>

Este interceptor indica ao browser para não efetuar cache das páginas, adicionando headers indicando que a página atual está expirada.

{% highlight java %}
@Intercepts
@RequestScoped
public class NoCacheInterceptor
    implements Interceptor {

    private final HttpServletResponse response;

    public NoCacheInterceptor(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public boolean accepts(ResourceMethod method) {
        return true; // allow all requests
    }

    @Override
    public void intercept(InterceptorStack stack, ResourceMethod method, 
                Object resourceInstance)
        throws InterceptionException {
        // set the expires to past
        response.setHeader("Expires", "Wed, 31 Dec 1969 21:00:00 GMT");

        // no-cache headers for HTTP/1.1
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        // no-cache headers for HTTP/1.1 (IE)
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");

        // no-cache headers for HTTP/1.0
        response.setHeader("Pragma", "no-cache");

        stack.next(method, resourceInstance);
    }
}
{% endhighlight %}

Você pode também criar uma anotação para que apenas as classes anotadas evitem o cache.

{% highlight java %}
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NoCache {

}
{% endhighlight %}

E alterar o método accepts do interceptor para o código abaixo:

{% highlight java %}
@Override
public boolean accepts(ResourceMethod method) {
    return method.containsAnnotation(NoCache.class);
}
{% endhighlight %}
