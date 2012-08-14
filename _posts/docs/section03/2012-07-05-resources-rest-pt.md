---
title: Resources-Rest
layout: page
language: pt
section: 3
category: docs
---

<h3>O que são Resources?</h3>

Resources são o que poderíamos pensar como recursos a serem disponibilizados para acesso pelos nossos clientes.
No caso de uma aplicação Web baseada no VRaptor, um recurso deve ser anotado com a anotação @Resource. Assim que o programador insere tal anotação em seu código, todos os métodos públicos desse recurso se tornam acessíveis através de chamadas do tipo GET a URIs específicas.
O exemplo a seguir mostra um recurso chamado ClienteController que possui métodos para diversas funcionalidades ligadas a um cliente.
Simplesmente criar essa classe e os métodos faz com que as URLs <strong>/cliente/adiciona</strong>, <strong>/cliente/lista</strong>, <strong>/cliente/visualiza</strong>, <strong>/cliente/remove</strong> e <strong>/cliente/atualiza</strong> sejam disponibilizadas, cada uma invocando o respectivo método em sua classe.

{% highlight java %}
@Resource
public class ClienteController {

  public void adiciona(Cliente cliente) {

  }
  
  public List<Cliente> lista() {
    return ...
  }
  
  public Cliente visualiza(Cliente perfil) {
    return ...
  }

  public void remove(Cliente cliente) {

  ...
  }
  

  public void atualiza(Cliente cliente) {
    ...
  }
}
{% endhighlight %}

<h3>Parâmetros dos métodos</h3>

Você pode receber parâmetros nos métodos dos seus controllers, e se o objeto usar a convenção de java beans (getters e setters para os atributos da classe), você pode usar pontos para navegar entre os atributos. Por exemplo, no método:

{% highlight java %}
public void atualiza(Cliente cliente) {
   //...
}
{% endhighlight %}

você pode passar como parâmetro na requisição:

{% highlight jsp %}
cliente.id=3
cliente.nome=Fulano
cliente.usuario.login=fulano
{% endhighlight %}

e os campos correspondentes, navegando por getters e setters a partir do cliente, serão setados.
Se um atributo do objeto ou parâmetro do método for uma lista (List<> ou array), você pode passar vários parâmetros usando colchetes e índices:

{% highlight jsp %}
cliente.telefones[0]=(11) 5571-2751 #no caso de ser uma lista de String
cliente.dependentes[0].id=1 #no caso de ser qualquer objeto, você pode continuar a navegação
cliente.dependentes[3].id=1 #os índices não precisam ser sequenciais
cliente.dependentes[0].nome=Cicrano #se usar o mesmo índice, vai ser setado no mesmo objeto
clientes[1].id=23 #funciona se você receber uma lista de clientes no método
{% endhighlight %}

<div class="nota">
<h4>Reflection no nome dos parâmetros</h4>

