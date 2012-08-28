---
title: VRaptor3 - O guia inicial de 10 minutos
layout: page
section: 2
category: [pt, docs]
---

<h3>Começando o projeto: uma loja virtual</h3>

Vamos começar baixando o vraptor-blank-project do <a href="http://vraptor.caelum.com.br/download.jsp">site do VRaptor</a>. Esse blank-project já possui a configuração no web.xml e os JARs no WEB-INF/lib necessários para começar a desenvolver no VRaptor.
Como você pode ver, a única configuração necessária no web.xml é o filtro do VRaptor:

{% highlight xml %}
<filter>
    <filter-name>vraptor</filter-name>
    <filter-class>br.com.caelum.vraptor.VRaptor</filter-class>
</filter>

<filter-mapping>
    <filter-name>vraptor</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>FORWARD</dispatcher>
    <dispatcher>REQUEST</dispatcher>
</filter-mapping>
{% endhighlight %}

Você pode facilmente importar esse projeto no Eclipse, e rodá-lo clicando com o botão da direita e escolhendo Run as... / Run on server.... Escolha então um servlet container (ou faça o setup de um novo) e então acesse <a href="http://localhost:8080/vraptor-blank-project/">http://localhost:8080/vraptor-blank-project/</a>.
Você pode escolher, nas propriedades do projetor, dentro de Web Project Settings, o nome do contexto para algo melhor, como onlinestore. Agora, se você rodar esse exemplo deve ser possível acessar <a href="http://localhost:8080/onlinestore">http://localhost:8080/onlinestore</a> e ver <strong>It works!</strong> no navegador.


<div class="nota">
<h4>Nota</h4>
Se você está usando um container de servlet 3.0 (java EE 6), você nem precisa de web.xml, pois o VRaptor vai configurar esse filtro por meio do novo recurso de web-fragments.
</div>

<h3>Um cadastro de produtos</h3>

Para começar o sistema, vamos começar com cadastro de produtos. Precisamos de uma classe que vai representar o produto, e vamos usá-la para guardar produtos no banco, usando o Hibernate:

{% highlight java %}
@Entity
public class Produto {
    @Id
    @GeneratedValue
    private Long id;
    
    private String nome;
    private String descricao;
    private Double preco;
    
    // getter e setters e métodos de negócio que julgar necessário
}
{% endhighlight %}

Precisamos também de uma classe que vai controlar o cadastro de produtos, tratando requisições web. Essa classe vai ser o Controller de produtos:

{% highlight java %}
public class ProdutosController {
}
{% endhighlight %}

A classe ProdutosController vai expor URIs para serem acessadas via web, ou seja, vai expor recursos da sua aplicação. E, para indicar isso, você precisa anotá-la com com @Resource:

{% highlight java %}
@Resource
public class ProdutosController {
}
{% endhighlight %}

Ao colocar essa anotação na classe, todos os métodos públicos dela serão acessíveis pela web. Por exemplo, se tivermos um método lista na classe:

{% highlight java %}
@Resource
public class ProdutosController {
    public List<Produto> lista() {
        return new ArrayList<Produto>();
    }
}
{% endhighlight %}

o VRaptor automaticamente redirecionará todas as requisições à URI /produtos/lista para esse método. Ou seja, a convenção para a criação de URIs é /<nome_do_controller>/<nome_do_metodo.
Ao terminar a execução do método, o VRaptor fará o dispatch da requisição para o JSP /WEB-INF/jsp/produtos/lista.jsp. Ou seja, a convenção para a view padrão é /WEB-INF/jsp/<nome_do_controller>/<nome_do_metodo>.jsp.
O método lista retorna uma lista de produtos, mas como acessá-la no JSP? No VRaptor, o retorno do método é exportado para a JSP por meio de atributos da requisição. No caso do método lista, vai existir um atributo chamado <strong>produtoList</strong> contendo a lista retornada:

{% highlight jsp %}
<ul>
<c:forEach items="${produtoList}" var="produto">
    <li> ${produto.nome} - ${produto.descricao} </li>
</c:forEach>
</ul>
{% endhighlight %}

A convenção para o nome dos atributos exportados é bastante intuitiva: se for uma collection, como o caso do método acima, o atributo será <tipoDaCollection>List, produtoList no caso; se for uma classe qualquer vai ser o nome da classe com a primeira letra minúscula. Se o método retorna Produto, o atributo exportado será produto.
Veremos em outro capítulo que podemos expor mais de um objeto sem usar o retorno, e sim por meio do Result, onde podemos dar nome à variável exposta.

<h3>Criando o ProdutoDao: injeção de Dependências</h3>

