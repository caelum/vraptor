---
title: View e Ajax
layout: page
language: pt
section: 8
category: docs
---

<h3>Compartilhando objetos com a view</h3>

Para registrar objetos a serem acessados na view, usamos o método include:

{% highlight java %}
@Resource
class ClientController {
    private final Result result;
    public ClientController(Result result) {
        this.result = result;
    }
    
    public void busca(int id) {
        result.include("mensagem", "Alguma mensagem");
        result.include("cliente", new Cliente(id));
    }
}
{% endhighlight %}

Agora as variáveis "mensagem" e "cliente" estão disponíveis para uso em seu template engine.
É possível registrar o objeto por meio da invocação do método include com um único argumento:

{% highlight java %}
@Resource
class ClientController {
    private final Result result;
    public ClientController(Result result) {
        this.result = result;
    }
    
    public void busca(int id) {
        result.include("Alguma mensagem").include(new Cliente(id));
    }
}
{% endhighlight %}

Nesse caso, a primeira invocação registra a chave "string" e a segunda, a chave "cliente". Você pode alterar o comportamento de convenção de chaves no seu próprio TypeNameExtractor.

<h3>Custom PathResolver</h3>

Por padrão, para renderizar suas views, o VRaptor segue a convenção:

{% highlight java %}
public class ClientsController {
    public void list() {
        //...
    }
}
{% endhighlight %}

Este método acima renderizará a view /WEB-INF/jsp/clients/list.jsp.
No entanto, nem sempre queremos esse comportamento, e precisamos usar algum template engine, como por exemplo, Freemarker ou Velocity, e precisamos mudar essa convenção.
Um jeito fácil de mudar essa convenção é estendendo a classe DefaultPathResolver:

{% highlight java %}
@Component
public class FreemarkerPathResolver extends DefaultPathResolver {
    protected String getPrefix() {
        return "/WEB-INF/freemarker/";
    }
    
    protected String getExtension() {
        return "ftl";
    }
}
{% endhighlight %}

Desse jeito, a lógica irá renderizar a view /WEB-INF/freemarker/clients/list.ftl. Se, ainda assim, isso não for o suficiente, você pode implementar a interface PathResolver e fazer qualquer convenção que você queira, não esquecendo de anotar a classe com @Component.

<h3>View</h3>

Se você quiser mudar a view de alguma lógica específica, você pode usar o objeto Result:

{% highlight java %}
@Resource
public class ClientsController {
    
    private final Result result;
    
    public ClientsController(Result result) {
        this.result = result;
    }
    
    public void list() {}
    
    public void save(Client client) {
        //...
        this.result.use(Results.logic()).redirectTo(ClientsController.class).list();
    }
}
{% endhighlight %}

Por padrão, existem estes tipos de views implementadas:

<ul>
	<li>Results.logic(), que vai redirecionar para uma outra lógica qualquer do sistema</li>

	<li>Results.page(), que vai redirecionar diretamente para uma página, podendo ser um JSP, um HTML, ou qualquer URI relativa ao web application dir, ou ao contexto da aplicação.</li>

	<li>Results.http(), que manda informações do protocolo HTTP como status codes e headers.</li>

	<li>Results.status(), manda status codes com mais informações.</li>

	<li>Results.referer(), que usa o header Referer para fazer redirects ou forwards.</li>

	<li>Results.nothing(), apenas retorna o código de sucesso (HTTP 200 OK).</li>

	<li>Results.xml(), serializa objetos em XML.</li>

	<li>Results.json(), serializa objetos em JSON.</li>

	<li>Results.representation(), serializa objetos em um formato determinado pela requisição (parâmetro _format ou header Accept)</li>
</ul>

<h3>Atalhos no Result</h3>

Alguns redirecionamentos são bastante utilizados, então foram criados atalhos para eles. Os atalhos disponíveis são:

