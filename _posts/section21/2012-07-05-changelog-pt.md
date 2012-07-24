---
title: Changelog
layout: page
language: pt
section: 21
---


<h3>3.4.1</h3>
<ul>
	<li><strong>PicoProvider</strong> agora também seta atributos do request e da session com os nomes dos
	componentes, da mesma forma que o Guice e o Spring</li>
	<li><strong>SpringProvider</strong> agora emula o comportamento do ContextLoaderListener, melhorando
	a integração com os componentes do Spring sem precisar registrar o listener no web.xml</li>
	<li>melhor serialização com o XStream, suportando mais casos</li>
	<li>correção do plugin do GAE</li>
	<li>Locale based converters para tipos primitivos (by Rafael Dipold)</li>
	<li><strong>Mudança de padrão</strong> IOGI como instanciador de objetos padrão, ao invés do OGNL</li>
	<li>suporte a @Consumes("application/x-www-form-urlencoded") (by Celso Dantas)</li>
	<li><strong>todas</strong> as dependências declaradas no pom.xml agora estão no maven</li>
	<li>suporte a @HeaderParam nos métodos do controller:
		{% highlight java %}
public void metodo(@HeaderParam("X-meu-param") String param) {..}
		{% endhighlight %}
	</li>
	<li>suporte a upload de vários arquivos, com nomes file&#91;&#93; (by dgouvea)</li>
	<li><strong>bugfix</strong> xstream no GAE</li>
	<li>atualizando Pico container de 2.8 para 2.13.6 (by Bruno Fuster)</li>
	<li>corrigindo scanning de classes nas diversas versões do JBoss (by garcia-jj)</li>
	<li>GuiceProvider agora dá preferência ao construtor anotado com @Inject (by cairesvs)</li>
	<li><strong>bugfix</strong> xstream não é mais opcional</li>
	<li><strong>bugfix</strong> ordenando parâmetros da requisição, assim é possível usar o converter
antes de setar os parâmetros (by Celso Dantas)</li>
	<li><strong>bugfix</strong> ExtJSView gerando json sem referências (by Éverton Trindade)</li>
	<li><strong>bugfix</strong> registro de stereotype handlers customizados com o Guice</li>
	<li><strong>bugfix</strong> chamada de defaultView() dentro de forwards agora vai para a view correta</li>
	<li>I18n das mensagens de validação agora são feitas no último momento possível, assim é possível setar o locale na jsp</li>
	<li>Possibilidade de sobrescrever o StaticContentHandler.</li>
</ul>


<h3>3.4.0</h3>
<ul>
	<li>novo status accepted (result.use(status()).accepted()) - by Vinicius Oliveira</li>
	<li>bugfix - race condition na ordenação de interceptors</li>
	<li>mock para o bean validations - by @seufagner</li>
	<li>bugfix - suportando converter para collections no XStream*Serialization</li>
	<li>mudando prioridades padrão das anotações de método (@Get, @Post, etc) para Path.DEFAULT</li>
	<li>atualizando jars das dependências do VRaptor - by @wbotelhos</li>
	<li>suporte a javassist para gerar os proxies - by garcia-jj</li>
	<li><strong>quebra de compatibilidade interna</strong> - refatoração da parte de proxies - by garcia-jj. Para todos que usavam diretamente o ObjenesisProxifier ou o DefaultProxifier, o novo jeito é:

	  {% highlight java %}
new ObjenesisProxifier() ==> new CglibProxifier(new ObjenesisInstanceCreator());
new DefaultProxifier() ==> new CglibProxifier(new ReflectionInstanceCreator());
	  {% endhighlight %}

	</li>
	<li>evitando o wrapping de runtime exceptions nos forwardings</li>
	<li>bugfix - upload no GAE, quando usando o Pico</li>
	<li>suportando popular parâmetros de objetos conversíveis:

		{% highlight java %}
bola = amarela  ==> convertido pelo BolaConverter
bola.tamanho = 23 ==> continuando a popular
		{% endhighlight %}

	</li>
	<li>suportando popular parâmetros usando atributos de request:

		{% highlight java %}
request.setAttribute("bola", new Bola()); //num interceptor, por exemplo

//...

