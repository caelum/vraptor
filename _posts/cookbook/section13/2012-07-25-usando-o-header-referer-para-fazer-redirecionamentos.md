---
section: 13
title: Usando o Header Referer para fazer redirecionamentos
language: pt
category: cookbook
layout: page
---

Geralmente quando você clica em um link, ou submete um formulário, o browser envia uma requisição para o servidor da sua aplicação, colocando um Header chamado Referer, que contém qual é a página atual, que originou a requisição.
Você pode usar esse Header com o VRaptor, para fazer os redirecionamentos:

{% highlight java %}
import static br.com.caelum.vraptor.view.Results.referer;
@Resource
public class ShoppingController {
    //...
    public void adicionaItem(Item item) {
        validator.checking(...);
        validator.onErrorUse(referer()).forward();
        
        dao.adiciona(item);
        
        result.use(referer()).redirect();
    }
}
{% endhighlight %}

O problema em usar o Referer é que ele não é obrigatório. Então quando o Referer não vem na requisição, o VRaptor vai lançar uma IllegalStateException, e assim você pode especificar uma outra lógica para ir caso o Referer não seja especificado:

{% highlight java %}
try {
    result.use(referer()).redirect();
} catch (IllegalStateException e) {
    result.use(logic()).redirectTo(HomeController.class).index();
}
{% endhighlight %}