O VRaptor usa fortemente o conceito de Injeção de Dependências e Inversão de Controle. A ideia é que você não precisa criar ou buscar as dependências da sua classe, você simplesmente as recebe e o VRaptor se encarrega de criá-las pra você. Mais informações no capítulo de Injeção de Dependências.
Estamos retornando uma lista vazia no nosso método lista. Seria muito mais interessante retornar uma lista de verdade, por exemplo todas os produtos cadastrados no sistema. Para isso vamos criar um DAO de produtos, para fazer a listagem:

{% highlight java %}
public class ProdutoDao {

    public List<Produto> listaTodos() {
        return new ArrayList<Produto>();
    }
    
}
{% endhighlight %}

E no nosso ProdutosController podemos usar o dao pra fazer a listagem de produtos:

{% highlight java %}
@Resource
public class ProdutosController {
    
    private ProdutoDao dao;
    
    public List<Produto> lista() {
        return dao.listaTodos();
    }
    
}
{% endhighlight %}

Podemos instanciar o ProdutoDao direto do controller, mas é muito mais interessante aproveitar o gerenciamento de dependências que o VRaptor faz e receber o DAO no construtor! E, para que isso seja possível, basta anotar o DAO com @Component e o VRaptor vai se encarregar de criá-lo e injetá-lo na sua classe:

{% highlight java %}
@Component
public class ProdutoDao {
    //...
}

@Resource
public class ProdutosController {
    
    private ProdutoDao dao;

    public ProdutosController(ProdutoDao dao) {
        this.dao = dao;
    }
        
    public List<Produto> lista() {
        return dao.listaTodos();
    }
    
}
{% endhighlight %}

<h3>Formulário de adição: redirecionamento</h3>

Temos uma listagem de Produtos, mas ainda não temos como cadastrá-los. Vamos então criar um formulário de adição de produtos. Para não ter que acessar o JSP diretamente, vamos criar uma lógica vazia que só redireciona para o JSP:

{% highlight java %}
@Resource
public class ProdutosController {
    //...
    public void form() {
    }
}
{% endhighlight %}

Podemos acessar o formulário, que estará em /WEB-INF/jsp/produtos/form.jsp, pela URI /produtos/form:

{% highlight jsp %}
<form action="<c:url value='/produtos/adiciona'/>">
    Nome:             <input type="text" name="produto.nome" /><br/>
    Descrição:    <input type="text" name="produto.descricao" /><br/>
    Preço:            <input type="text" name="produto.preco" /><br/>
    <input type="submit" value="Salvar" />
</form>
{% endhighlight %}

Como o formulário vai salvar o Produto pela URI /produtos/adiciona, precisamos criar esse método no nosso controller:

{% highlight java %}
@Resource
public class ProdutosController {
    //...
    public void adiciona() {
    }
}
{% endhighlight %}

Repare nos nomes dos nossos inputs: <strong>produto.nome</strong>, <strong>produto.descricao</strong> e <strong>produto.preco</strong>. Se recebermos um Produto no método adiciona com o nome <strong>produto</strong>, o VRaptor vai popular os seus campos <strong>nome</strong>, <strong>descricao</strong> e <strong>preco</strong>, usando os seus setters no Produto, com os valores digitados nos inputs. Inclusive o campo preco, vai ser convertido para Double antes de ser setado no produto. Veja mais sobre isso no capítulo de converters.
Então, usando os nomes corretamente nos inputs do form, basta criar seu método adiciona:

{% highlight java %}
@Resource
public class ProdutosController {
    //...
    public void adiciona(Produto produto) {
        dao.adiciona(produto);
    }
}
{% endhighlight %}

Geralmente, depois de adicionar algo no sistema queremos voltar para a sua listagem, ou para o formulário novamente. No nosso caso, queremos voltar pra listagem de produtos ao adicionar um produto novo. Para isso existe um componente do VRaptor: o <strong>Result</strong>. Ele é responsável por adicionar atributos na requisição, e por mudar a view a ser carregada. Se eu quiser uma instância de Result, basta recebê-lo no construtor:

{% highlight java %}
@Resource
public class ProdutosController {
    public ProdutosController(ProdutoDao dao, Result result) {
        this.dao = dao;
        this.result = result;
    }
}
{% endhighlight %}

E para redirecionar para a listagem basta usar o result:

{% highlight java %}
result.redirectTo(ProdutosController.class).lista();
{% endhighlight %}