public void logica(Bola bola) { //injetado com o atributo do request!
//...
}
		{% endhighlight %}

	</li>
	<li>suportando popular parâmetros usando injeção de dependências (somente interfaces):
		{% highlight java %}
public void logica(Parametro parametro, Validator validator) {
	//parametro populado com parametros do request
	//validator vindo com injeção de dependências
}
		{% endhighlight %}
	</li>
	<li>plugin - @Load nos parâmetros das lógicas (....util.hibernate.extra e ....util.jpa.extra)

		{% highlight java %}
@Path("/entidade/{entidade.id}") //o id precisa estar preenchido no request
public void minhaLogica(@Load Entidade entidade) {
	//entidade já vem carregada do banco, pelo id, e se não existir retorna 404 automaticamente
	//isso desabilita população da entidade por outros parâmetros do request
}
		{% endhighlight %}
	</li>
	<li>bugfix - serialização de classes com tipos genéricos - by @wbotelhos</li>
	<li>guice - suportando receber List<QualquerComponent> como dependência</li>
	<li>plugin - vraptor-flex-plugin melhorado, suportando invocação de lógicas, e injeção de dependências - by @davidpaniz</li>
	<li>test - MockSerializationResult, para poder fazer asserções nos objetos serializados. - by Vinicius oliveira</li>
	<li>plugin - vraptor-scala-plugin melhorado (usando acessores de propriedades do scala, e ignorando métodos com $).
		Blank project scala usando sbt. Scala plugin feito em scala agora ;)
	</li>
	<li>melhor suporte ao flash scope automático.</li>
	<li>bugfix - corrigido scan de classes no JBoss - by garcia-jj</li>
	<li>internal - suporte ao OGNL refatorado para melhorar a estensibilidade. - pair @qmx</li>
	<li>bugfix - regexes nos @Path - by Anderson Parra</li>
	<li>guice na versão estável (3.0)</li>
	<li>Hibernate Validator deprecado (preferência para usar Bean Validations)</li>
	<li>bugfix - result.include(null) gerava NullPointerException</li>
	<li>bugfix - conversão de listas - by Narciso Benigno</li>
	<li>melhores docs do plugin pro GAE - by Roberto Nogueira</li>
	<li>serialização de Links em JSON - by A.C de Souza</li>
	<li>lazy 18n para validações:

	  {% highlight java %}
validator.checking(new Validations() { {
	that(a == b, i18n("category"), "message.key", "params"); 
	// tanto category quanto message.key serão i18n na hora de mostrar a mensagem,
	// usando o resource bundle padrão (do Localization)
}});
	  {% endhighlight %}
	</li>
	<li>deixando a ordenação dos serializations estensível (no result.use(representation())) - by A.C de Souza</li>
	<li>Registro do converters do XStream automático. Basta anotar com @Component. - by Rafael Viana</li>
	<li>Refatoração dos XStream (de)serializers para usar um XStreamBuilder centralizado e estensível - by Rafael Viana <strong>quebra compatibilidade de quem sobrescreveu algum Serializer/Deserializer. Testem!</strong></li>
	<li>bugfix - validator.onErrorSendBadRequest não estava setando status 400 - by @wbotelhos</li>
	<li>linkTo para os jsps:
	  	{% highlight java %}
@Resource
public class ProdutoController {
	@Path("/produtos/{id}")
	public void carrega(Long id) {..}
}
		{% endhighlight %}

	  	jsp:

		{% highlight jsp %}
${linkTo[ProdutoController].carrega[2]} ==> /produtos/2
		{% endhighlight %}
	</li>
</ul>


<h3>3.3.1</h3>

<ul>
	<li>bugfix - corrigido o scannotation como obrigatório no maven</li>
	<li>bugfix - corrigido ConcurrentModificationException na ordenação dos interceptors</li>
	<li>atualizada dependência do spring de 3.0.0 para 3.0.5</li>
	<li>bugfix - corrigida chamada do @PostConstruct nos componentes @ApplicationScoped quando
	usando o Spring como DI container</li>
	<li>melhoria nas documentações</li>
	<li>bugfix - redirect para @Path's com regexes</li>
	<li>bugfix - Hibernate e JPA Transaction interceptors agora dão rollback quando existem erros de validação</li>
