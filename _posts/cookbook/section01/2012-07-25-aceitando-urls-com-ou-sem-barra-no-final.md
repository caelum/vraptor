---
section: 1
title: Aceitando URLs com ou sem barra no final
category: [pt, cookbook]
layout: page
---

<h4>por Tomaz Lavieri</h4>
Para quem teve dificuldade como eu em conseguir determinar urls como

{% highlight java %}
@Path("produto/{produto.id}")
{% endhighlight %}

quando digitava a URI <strong>"/produto/1/"</strong> e o link não funcionava, segue abaixo uma modificação que corrige o problema.

<div class="nota">
<h4>Nota do editor</h4>
Isso não é necessariamente um problema... a URL /abc é diferente da /abc/ portanto o comportamento de dar 404 é o esperado. Mas se você quiser que sejam urls equivalentes você pode escrever a classe abaixo.
</div>

{% highlight java %}
import br.com.caelum.vraptor.Result;  
import br.com.caelum.vraptor.core.RequestInfo;  
import br.com.caelum.vraptor.http.route.ResourceNotFoundException;  
import br.com.caelum.vraptor.http.route.Router;  
import br.com.caelum.vraptor.ioc.Component;  
import br.com.caelum.vraptor.ioc.RequestScoped;  
import br.com.caelum.vraptor.resource.DefaultResourceNotFoundHandler;  
import br.com.caelum.vraptor.resource.HttpMethod;  
import br.com.caelum.vraptor.view.Results;  
  
@Component  
@RequestScoped  
public class Error404 extends DefaultResourceNotFoundHandler  {  
    private final Router router;  
    private final Result result;  
    public Error404(Router router, Result result) {  
        this.router = router;  
        this.result = result;  
    }  
      
    @Override  
    public void couldntFind(RequestInfo requestInfo) {  
        try {  
            String uri = requestInfo.getRequestedUri();  
            if (uri.endsWith("/")) {  
                tryMovePermanentlyTo(requestInfo, uri.substring(0, uri.length()-1));  
            } else {  
                tryMovePermanentlyTo(requestInfo, uri + "/");  
            }  
        } catch (ResourceNotFoundException ex) {  
            super.couldntFind(requestInfo);  
        }  
    }  
  
    private void tryMovePermanentlyTo(RequestInfo requestInfo, String newUri) {  
        router.parse(newUri, HttpMethod.of(requestInfo.getRequest()), 
            requestInfo.getRequest());  
        result.use(Results.http()).movedPermanentlyTo(newUri);  
    }  
}
{% endhighlight %}
