---
section: 4
title: Desabilitar exception do Page Result
categories: [pt, cookbook]
layout: page
---

Quando você usa redirecionamentos para página:

{% highlight java %}
result.use(Results.page()).forward(url);
{% endhighlight %}

e tenta passar uma URL que é tratada por alguma lógica da sua aplicação, o VRaptor vai lançar uma exceção falando para você usar o result.use(logic()) correspondente. Por exemplo:

{% highlight java %}
public class TesteController {
    @Path("/teste")
    public void teste() {}

    public void redireciona() {
        result.use(page()).redirect("/teste");
        // vai lançar uma exceção, falando para você usar o código
        // result.use(logic()).redirectTo(TesteController.class).teste();
    }
}
{% endhighlight %}

Se você quiser desabilitar esta exceção, você pode criar a seguinte classe:

{% highlight java %}
@Component
public class MyPageResult extends DefaultPageResult {
    // delega o construtor
    @Override
    protected void checkForLogic(String url, HttpMethod httpMethod) {
       //nada aqui, ou algum outro tipo de checkagem
    }
}
{% endhighlight %}