</ul>

<h3>3.3.0</h3>

<div class="nota">
	<h4>Mudança de jars</h4>
	<ul>
		<li>troque google-collect 1.0rc por guava-r08</li>
		<li>scannotations 1.0.2 agora é obrigatório</li>
	</ul>
</div>

<ul>
	<li><strong>melhor integração com o Spring</strong>: agora os componentes do Spring podem acessar os do VRaptor e vice-versa</li>
	<li>guice: @PostConstruct e @PreDestroy funcionando completamente</li>
	<li>guice: todos os componentes request e session scoped são exportados para a view do mesmo jeito que com o spring (pelo nome da classe)</li>
	<li><strong>mudança da estratégia da ordem dos interceptors</strong>: agora é possível ordenar interceptors na anotação @Intercepts, dizendo quais interceptors devem rodar antes ou depois do interceptor anotado.

		{% highlight java %}
@Intercepts(before=AnInterceptor.class, after=AnotherInterceptor.class)
		{% endhighlight %}

		Assim o VRaptor executa os interceptors em uma ordem que respeita o before e o after de todos os interceptors.
		Desse modo a interface InterceptorSequence fica deprecada.
	</li>
	<li>As anotações de verbos HTTP agora também podem definir o path do método:

		{% highlight java %}
@Get("/items/{id}"), @Post("/items"), etc 
		{% endhighlight %}

	</li>
	<li>bugfix: @Transactional do Spring agora pode ser usado em qualquer classe (com as limitações do spring aop)</li>
	<li>bugfix: upload de arquivos com mesmo nome</li>
	<li>bugfix: web-fragments.xml no jboss 6</li>
	<li>bugfix: melhor suporte para arrays como parâmetros</li>
	<li>novas implementações de Download: ByteArrayDownload e JFreeChartDownload</li>
	<li>nova view jsonp:
		{% highlight java %}
result.use(jsonp()).withCallback("oCallback").from(objeto).serialize();
		{% endhighlight %}

		que retorna

		{% highlight java %}
aCallback({"objeto": {...}})
		{% endhighlight %}
	</li>
	<li>removida a dependência direta com o commons-io</li>
	<li>métodos do PageResult renomeados para ficarem consistentes com o resto do sistema</li>
	<li>melhores logs de upload</li>
	<li>refatoração nos converters do vraptor: agora eles usam o Localization para pegar o Locale e o bundle</li>
	<li>removida classe Hibernate. Use validator.validate(objeto)</li>
	<li>JSON serializado com indentação opcional.</li>
</ul>

<h3>3.2.0</h3>

<ul>
	<li>várias melhorias na performance: por volta de 60% menos no tempo de requisição</li>
	<li><strong>quebra de compatibilidade interna</strong>: interface InterceptorStack reorganizada</li>
	<li>melhor implementação do método accepts dos interceptors internos do VRaptor</li>
	<li>suporte beta ao Google Guice, para ser usado ao invés do Spring</li>
	<li>Pico provider não é mais deprecated</li>
	<li>Agora é possível escolher o DI container sem precisar mudar o web.xml. Se os jars do Spring estiverem no classpath, o Spring será usado; se forem os jars do PicoContainer ele será usado, e da mesma forma pros jars do Guice. Os jars estão na pasta lib/containers do zip do VRaptor</li>
	<li><strong>quebra de compatibilidade interna</strong>: interfaces <em>Converters</em>, <em>Router</em> e construtor da classe <em>PathAnnotationRoutesParser</em> alterados. RouteBuilder convertido para interface => implementação DefaultRouteBuilder.
		Para quem estendia o RoutesParser basta trocar o delegate constructor.
		Para quem instanciava o RouteBuilder diretamente basta instanciar o DefaultRouteBuilder.</li>
	<li>nova anotação @Lazy. Use-a nos interceptors em que o método accepts não depende do estado interno do interceptor:
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
		Nesse caso o MeuLazyInterceptor só será instanciado se o método accepts retornar true.
		Uma instância não funcional do MyLazyInterceptor será usada para chamar o método accepts, então esse método não pode usar o estado interno do interceptor.
		Não use o @Lazy se o método accepts for trivial (sempre retornar true)
	</li>
	<li><strong>pequena quebra de compatibilidade</strong>: prioridade padrão do @Path agora é Integer.MAX_INTEGER/2. Antes era Integer.MAX_INTEGER - 1. Apesar dessa quebra, acreditamos que isso não irá afetar os sistemas já implementados</li>
	<li>prioridades do @Path agora podem ser definidas por constantes:
		{% highlight java %}
