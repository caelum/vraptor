---
title: Injeção de dependências
layout: page
section: 9
categories: [pt, docs]
---

O VRaptor está fortemente baseado no conceito de injeção de dependências uma vez que chega até mesmo a utilizar dessa ideia para juntar seus componentes internos.
O conceito básico por trás de Dependency Injection (DI) é que você não deve buscar aquilo que deseja acessar mas tudo o que deseja acessar deve ser fornecido para você.
Isso se traduz, por exemplo, na passagem de componentes ao construtor de seus controladores. Imagine que seu controlador de clientes necessita acessar um DAO de clientes. Sendo assim, especifique claramente essa necessidade:

{% highlight java %}
@Resource
public class ClienteController {
    private final ClienteDao dao;

    public ClienteController(ClienteDao dao) {
        this.dao = dao;
    }

    @Post
    public void adiciona(Cliente cliente) {
        this.dao.adiciona(cliente);
    }

}
{% endhighlight %}

E anote também o componente ClienteDao como sendo controlado pelo vraptor:

{% highlight java %}
@Component
public class ClienteDao {
}
{% endhighlight %}

A partir desse instante, o VRaptor fornecerá uma instância de ClienteDao para seu ClienteController sempre que precisar instanciá-lo. Vale lembrar que o VRaptor honrará o escopo de cada componente. Por exemplo, se ClienteDao fosse de escopo Session (@SessionScoped), seria criada uma única instância desse componente por sessão (note que é provavelmente errado usar um DAO no escopo de session, isto é um mero exemplo).

<h3>ComponentFactory</h3>

Em diversos momentos, queremos que nossos componentes recebam componentes de outras bibliotecas. Nesse caso não temos como alterar o código fonte da biblioteca para adicionar a anotação @Component (além de possíveis alterações requeridas na biblioteca).
O exemplo mais famoso envolve adquirir uma Session do Hibernate. Nesses casos, precisamos criar um componente que possui um único papel: fornecer instâncias de Session para os componentes que precisam dela.
O VRaptor possui uma interface chamada ComponentFactory que permite que suas classes possuam tal responsabilidade. Implementações dessa interface definem um único método. Veja o exemplo a seguir, que inicializa o Hibernate na construção e utiliza essa configuração para fornecer sessões para nosso projeto:

{% highlight java %}
@Component
@ApplicationScoped
public class SessionFactoryCreator implements ComponentFactory<SessionFactory> {

    private SessionFactory factory;

    @PostConstruct
    public void create() {
        factory = new AnnotationConfiguration().configure().buildSessionFactory();
    }

    public SessionFactory getInstance() {
        return factory;
    }

    @PreDestroy
    public void destroy() {
        factory.close();
    }

}

@Component
@RequestScoped
public class SessionCreator implements ComponentFactory<Session> {

    private final SessionFactory factory;
    private Session session;

    public SessionCreator(SessionFactory factory) {
        this.factory = factory;
    }

    @PostConstruct
    public void create() {
        this.session = factory.openSession();
    }

    public Session getInstance() {
        return session;
    }

    @PreDestroy
    public void destroy() {
        this.session.close();
    }
}
{% endhighlight %}

Essas implementações já estão disponíveis no código do VRaptor. Para usá-la veja o capítulo de utils.

<h3>Providers</h3>

Por trás dos panos, o VRaptor utiliza um container de injeção de dependências específico. Existem três containers suportados pelo VRaptor:

<ul>
<li><strong>Spring IoC:</strong> além da injeção de dependências, o Spring IoC possibilita usar qualquer outro componente do Spring integrado com o VRaptor, sem precisar de configurações</li>

<li><strong>Google Guice:</strong> um container mais leve e rápido, que possibilita um melhor controle na ligação das dependências, o uso da nova API de injeção de dependências do java: o javax.inject e funcionalidades de AOP.</li>

<li><strong>Pico container:</strong> um container leve e simples, pra quem não vai usar nada além de injeção de dependências.</li>
</ul>

Para selecionar quaisquer um desses containers, basta colocar seus respectivos JARs no classpath. Os JARs estão disponíveis na pasta lib/containers do ZIP do VRaptor.
Por padrão, os containers vão considerar apenas as classes abaixo da pasta WEB-INF/classes da sua aplicação, mas é possível, ainda, colocar classes anotadas da sua aplicação dentro de JARs, desde que esses JARs incluam as entradas de diretório ("Add directory entries" no eclipse, ou ant sem a opção "files only"). Para isso, é necessário usar o seguinte parâmetro no web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>br.com.pacote.dentro.do.jar</param-value>
</context-param>
{% endhighlight %}

<h3>Spring</h3>

Ao utilizar o Spring, você ganha todas as características e componentes prontos do Spring para uso dentro do VRaptor, isto é, todos os componentes que funcionam com o Spring DI/Ioc, funcionam com o VRaptor. Nesse caso, todas as anotações.
Para usar o Spring, adicione todos os JARs da pasta lib/containers/spring na sua aplicação.
O VRaptor vai usar suas configurações do Spring, caso você já o tenha configurado no seu projeto (os listeners e o applicationContext.xml no classpath). Caso o VRaptor não tenha encontrado sua configuração, ou você precise fazer alguma configuração mais avançada, você pode estender o provider do Spring:

{% highlight java %}
package br.com.suaaplicacao;
public class CustomProvider extends SpringProvider {

    @Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        registry.register(UmaClasse.class, ImplementacaoDessaClasse.class);
        //...
    }

    @Override
    protected ApplicationContext getParentApplicationContext(ServletContext context) {
        ApplicationContext context = //configure seu próprio application context
        return context;
    }
}
{% endhighlight %}

e pra usar o seu provider, coloque no web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.provider</param-name>
    <param-value>br.com.suaaplicacao.CustomProvider</param-value>
</context-param>
{% endhighlight %}

<h3>Google Guice</h3>

Para habilitar o Google Guice basta colocar os JARs que estão na pasta lib/containers/guice do ZIP do VRaptor.
Ao usar o Guice você pode escolher não usar a anotação @Component do VRaptor e usar as anotações do guice ou do javax.inject (@Inject, anotações de escopo) para controlar a instanciação dos seus componentes.
Se precisar fazer configurações mais específicas, crie uma classe que estende o provider do Guice:

{% highlight java %}
public class CustomProvider extends GuiceProvider {

    @Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        //binding só na UmaClasse
        registry.register(UmaClasse.class, ImplementacaoDessaClasse.class);

        //binding da classe e de todas as superclasses e interfaces
        registry.deepRegister(OutraClasse.class);
    }

    @Override
    protected Module customModule() {
        //você precisa instalar esse módulo se quiser
        //habilitar o método registerCustomComponents
        //e o classpath scanning
        final Module module = super.customModule();

        return new AbstractModule() {
           public void configure() {
                module.configure(binder());

                // binds personalizados do Guice
           }
        };
    }
}
{% endhighlight %}

e pra usar esse provider, coloque no web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.provider</param-name>
    <param-value>br.com.suaaplicacao.CustomProvider</param-value>
</context-param>
{% endhighlight %}

<h3>Pico Container</h3>

Para utilizar o Picocontainer como provider de sua aplicação, basta colocar os JARs da pasta lib/containers/picocontainer do zip do VRaptor no classpath da sua aplicação.