Infelizmente, o Java não realiza reflection em cima de parâmetros, pois esses dados não ficam disponíveis em bytecode (a não ser se compilado em debug mode, porém é algo opcional). Isso faz com que a maioria dos frameworks que precisem desse tipo de informção criem uma anotação própria para isso, o que polui muito o código (exemplo no JAX-WS, onde é comum encontrar um método como o acima com a assinatura void add(@WebParam(name="cliente") Cliente cliente).
O VRaptor tira proveito do framework <a href="http://paranamer.codehaus.org">Paranamer</a>, que consegue tirar essa informação por meio da pré compilação ou dos dados de debug, evitando criar mais uma anotação. Alguns dos desenvolvedores do VRaptor também participam no desenvolvimento do Paranamer.
</div>

<h3>Escopos</h3>

Por vezes você deseja compartilhar um componente entre todos os usuários, entre todas as requisições de um mesmo usuário ou a cada requisição de um usuário.
Para definir em que escopo o seu componente vive, basta utilizar as anotações @ApplicationScoped, @SessionScoped e @RequestScoped.
Caso nenhuma anotação seja utilizada, o VRaptor assume que seu componente ficará no escopo de request, isto é, você terá um novo componente a cada nova requisição.

<h3>@Path</h3>

A anotação @Path permite que você customize as URIs de acesso a seus métodos. O uso básico dessa anotação é feito por meio de uma URI fixa. O exemplo a seguir mostra a customização de uma URI para um método, que somente receberá requisições do tipo POST na URI <strong>/cliente</strong>:

{% highlight java %}
@Resource
public class ClienteController {

  @Path("/cliente")
  @Post 
  public void adiciona(Cliente cliente) {
  }
    
}
{% endhighlight %}

Se você anotar o ClienteController com @Path, o valor especificado vai ser usado como prefixo para todas as URIs desse controller:

{% highlight java %}
@Resource
@Path("/clientes")
public class ClienteController {
    //URI: /clientes/lista
    public void lista() {...}
    
    //URI: /clientes/salva
    @Path("salva")
    public void adiciona() {...}
    
    //URI: /clientes/todosOsClientes
    @Path("/todosOsClientes")
    public void listaTudo() {...}
}
{% endhighlight %}

<h3>Http Methods</h3>

O ideal é definir uma URI específica para diversos métodos HTTP diferentes, como GET, POST, PUT etc.
Para atingir esse objetivo, utilizamos as anotações @Get, @Post, @Delete etc que também permitem configurar uma URI diferente da URI padrão, da mesma forma que a anotação @Path.
O exemplo a seguir altera os padrões de URI do ClienteController para utilizar duas URIs distintas, com diversos métodos HTTP:

{% highlight java %}
@Resource
public class ClienteController {

  @Post("/cliente") 
  public void adiciona(Cliente cliente) {
  }
  
  @Path("/")
  public List<Cliente> lista() {
    return ...
  }

  @Get("/cliente")  
  public Cliente visualiza(Cliente cliente) {
    return ...
  }

  @Delete("/cliente")
  public void remove(Cliente cliente) {
    ...
  }
  
  @Put("/cliente")
  public void atualiza(Cliente cliente) {
    ...
  }
  
}
{% endhighlight %}

Como você pode notar, utilizamos os métodos HTTP + uma URI específica para identificar cada um dos métodos da nossa classe Java.
No momento de criar os links e formulários HTML devemos tomar um cuidado <strong>muito importante</strong> pois os browsers só dão suporte aos métodos POST e GET por meio de formulários hoje em dia.
Por isso, você deverá criar as requisições para métodos do tipo DELETE, PUT etc usando JavaScript ou passando um parâmetro extra, chamado <strong>_method</strong>. O último só funciona em requisições POST.
Esse parâmetro sobrescreverá o método HTTP real sendo invocado.
O exemplo a seguir mostra um link para o método visualiza de cliente:

{% highlight jsp %}
<a href="/cliente?cliente.id=5">ver cliente 5</a>
{% endhighlight %}

Agora um exemplo de como invocar o método de adicionar um cliente:

{% highlight jsp %}
<form action="/cliente" method="post">
    <input name="cliente.nome" />
    <input type="submit" />
</form>
{% endhighlight %}

E, note que para permitir a remoção pelo método DELETE, temos que usar o parâmetro _method, uma vez que o browser ainda não suporta tais requisições:

{% highlight jsp %}
<form action="/cliente" method="post">
    <input name="cliente.id" value="5" type="hidden" />
    <button type="submit" name="_method" value="DELETE">remover cliente 5</button>
</form>
{% endhighlight %}

<h3>Paths com expressões regulares</h3>

Você pode restringir o valor dos parâmetros da sua URI com expressões regulares, dessa maneira:

{% highlight java %}
@Path("/cor/{cor:[0-9A-Fa-f]{6}}")
public void setCor(String cor) {
    //...
}
{% endhighlight %}

Tudo que estiver depois do ":" será tratado como uma regex, e a URI só vai casar se o parâmetro casar com a regex:

{% highlight jsp %}
/cor/a0b3c4 => casa
/cor/AABBCC => casa
/cor/branco => não casa
{% endhighlight %}

<h3>Path com injeção de variáveis</h3>

Em diversos casos, desejamos que a URI que identifica nosso recurso tenha como parte de seu valor, por exemplo, o identificador único do nosso recurso.
Suponha o exemplo de um controle de clientes onde meu identificador único (chave primária) é um número, podemos então mapear as URIs /cliente/{cliente.id} para a visualização de cada cliente.
Isto é, se acessarmos a URI /cliente/2, o método <strong>visualiza</strong> será invocado e o parâmetro cliente.id será setado para <strong>2</strong>. Caso a URI /cliente/1717 seja acessada, o mesmo método será invocado com o valor <strong>1717</strong>.
Dessa maneira, criamos URIs únicas para identificar recursos diferentes do nosso sistema. Veja o exemplo:

{% highlight java %}
@Resource
public class ClienteController {

  @Get
  @Path("/cliente/{cliente.id}")  
  public Cliente visualiza(Cliente cliente) {
    return ...
  }
}
{% endhighlight %}

Você pode ir além e setar diversos parâmetros pela URI:

{% highlight java %}
@Resource
public class ClienteController {

  @Get
  @Path("/cliente/{cliente.id}/visualiza/{secao}")  
  public Cliente visualiza(Cliente cliente, String secao) {
    return ...
  }
  
}
{% endhighlight %}

<h3>Vários paths para a mesma lógica</h3>

Você pode setar mais de um path para a mesma lógica fazendo:

{% highlight java %}
@Resource
public class ClienteController {

  @Path({"/cliente/{cliente.id}/visualiza/{secao}", "/cliente/{cliente.id}/visualiza/"})  
  public Cliente visualiza(Cliente cliente, String secao) {
    return ...
  }
  
}
{% endhighlight %}

<h3>Paths com *</h3>

Você também pode utilizar o * como método de seleção para a sua URI. O exemplo a seguir ignora qualquer coisa após a palavra foto/ :

{% highlight java %}
@Resource
public class ClienteController {

  @Get
  @Path("/cliente/{cliente.id}/foto/*")  
  public File foto(Cliente cliente) {
    return ...
  }
  
}
{% endhighlight %}

E agora o mesmo código, mas utilizado para baixar uma foto específica de um cliente:

{% highlight java %}
@Resource
public class ClienteController {

  @Get
  @Path("/cliente/{cliente.id}/foto/{foto.id}")  
  public File foto(Cliente cliente, Foto foto) {
    return ...
  }
  
}
{% endhighlight %}

Por vezes você deseja que o parâmetro a ser setado inclua também /s. Para isso você deve utilizar o padrão {...*}:

{% highlight java %}
@Resource
public class ClienteController {

  @Get
  @Path("/cliente/{cliente.id}/download/{path*}")  
  public File download(Cliente cliente, String path) {
    return ...
  }
  
}
{% endhighlight %}

<h3>Definindo prioridades para seus paths</h3>

É possível que algumas das nossas URIs possam ser tratada por mais de um método da classe:

{% highlight java %}
@Resource
public class PostController {

  @Get
  @Path("/post/{post.autor}")
  public void mostra(Post post) { ... }

  @Get
  @Path("/post/atual")
  public void atual() { ... }
}
{% endhighlight %}

A URI /post/atual pode ser tratada tanto pelo método mostra, quanto pelo atual. Mas eu quero que quando seja exatamente /post/atual o método executado seja o atual. O que eu quero é que o VRaptor teste primeiro o path do método atual, para não correr o risco de cair no método mostra.
Para fazer isso, podemos definir prioridades para os @Paths, assim o VRaptor vai testar primeiro os paths com maior prioridade, ou seja, valor menor de prioridade:

{% highlight java %}
@Resource
public class PostController {

  @Get
  @Path(value = "/post/{post.autor}", priority=Path.LOW)
  public void mostra(Post post) { ... }

  @Get
  @Path(value = "/post/atual", priority=Path.HIGH)
  public void atual() { ... }
}
{% endhighlight %}

Assim, o path "/post/atual" vai ser testado antes do "/post/{post.autor}", e o VRaptor vai fazer o que a gente gostaria que ele fizesse.
Você não precisa definir prioridades se tivermos as uris: /post/{post.id} e /post/atual, pois na /post/{post.id} o vraptor só vai aceitar números.

<h3>RoutesConfiguration</h3>

Por fim, a maneira mais avançada de configurar rotas de acesso aos seus recursos é com o uso de um <strong>RoutesConfiguration</strong>.
Esse componente deve ser configurado no escopo de aplicação e implementar o método config:

{% highlight java %}
@Component
@ApplicationScoped
public class CustomRoutes implements RoutesConfiguration {

    public void config(Router router) {
    }

}
{% endhighlight %}

De posse de um <strong>Router</strong>, você pode definir rotas para acesso a métodos e, o melhor de tudo, é que a configuração é refactor-friendly, isto é, se você alterar o nome de um método, a configuração também se altera, mas mantém a mesma URI:

{% highlight java %}
@Component
@ApplicationScoped
public class CustomRoutes implements RoutesConfiguration {

    public void config(Router router) {
        new Rules(router) {
            public void routes() {
                routeFor("/").is(ClienteController.class).list();
                routeFor("/cliente/random").is(ClienteController.class).aleatorio();
            }
        };
    }

}
{% endhighlight %}

Você pode também colocar parâmetros na URI e eles vão ser populados diretamente nos parâmetros do método. Você pode ainda adicionar restrições para esses parâmetros:

{% highlight java %}
// O método mostra recebe um Cliente que tem um id
routeFor("/cliente/{cliente.id}").is(ClienteController.class).mostra(null);
// Se eu quiser garantir que o parametro seja um número:
routeFor("/cliente/{cliente.id}").withParameter("cliente.id").matching("\\d+")
            .is(ClienteController.class).mostra(null);
{% endhighlight %}