@Path(value="/url", priority=Path.HIGHEST)
@Path(value="/url", priority=Path.HIGH)
@Path(value="/url", priority=Path.DEFAULT)
@Path(value="/url", priority=Path.LOW)
@Path(value="/url", priority=Path.LOWEST)
		{% endhighlight %}</li>
	<li>Suporte a upload da Servlet 3.0 (por garcia-jj)</li>
	<li>new Exception handlers (por garcia-jj)

	  {% highlight java %}
result.on(SomeException.class).forwardTo(Controller.class).method();
//se uma SomeException é lançada, a requisição será redirecionada
	  {% endhighlight %}</li>

	<li>Nova interface <em>TwoWayConverter</em> para conversões bidirecionais</li>
	<li>suporte nativo a requisições OPTIONS</li>
	<li>fix: 405 ao invés de 500 em requisições com HTTP metodo desconhecido</li>
	<li>mais converters do Joda Time (por Rodolfo Liviero)</li>
	<li>melhorias no Scala Blank Project (por Pedro Matiello)</li>
	<li>bugfix: null Accept Header gera respostas html</li>
</ul>


<h3>3.1.3</h3>

<ul>
	<li>Scala Blank Project</li>
	<li>melhor estratégia no escopo flash</li>
	<li>começo do suporte pra javax.inject API. Agora é possível dar nomes para os parâmetros de uma lógica:</li>
	<li>Corrigidos bugs do novo validator</li>
	<li>corrigido bug do char como parâmetro na URI</li>
	<li>Corrigido bug para poder aceitar browsers que trabalham mal com o header Accepts

		{% highlight java %}
public void logica(@Named("um_nome") String outroNome) {...}
		{% endhighlight %}

		Nesse caso, o parâmetro aceito na requisição se chamará 'um_nome'.
	</li>
	<li>Melhor suporte ao GAE</li>
	<li>novo método no http result:

		{% highlight java %}
result.use(http()).body(conteudo);
		{% endhighlight %}

		conteudo pode ser uma String, um InputStream ou um Reader.
	</li>
	<li>mais métodos disponíveis no result.use(status())</li>
	<li>novo método : result.use(representation()).from(objeto, alias)</li>
	<li>suporte a selects múltiplos:

		{% highlight java %}
public void logica(List<String> abc) {...}
		{% endhighlight %}

		{% highlight xml %}
<select name="abc[]" multiple="multiple">...</select>
		{% endhighlight %}
	</li>
	<li>status 406 automatico no result.use(representation())</li>
	<li>Agora é possível registrar os componentes opcionais do vraptor no parâmetro packages do web.xml:

		{% highlight xml %}
<context-param>
	<param-name>br.com.caelum.vraptor.packages</param-name>
	<param-value>
		br.com.caelum.vraptor.util.hibernate, // Session e SessionFactory 
		br.com.caelum.vraptor.util.jpa, // EntityManager e EntityManagerFactory
		br.com.caelum.vraptor.converter.l10n, //Converters numericos localizados
		br.com.caelum.vraptor.http.iogi // suporte a parâmetros imutáveis
	</param-value>
</context-param>
		{% endhighlight %}
	</li>
	<li>renderizar null para sua representação significa retornar 404</li>
	<li>nova classe: JsonDeserializer</li>
	<li>MultipartInterceptor agora é opcional</li>
	<li>bugfix: arrays de tamanho == 1 agora são suportados como parâmetros de lógicas</li>
	<li>Pico provider deprecated</li>
	<li>Validations agora usa o bundle (e locale) da requisição</li>
	<li>ValidationMessage agora implementa Serializable</li>
	<li>novo método: result.use(status()).badRequest(errorList); que serializa a lista de erros passada usando result.use(representation()).from(errorList, "errors");</li>
	<li>atalhos no Validator:

		{% highlight java %}