Podemos ler esse código como: Como resultado, redirecione para o método lista do ProdutosController. A configuração de redirecionamento é 100% java, sem strings envolvidas! Fica explícito no seu código que o resultado da sua lógica não é o padrão e qual resultado você está usando! Você não precisa ficar se preocupando com arquivos de configuração! Mais ainda, se eu quiser mudar o nome do método lista, eu não preciso ficar rodando o sistema inteiro procurando onde estão redirecionando pra esse método, basta usar o refactor do eclipse, por exemplo, e tudo continua funcionando!
Então nosso método adiciona ficaria:

{% highlight java %}
public void adiciona(Produto produto) {
    dao.adiciona(produto);
    result.redirectTo(ProdutosController.class).lista();
}
{% endhighlight %}

Veja mais informações sobre o Result no capítulo Views e Ajax.

<h3>Validação</h3>

Não faz muito sentido adicionar um produto sem nome no sistema, nem um produto com preço negativo. Antes de adicionar o produto, precisamos verificar se é um produto válido, com nome e preço positivo, e caso não seja válido voltamos para o formulário com mensagens de erro. Para fazermos isso, podemos usar um componente do VRaptor chamado Validator. Você pode recebê-lo no construtor do seu Controller, e usá-lo da seguinte maneira:

{% highlight java %}
@Resource
public class ProdutosController {
    public ProdutosController(ProdutoDao dao, Result result, Validator validator) {
        //...
        this.validator = validator;
    }
    
    public void adiciona(Produto produto) {
        validator.checking(new Validations() { {
            that(!produto.getNome().isEmpty(), "produto.nome", "nome.vazio");
            that(produto.getPreco() > 0, "produto.preco", "preco.invalido");
        } });
        validator.onErrorUsePageOf(ProdutosController.class).form();
        
        dao.adiciona(produto);
        result.redirectTo(ProdutosController.class).lista();
    }
}
{% endhighlight %}

Podemos ler as validações da seguinte maneira: Valide que o nome do produto não é vazio e que o preço do produto é maior que zero. Se acontecer um erro, use como resultado a página do form do ProdutosController. Ou seja, se por exemplo o nome do produto for vazio, vai ser adicionada a mensagem de erro para o campo "produto.nome", com a mensagem "nome.vazio" internacionalizada. Se acontecer algum erro, o sistema vai voltar pra página do formulário, com os campos preenchidos e com mensagens de erro que podem ser acessadas da seguinte maneira:

{% highlight jsp %}
<c:forEach var="error" items="${errors}">
    ${error.category}  ${error.message}<br />
</c:forEach>
{% endhighlight %}

Veja mais informações sobre o Validator no capítulo de Validações.

Com o que foi visto até agora, você já consegue fazer 90% da sua aplicação! As próximas sessões desse tutorial mostram a solução para alguns problemas frequentes que estão nos outros 10% da sua aplicação.

<h3>Usando o Hibernate para guardar os Produtos</h3>

Agora vamos fazer uma implementação de verdade do ProdutoDao, usando o Hibernate para persistir os produtos. Para isso, nosso ProdutoDao precisa de uma Session. Como o VRaptor usa injeção de dependências, basta receber uma Session no construtor!

{% highlight java %}
@Component
public class ProdutoDao {
    
    private Session session;
    
    public ProdutoDao(Session session) {
        this.session = session;
    }

    public void adiciona(Produto produto) {
        session.save(produto);
    }
    //...
}
{% endhighlight %}

O VRaptor precisa saber como criar essa Session, e eu não posso simplesmente colocar um @Component na Session pois é uma classe do Hibernate! Para isso existe a interface ComponentFactory, que você pode usar pra criar uma Session. 

Veja mais informações de como fazer ComponentFactories no capítulo de Componentes. Você pode, ainda, usar os ComponentFactories que já estão disponíveis para isso no VRaptor, como mostra o capítulo de Utils.

<h3>Controlando transações: Interceptors</h3>

Muitas vezes, queremos interceptar todas as requisições (ou uma parte delas) e executar alguma lógica, como acontece com o controle de transações. Para isso, existem os Interceptors no VRaptor. 

Saiba mais sobre eles no capítulo de Interceptors. Existe um TransactionInterceptor já implementado no VRaptor, saiba como usá-lo no capítulo de Utils.

<h3>Carrinho de compras: Componentes na sessão</h3>

Se quisermos criar um carrinho de compras no nosso sistema, precisamos de alguma forma manter os itens do carrinho na Sessão do usuário. Para fazer isso, podemos criar um componente que está no escopo de sessão, ou seja, ele vai ser único na sessão do usuário. Para isso, basta criar um componente anotado com @SessionScoped:

{% highlight java %}
@Component
@SessionScoped
public class CarrinhoDeCompras {
    private List<Produto> itens = new ArrayList<Produto>();
    
    public List<Produto> getTodosOsItens() {
        return itens;
    }
    