<ul>
	<li>result.forwardTo("/some/uri") ==> result.use(page()).forward("/some/uri");</li>

	<li>result.redirectTo("/some/uri") ==> result.use(page()).redirect("/some/uri)</li>

	<li>result.permanentlyRedirectTo("/some/uri") ==> result.use(status()).movedPermanentlyTo("/some/uri");</li>

	<li>result.forwardTo(ClientController.class).list() ==> result.use(logic()).forwardTo(ClientController.class).list();</li>

	<li>result.redirectTo(ClientController.class).list() ==> result.use(logic()).redirectTo(ClientController.class).list();</li>

	<li>result.of(ClientController.class).list() ==> result.use(page()).of(ClientController.class).list();</li>

	<li>result.permanentlyRedirectTo(Controller.class) ==> use(status()).movedPermanentlyTo(Controller.class);</li>

	<li>result.notFound()	 ==> use(status()).notFound()</li>

	<li>result.nothing()	 ==> use(nothing());</li>
</ul>

Além disso, se o redirecionamento é para um método do mesmo Controller, podemos usar:

<ul>
	<li>result.forwardTo(this).list() ==> result.use(logic()).forwardTo(this.getClass()).list();</li>

	<li>result.redirectTo(this).list() ==> result.use(logic()).redirectTo(this.getClass()).list();</li>

	<li>result.of(this).list() ==> result.use(page()).of(this.getClass()).list();</li>

	<li>result.permanentlyRedirectTo(this) ==> use(status()).movedPermanentlyTo(this.getClass());</li>
</ul>

<h3>Redirecionamento e forward</h3>

No VRaptor3, podemos tanto realizar um redirect ou um forward do usuário para uma outra lógica ou um JSP. Apesar de serem conceitos da API de Servlets, vale a pena relembrar a diferença: o redirecionamento acontece no lado do cliente, através de códigos HTTP que farão o browser acessar uma nova URL; já o forward acontece no lado do servidor, totalmente transparente para o cliente/browser.

Um bom exemplo de uso do redirect é no chamado 'redirect-after-post'. Por exemplo: quando você adiciona um cliente e que, após o formulário submetido, o cliente seja retornado para a página de listagem de clientes. Fazendo isso com redirect, impedimos que o usuário atualize a página (F5) e reenvie toda a requisição, acarretando em dados duplicados.

No caso do forward, um exemplo de uso é quando você possui uma validação e essa validação falhou, geralmente você quer que o usuário continue na mesma tela do formulário com os dados da requisição preenchidos, mas internamente você vai fazer o forward para outra lógica de negócios (a que prepara os dados necessários para o formulário).

<div class="nota">
<h4>Escopo Flash automático</h4>

Se você adicionar objetos no Result e fizer um Redirect, esses objetos estarão disponíveis na próxima requisição.

{% highlight java %}
public void adiciona(Cliente cliente) {
    dao.adiciona(cliente);
    result.include("mensagem", "Cliente adicionado com sucesso");
    result.redirectTo(ClientesController.class).lista();
}
{% endhighlight %}

lista.jsp:

{% highlight jsp %}
...
<div id="mensagem">
   <h3>${mensagem}</h3>
</div>
...
{% endhighlight %}
</div>

<h3>Accepts e o parâmetro _format</h3>

Muitas vezes, precisamos renderizar formatos diferentes para uma mesma lógica. Por exemplo queremos retornar um JSON, em vez de um HTML. Para fazer isso, podemos definir o Header Accepts da requisição para que aceite o tipo desejado, ou colocar um parâmetro &#95;format na requisição.

Se o formato for JSON, a view renderizada por padrão será: /WEB-INF/jsp/{controller}/{logic}.json.jsp, ou seja, em geral será renderizada a view: /WEB-INF/jsp/{controller}/{logic}.{formato}.jsp. Se o formato for HTML você não precisa colocá-lo no nome do arquivo.
O parâmetro &#95;format tem prioridade sobre o header Accepts.

<h3>Ajax: construindo na view</h3>

Para devolver um JSON na sua view, basta que sua lógica disponibilize o objeto para a view, e dentro da view você forme o JSON como desejar. Como no exemplo, o seu /WEB-INF/jsp/clients/load.json.jsp:

{% highlight jsp %}
{ nome: '${client.name}', id: '${client.id}' }
{% endhighlight %}

E na lógica:

{% highlight java %}
@Resource
public class ClientsController {
    
    private final Result result;
    private final ClientDao dao;
    
    public ClientsController(Result result, ClientDao dao) {
        this.result = result;
        this.dao = dao;
    }
    
    public void load(Client client) {
        result.include("client", dao.load(client));
    }
}
{% endhighlight %}

<h3>Ajax: Versão programática</h3>

Se você quiser que o VRaptor serialize automaticamente seus objetos para XML ou JSON, você pode escrever em sua lógica:

{% highlight java %}
import static br.com.caelum.vraptor.view.Results.*;
@Resource
public class ClientsController {
    
    private final Result result;
    private final ClientDao dao;
    
    public ClientsController(Result result, ClientDao dao) {
        this.result = result;
        this.dao = dao;
    }
    
    public void loadJson(Cliente cliente) {
        result.use(json()).from(cliente).serialize();
    }
    public void loadXml(Cliente cliente) {
        result.use(xml()).from(cliente).serialize();
    }
}
{% endhighlight %}

Os resultados vão ser parecidos com:

{% highlight jsp %}
{"cliente": {
	"nome": "Joao"
}}
{% endhighlight %}

{% highlight xml %}
<cliente>
	<nome>Joao</nome>
</cliente>
{% endhighlight %}

Por padrão, apenas campos de tipos primitivos serão serializados (String, números, enums, datas), se você quiser incluir um campo de tipo não primitivo você precisa incluí-lo explicitamente:

{% highlight java %}
result.use(json()).from(cliente).include("endereco").serialize();
{% endhighlight %}

vai resultar em algo parecido com:

{% highlight jsp %}
{"cliente": {
	"nome": "Joao",
	"endereco" {
		"rua": "Vergueiro"
	}
}}
{% endhighlight %}

Você pode também excluir os campos de tipo primitivo da serialização:

{% highlight java %}
result.use(json()).from(usuario).exclude("senha").serialize();
{% endhighlight %}

vai resultar em algo parecido com:

{% highlight jsp %}
{"usuario": {
	"nome": "Joao",
	"login": "joao"
}}
{% endhighlight %}

Ou ainda você pode serializar recursivamente (cuidado com ciclos):

{% highlight java %}
result.use(json()).from(usuario).recursive().serialize();
result.use(xml()).from(usuario).recursive().serialize();
{% endhighlight %}

A implementação padrão é baseada no XStream, então é possível configurar a serialização por anotações ou configurações diretas ao XStream, bastando criar a classe:

{% highlight java %}
@Component
public class CustomXMLSerialization extends XStreamXMLSerialization {
//or public class CustomJSONSerialization extends XStreamJSONSerialization {
    //delegate constructor
    
    @Override
    protected XStream getXStream() {
        XStream xStream = super.getXStream();
        //suas configurações ao XStream aqui
        return xStream;
    }
}
{% endhighlight %}

<h3>Serializando Collections</h3>

Ao serializar coleções, o padrão é colocar a tag "list" em volta dos elementos:

{% highlight java %}
List<Cliente> clientes = ...;
result.use(json()).from(clientes).serialize();
//ou
result.use(xml()).from(clientes).serialize();
{% endhighlight %}

vai resultar em algo como:

{% highlight jsp %}
{"list": [
	{
		"nome": "Joao"
	},
	{
		"nome": "Maria"
	}
]}
{% endhighlight %}

ou

{% highlight xml %}
<list>
	<cliente>
		<nome>Joao</nome>
	</cliente>
	<cliente>
		<nome>Maria</nome>
	</cliente>
</list>
{% endhighlight %}

É possível personalizar o elemento de fora usando o método:

{% highlight java %}
List<Cliente> clientes = ...;
result.use(json()).from(clientes, "clientes").serialize();
//ou
result.use(xml()).from(clientes, "clientes").serialize();
{% endhighlight %}

vai resultar em algo como:

{% highlight jsp %}
{"clientes": [
	{
		"nome": "Joao"
	},
	{
		"nome": "Maria"
	}
]}
{% endhighlight %}

ou

{% highlight xml %}
<clientes>
	<cliente>
		<nome>Joao</nome>
	</cliente>
	<cliente>
		<nome>Maria</nome>
	</cliente>
</clientes>
{% endhighlight %}

Os includes e excludes funcionam como se você os estivesse aplicando num elemento de dentro da lista. Por exemplo se você quiser incluir o endereço no cliente:

{% highlight java %}
List<Cliente> clientes = ...;
result.use(json()).from(clientes).include("endereco").serialize();
{% endhighlight %}

com resultado:

{% highlight jsp %}
{"list": [
	{
		"nome": "Joao",
		"endereco": {
			"rua": "Vergueiro, 3185"
		}
	},
	{
		"nome": "Maria",
		"endereco": {
			"rua": "Vergueiro, 3185"
		}
	}
]}
{% endhighlight %}

<h3>Serializando JSON sem elemento raiz</h3>

Se você quiser serializar um objeto em JSON sem dar nomes a eles, pode fazer isso com o método withoutRoot:

{% highlight java %}
result.use(json()).from(carro).serialize(); //=> {'carro': {'cor': 'azul'}}
result.use(json()).withoutRoot().from(carro).serialize(); //=> {'cor': 'azul'}
{% endhighlight %}