validator.onErrorForwardTo(controller).logica();
validator.onErrorRedirectTo(controller).logica();
validator.onErrorUsePageOf(controller).logica();
		{% endhighlight %}

		onde controller pode ser uma classe ou o this, como acontece no Result.
		E ainda o atalho:

		{% highlight java %}
validator.onErrorSendBadRequest();
		{% endhighlight %}

		que retorna o status Bad Request (400) e serializa a lista de erros de validação de acordo com o header Accept da requisição (result.use(representation()))
	</li>
</ul>

<h3>3.1.2</h3>

<ul>
	<li>Blank project agora rodando também no netbeans 6.8</li>
	<li>Encoding agora suportado quando faz upload de arquivos no Google App Engine</li>
	<li>bugfix: validator.onErrorUse(json()).... não dá mais NullPointerException</li>
	<li>Serializers tem agora o método recursive:

		{% highlight java %}
result.use(xml()).from(meuObjeto).recursive().serialize();
		{% endhighlight %}

		Assim, toda a árvore de objetos a partir do meuObjeto será serializada. 
	</li>
	<li>Os parâmetros das mensagens do Validations agora também podem ser internacionalizados:

		{% highlight java %}
// idade = Idade
// maior_que = {0} deveria ser maior que {1}

validator.checking(new Validations() { {
	that(idade > 18, "idade", "maior_que", i18n("idade"), 18);
	//resulta na mensagem "Idade deveria ser maior que 18"  
}});
		{% endhighlight %}

	</li>
	<li>Proxies do Hibernate agora são serializados (quase) como classes normais (graças ao Tomaz Lavieri)</li>
	<li>Ao serializar para json agora é possível excluir o root (graças ao Tomaz Lavieri):

		{% highlight java %}
result.use(json()).from(carro).serialize(); //=> {'carro': {'cor': 'azul'}}
result.use(json()).withoutRoot().from(carro).serialize(); //=> {'cor': 'azul'} 
		{% endhighlight %}
	</li>
	<li>Google collections atualizado para a versão 1.0</li>
	<li>corrigido bug das chaves dentro de expressões regulares dentro do @Path</li>
	<li>as anotações do XStream agora são lidas automaticamente quando você usa a serialização padrão do vraptor</li>
	<li>quando um arquivo é maior do que o limite de tamanho de arquivo é criado um erro de validação ao invés de uma exceção genérica</li>
	<li>mais atalhos na interface Result:
	
		{% highlight java %}
redirectTo("uma/uri")			=>  use(page()).redirect("uma/uri")
notFound()						=>  use(status()).notFound()
nothing()						=>  use(nothing());
permanentlyRedirectTo(Controller.class) 	
		=> use(status()).movedPermanentlyTo(Controller.class);
permanentlyRedirectTo("uma/uri") 	=> use(status()).movedPermanentlyTo("uma/uri");
permanentlyRedirectTo(this)		=> use(status()).movedPermanentlyTo(this.getClass());
		{% endhighlight %}
	</li>
	<li>adicionado novo método à interface <em>Validator</em> (graças ao Otávio Garcia)

		{% highlight java %}
validator.validate(objeto);
		{% endhighlight %}

		Esse método vai validar o objeto usando o Hibernate Validator 3, a Java Validation API (JSR303), ou qualquer implementação da interface BeanValidator anotada com <em>@Component</em>
	</li>
	<li>novos converters de BigDecimal, Double e Float, que levam em consideração o Locale para converter os valores (graças ao Otávio Garcia).
		Para usá-los basta adicionar ao web.xml:

		{% highlight xml %}
<context-param>
	<param-name>br.com.caelum.vraptor.packages</param-name>
	<param-value>!!valor anterior!!,br.com.caelum.vraptor.converter.l10n</param-value>
</context-param>
		{% endhighlight %}
	</li>
</ul>

<h3>3.1.1</h3>

<ul>
	<li>VRaptor 3 publicado no repositório central do maven!

		{% highlight xml %}
<dependency>
	<groupId>br.com.caelum</groupId>
	<artifactId>vraptor</artifactId>
	<version>3.1.1</version>