    public void adicionaItem(Produto item) {
        itens.add(item);
    }
}
{% endhighlight %}

Como esse carrinho de compras é um componente, podemos recebê-lo no construtor do controller que vai cuidar do carrinho de compras:

{% highlight java %}
@Resource
public class CarrinhoController {

    private final CarrinhoDeCompras carrinho;
    
    public CarrinhoController(CarrinhoDeCompras carrinho) {
        this.carrinho = carrinho;
    }

    public void adiciona(Produto produto) {
        carrinho.adicionaItem(produto);
    }
    
    public List<Produto> listaItens() {
        return carrinho.getTodosOsItens();
    }
}
{% endhighlight %}

Além do escopo de sessão existe o escopo de Aplicação com a anotação @ApplicationScoped. Os componentes anotados com @ApplicationScoped serão criados apenas uma vez em toda a aplicação.

<h3>Um pouco de REST</h3>

Seguindo a idéia REST de que URIs devem identificar recursos na rede para então podermos fazer valer as diversas vantagens estruturais que o protocolo HTTP nos proporciona, note o quão simples fica mapear os diversos métodos HTTP para a mesma URI, e com isso invocar diferentes métodos. Por exemplo, queremos usar as seguintes URIs para o cadastro de produtos:

{% highlight jsp%}
	GET /produtos - lista todos os produtos
	POST /produtos - adiciona um produto
	GET /produtos/{id} - visualiza o produto com o id passado
	PUT /produtos/{id} - atualiza as informações do produto com o id passado
	DELETE /produtos/{id} - remove o produto com o id passado
{% endhighlight %}

Para criar esse comportamento REST no VRaptor, podemos usar as anotações @Path - que muda qual é a URI que vai acessar o determinado método, e as anotações com os nomes dos métodos HTTP @Get, @Post, @Delete, @Put, que indicam que o método anotado só pode ser acessado se a requisição estiver com o método HTTP indicado. Então, uma versão REST do nosso ProdutosController seria:

{% highlight java %}
public class ProdutosController {
    //...
    
    @Get @Path("/produtos")
    public List<Produto> lista() {...}
    
    @Post("/produtos")
    public void adiciona(Produto produto) {...}

    @Get("/produtos/{produto.id}")
    public void visualiza(Produto produto) {...}
    
    @Put("/produtos/{produto.id}")
    public void atualiza(Produto produto) {...}
    
    @Delete("/produtos/{produto.id}")
    public void remove(Produto produto) {...}
    
}
{% endhighlight %}

Note que podemos receber parâmetros nas URIs. Por exemplo, se chamarmos a URI <strong>GET /produtos/5</strong>, o método visualiza será invocado, e o parâmetro produto vai ter o id populado com 5.
Mais informações sobre isso no capítulo de Resources-REST.

<h3>Arquivo de mensagens</h3>

Internacionalização (i18n) é um recurso poderoso, e que está presente em quase todos os frameworks Web,  hoje em dia. E não é diferente no VRaptor3. Com i18n podemos fazer com que nossa aplicação suporte várias línguas (francês, português, espanhol, inglês, etc) de uma maneira que não nos cause muito esforço, bastando apenas fazermos a tradução das mensagens da nossa aplicação.
Para isso, é só criarmos um arquivo chamado messages.properties e disponibilizá-lo no classpath da nossa aplicação (WEB-INF/classes). Esse arquivo conterá várias linhas compostas por um conjunto de chave/valor, como por exemplo:

{% highlight jsp %}
campo.nomeUsuario = Nome de Usuário
campo.senha = Senha
{% endhighlight %}

Até então está fácil, mas e se quisermos criar esses arquivos para várias línguas, como por exemplo, inglês? Simples, basta criarmos um outro arquivo properties chamado messages_en.properties. Repare no sufixo _en no nome do arquivo. Isso indica que quando o usuário acessar sua aplicação em uma máquina configurada com locale em inglês as mensagens desse arquivo serão utilizadas. O conteúdo desse arquivo então ficaria:

{% highlight jsp %}
campo.nomeUsuario = Username
campo.senha = Password
{% endhighlight %}

Repare que as chaves são mantidas, mudando apenas o valor para a língua escolhida.
Para usar essas mensagens em seus arquivos JSP, você pode utilizar a JSTL. Dessa forma, o código ficaria:

{% highlight jsp %}
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
    <body>
        <fmt:message key="campo.usuario" /> <input name="usuario.nomeUsuario" />
        
        <br />
        
        <fmt:message key="campo.senha" /> <input type="password" name="usuario.senha" />
        
        <input type="submit" />
    </body>
</html>
{% endhighlight %}