</dependency>
		{% endhighlight %}
	</li>
	<li>nova implementação do Outjector. Agora quando acontecem erros de validação os objetos populados são replicados para a próxima requisição, e não mais os parâmetros do request, prevenindo class cast exceptions nas taglibs</li>
	<li>fixados alguns bugs da compatibilidade com o VRaptor 2</li>
</ul>

<h3>3.1.0</h3>

<ul>
	<li>agora é possível serializar coleções usando result.use(xml()) e result.use(json())</li>
	<li>novo escopo @PrototypeScoped, que cria sempre uma nova instância da classe anotada cada vez que ela for requisitada</li>
	<li>nova view: result.use(Results.representation()).from(objeto).serialize();

		Essa view tenta descobrir o formato da requisição (via _format ou o header Accept) e renderizar o objetodado nesse formato. Por enquanto apenas xml e json são suportados, mas é possível criar serializadores para qualquer formato. Se o formato não foi passado ou ele não é suportado, o jsp padrão vai ser mostrado
	</li>
	<li>bugfix: os parâmetros agora são passados via array no escopo Flash, então tudo vai funcionar como deveria no GAE</li>
	<li>bugfix: agora o validator.onErrorUse(...) funciona com todos os Results padrão</li>
	<li>bugfix: retornar um Download/File/InputStream null não dá mais NullPointerException se já houve algum redirecionamento (result.use(...))</li>
	<li>bugfix: result.use(page()).redirect("...") agora inclui o contextPath se a url começar com /</li>
	<li>bugfix: agora é possível criar Controllers genéricos:

		{% highlight java %}
public class ClientesController extends GenericController<Cliente> {

}
public class GenericController<T> {
	public T mostra(Long id) {...} // a variável da view vai se chamar t
	public void adiciona(T obj) {...} // os parâmetros da requisição vão ser obj.campo 
}
		{% endhighlight %}
	</li>
	<li>você pode anotar sua classe controller com @Path, e todas as URIs dos métodos vão incluir o prefixo especificado.
  
		{% highlight java %}
@Resource
@Path("/prefixo")
public class MeuController {
	//URI: /prefixo/umMetodo
	public void umMetodo() {...}

	//URI: /prefixo/relativo
	@Path("relativo")
	public void pathRelativo() {...}

	//URI: /prefixo/absoluto
	@Path("/absoluto")
	public void pathAbsoluto() {...}
}
		{% endhighlight %}
	</li>
	<li>@Path agora aceita regexes: <em>@Path("/abc/{abc:a+b+c+}")</em> vai aceitar as URIs do tipo:
		
		{% highlight jsp %}
/abc/abc
/abc/aaaaabbcccc
/abc/abbc
		{% endhighlight %}

		ou seja, onde o parâmetro casa com a regex <em>a+b+c+</em>
	</li>
	<li>Foram criados atalhos na interface <em>Result</em> para as operações mais comuns:

		<ul>
			<li>result.forwardTo("/uma/uri") ==> result.use(page()).forward("/uma/uri");
			<li>result.forwardTo(ClienteController.class).lista() ==> result.use(logic()).forwardTo(ClienteController.class).lista();
			<li>result.redirectTo(ClienteController.class).lista() ==> result.use(logic()).redirectTo(ClienteController.class).lista();
			<li>result.of(ClienteController.class).lista() ==> result.use(page()).of(ClienteController.class).lista();
		</ul>

		Além disso, se o redirecionamento é para um método do mesmo controller, você pode usar:

	  	<ul>
		  	<li>result.forwardTo(this).lista() ==> result.use(logic()).forwardTo(this.getClass()).lista();</li>
			<li>result.redirectTo(this).lista() ==> result.use(logic()).redirectTo(this.getClass()).lista();</li>
			<li>result.of(this).lista() ==> result.use(page()).of(this.getClass()).lista();</li>
  		</ul>
	</li>

	<li>VRaptor agora scaneia por componentes e recursos em todo WEB-INF/classes automaticamente sem configuracao</li>
	<li>Suporte a Servlet 3.0, fazendo desnecessário configurar o filtro no web.xml (usando recurso de webfragments)</li>
	<li>Jars do spring atualizados (3.0.0) e do hibernate também, para os projetos de exemplo. Google Collections atualizada para 1.0</li>
	<li>Blank project atualizado para WTP mais novo e refletindo novidades do VR 3.1</li>
	<li>Blank project muito mais fácil de se importar no Eclipse WTP. Configurações e logging ajustados para melhor compreensão</li>
	<li>bugfix: mimetypes funcionam corretamente para browsers webkit, favorecendo html quando nao ha priorizacao</li>
	<li>bugfix: quando há erros de validação, os parâmetros da requisição são passados como String, não como mapas como antes. Isso previne ClassCastExceptions quando se usa taglibs, como a fmt:formatNumber.</li>
</ul>

<h3>3.0.2</h3>

<ul>
	<li>suporte a containers servlet 2.4, como Oracle Container 10.1.3.1</li>
	<li>bugfix: Results.referer() agora implementa View</li>
	<li>bugfix: content-type agora é exposto pelo File/InputStream Download</li>
	<li>removida chamadas a api de Java 6</li>
	<li>novos providers, baseados no Spring: HibernateCustomProvider e JPACustomProvider. Esses providers já registram os componentes opcionais do Hibernate ou da JPA</li>
	<li>bugfix: os converters agora não jogam exceções quando não existe um ResourceBundle configurado</li>
	<li>bugfix: o retorno do método agora é incluido no result quando acontece um forward</li>
	<li>bugfix: os parâmetros da requisição são mantidos quando acontece um erro de validação. </li>
	<li>bugfix: lançando exceção quando o paranamer não consegue achar os metadados dos parâmetros,
assim é possível se recuperar desse problema</li>
	<li>suporte inicial à serialização de objetos em xml e json:

		{% highlight java %}
result.use(Results.json()).from(meuObjeto).include(...).exclude(...).serialize();
result.use(Results.xml()).from(meuObjeto).include(...).exclude(...).serialize();
		{% endhighlight %}
	</li>
</ul>

<h3>3.0.1</h3>

<ul>
	<li>paranamer atualizado para versão 1.5 (Atualize seu jar!)</li>
	<li>jars separados em opcional e obrigatório no vraptor-core</li>
	<li>dependências estão explicadas no vraptor-core/libs/mandatory/dependencies.txt e no vraptor-core/libs/optional/dependencies.txt</li>
	<li>possibilidade de setar o character encoding da aplicação no web.xml através do context-param br.com.caelum.vraptor.encoding</li>
	<li>nova view: Referer view: result.use(Results.referer()).redirect();</li>
	<li>Escopo Flash:

		{% highlight java %}
result.include("umaChave", umObjeto);
result.use(logic()).redirectTo(UmController.class).umMetodo();
		{% endhighlight %}

		objetos incluidos no Result vão sobreviver até a próxima requisição quando acontecer um redirect
	</li>
	<li>@Path suporta vários valores (String -> String&#91;&#93;) ex @Path({"/client", "/Client"})</li>
	<li>Result.include agora retorna this para uma interface fluente (result.include(...).include(....))</li>
	<li>Melhor mensagem de exception quando não encontra o Http method requisitado  </li>
	<li>File Download registra automaticamente content-length</li>
	<li>Bug 117 resolvido: expondo null quando retorna null (antes era "ok")</li>
	<li>Bug 109 resolvido: se você tem um arquivo <em>/caminho/index.jsp</em>, você consegue acessá-lo agora via <em>/caminho/</em>, a menos que exista algum controller que trata essa URI</li>
	<li>Quando existe uma rota que consegue tratar a URI da requisição, mas que não aceita o HTTP method da requisição, o VRaptor vai retornar um HTTP status code 405 -> Method Not Allowed, ao invés do 404</li>
	<li>Uma grande refatoração na API interna de rotas</li>
</ul>

<h3>3.0.0</h3>

<ul>
	<li>ValidationError foi renomeado para ValidationException</li>
	<li>result.use(Results.http()) para setar headers e status codes do protocolo HTTP</li>
	<li>Correção de bugs</li>
	<li>documentação</li>
	<li>novo site</li>
</ul>

<h3>3.0.0-rc-1</h3>

<ul>
	<li>aplicação de exemplo: mydvds</li>
	<li>novo jeito de adicionar os componentes opcionais do VRaptor:

		{% highlight java %}
public class CustomProvider extends SpringProvider {
	
	@Override
	protected void registerCustomComponents(ComponentRegistry registry) {
		registry.registry(ComponenteOpcional.class, ComponenteOpcional.class);
	}
}
		{% endhighlight %}
	</li>
	<li>Utils: HibernateTransactionInterceptor e JPATransactionInterceptor</li>
	<li>Um exemplo completo de aplicação na documentação</li>
	<li>Docs em inglês</li>
</ul>

<h3>3.0.0-beta-5</h3>

<ul>
	<li>Novo jeito de fazer validações:

		{% highlight java %}
public void visualiza(Cliente cliente) {
	validator.checking(new Validations() { {
		that(cliente.getId() != null, "id", "id.deve.ser.preenchido");
	}});
	validator.onErrorUse(page()).of(ClientesController.class).list();
	
	//continua o metodo
}
		{% endhighlight %}
	</li>
	<li>UploadedFile.getFile() agora retorna InputStream</li>
	<li>EntityManagerCreator e EntityManagerFactoryCreator</li>
	<li>bugfixes</li>
</ul>

<h3>3.0.0-beta-4</h3>

<ul>
	<li>Novo result: result.use(page()).of(MeuController.class).minhaLogica() renderiza a view padrão (/WEB-INF/jsp/meu/minhaLogica.jsp) sem executar a minhaLogica</li>
	<li>Classes Mocks para testes: MockResult e MockValidator, para facilitar testes unitários das lógicas. Eles ignoram a maioria das chamadas e guardam parâmetros incluídos no result e erros de validação</li>
	<li>As URIs passadas para result.use(page()).forward(uri) e result.use(page()).redirect(uri) não podem ser URIs de lógicas, usem os forwards e redirects do result.use(logic())</li>
	<li>Os parâmetros passados para as URIs agora aceitam pattern-matching:

		<ul>
			<li>Automático: se temos a URI /clients/{client.id} e client.id é um Long, o parâmetro {client.id} só vai casar com números, ou seja, a URI /clients/42 casa, mas a uri /clients/random não casa.
			Isso funciona para todos os tipos numéricos, booleanos e enums, o vraptor vai restringir para os valores possíveis.</li>
			<li>Manual: no CustomRoutes você vai poder fazer:

				{% highlight java %}
routeFor("/clients/{client.id}").withParameter("client.id")
								.matching("\\d{1,4}")
								.is(ClienteController.class).mostra(null);
				{% endhighlight %}

				ou seja, pode restringir os valores para o determinado parâmetro via expressões regulares no método matching.
			</li>
		</ul>
	</li>

	<li>Converters para LocalDate e LocalTime do joda-time já vêm por padrão</li>
	<li>Quando o Spring é usado como IoC Provider, o VRaptor tenta buscar o spring da aplicação para usar como container pai. A busca é feita por padrão em um dos dois jeitos:

		<ul>
			<li>WebApplicationContextUtils.getWebApplicationContext(servletContext), para o caso em que você tem os listeners do Spring configurados.</li>
			<li>applicationContext.xml dentro do classpath</li>
		</ul>

		Se isso não for o suficiente você pode implementar a interface SpringLocator e disponbilizar o ApplicationContext do spring usado pela sua aplicação
	</li>
	<li>Utils:

		<ul>
			<li>SessionCreator e SessionFactoryCreator para disponbilizar a Session e o SessionFactory do hibernate para os componentes registrados.</li>
			<li>EncodingInterceptor, para mudar o encoding da sua aplicação.</li>
		</ul>
	</li>
	<li>correção de vários bugs e melhorias na documentação.</li>
</ul>

<h3>3.0.0-beta-3</h3>

<ul>
	<li>O Spring é o Provider de IoC padrão</li>
	<li>o applicationContext.xml no classpath é usado como configuração incial do spring, caso exista</li>
	<li>a documentação http://vraptor.caelum.com.br/documentacao está mais completa e atualizada</li>
	<li>pequenos bugs e otimizações</li>
</ul>
